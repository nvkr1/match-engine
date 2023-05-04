package bbattulga.matchengine.servicematchengine;

import bbattulga.matchengine.libmodel.consts.OrderSide;
import bbattulga.matchengine.libmodel.consts.OrderStatus;
import bbattulga.matchengine.libmodel.consts.OrderType;
import bbattulga.matchengine.libmodel.engine.LimitOrderEvent;
import bbattulga.matchengine.libmodel.engine.OrderBookPriceLevel;
import bbattulga.matchengine.libmodel.engine.OrderEvent;
import bbattulga.matchengine.libmodel.engine.output.OrderMatchOutput;
import bbattulga.matchengine.libmodel.engine.output.OrderOpenOutput;
import bbattulga.matchengine.servicematchengine.config.MatchEngineConfig;
import bbattulga.matchengine.servicematchengine.service.output.EngineOutputPublisherService;
import bbattulga.matchengine.servicematchengine.service.place.OrderBookService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class LimitOrderExecutorService {

    private final OrderBookService orderBookService;
    private final EngineOutputPublisherService engineOutputPublisherService;
    private final MatchEngineConfig config;

    public void executeLimitOrder(LimitOrderEvent execOrder) throws JsonProcessingException {
        if (execOrder.getSide() == OrderSide.BUY) {
            execLimitBuy(execOrder);
        } else if (execOrder.getSide() == OrderSide.SELL) {
            execLimitSell(execOrder);
        }
    }

    private void execLimitBuy(LimitOrderEvent execOrder) throws JsonProcessingException {
        final var asks = orderBookService.getAsks();
        if (asks.isEmpty()) {
            newRestingBid((OrderEvent) execOrder, OrderStatus.OPEN);
            return;
        }
        final var lowestAskPrice = asks.firstKey();
        if (execOrder.getPrice().compareTo(lowestAskPrice) < 0) {
            // no matching price
            newRestingBid((OrderEvent) execOrder, OrderStatus.OPEN);
            return;
        }
        final var matchingAsks = asks.subMap(lowestAskPrice, true, execOrder.getPrice(), true);
        if (matchingAsks.isEmpty()) {
            newRestingBid((OrderEvent) execOrder, OrderStatus.OPEN);
            return;
        }
        List<BigInteger> removePriceLevel = new ArrayList<>();
        for (final var matchingEntry: matchingAsks.entrySet()) {
            final var matchingPrice = matchingEntry.getKey();
            final var level = matchingEntry.getValue();
            final var restingOrders = level.getOrders();
            int orderIndex = 0;
            while (execOrder.getRemainingQty().compareTo(BigInteger.ZERO) > 0 && !restingOrders.isEmpty()) {
                final var restingOrderOriginal = restingOrders.get(orderIndex);
                if (restingOrderOriginal.getType() == OrderType.LIMIT) {
                    final var restingOrder = restingOrderOriginal.clone();
                    final var matchQty = execOrder.getRemainingQty().min(restingOrder.getRemainingQty());
                    final var matchTotal = matchingPrice.multiply(matchQty).divide(BigInteger.valueOf(config.getBaseTick()));
                    final var takerFeeQty = matchQty.multiply(config.getTakerFee()).divide(BigInteger.valueOf(100));
                    final var restingRemainingQty = restingOrder.getRemainingQty().subtract(matchQty);
                    final var restingRemainingTotal = matchingPrice.multiply(restingRemainingQty).divide(BigInteger.valueOf(config.getBaseTick()));
                    restingOrder.setRemainingQty(restingRemainingQty);
                    restingOrder.setRemainingTotal(restingRemainingTotal);
                    restingOrder.setFillQty(restingOrder.getFillQty().add(matchQty));
                    restingOrder.setFillTotal(restingOrder.getFillTotal().add(matchTotal));
                    final var execRemainingQty = execOrder.getRemainingQty().subtract(matchQty);
                    final var execRemainingTotal = matchingPrice.multiply(execRemainingQty).divide(BigInteger.valueOf(config.getBaseTick()));
                    execOrder.setRemainingQty(execRemainingQty);
                    execOrder.setRemainingTotal(execRemainingTotal);
                    execOrder.setExecQty(execOrder.getExecQty().add(matchQty));
                    execOrder.setExecTotal(execOrder.getExecTotal().add(matchTotal));
                    if (restingOrder.getRemainingQty().compareTo(BigInteger.ZERO) == 0) {
                        // resting order fulfilled
                        restingOrder.setStatus(OrderStatus.FULFILLED);
                        restingOrders.remove(orderIndex);
                    } else {
                        restingOrder.setStatus(OrderStatus.PARTIALLY_FILLED);
                        restingOrders.set(orderIndex, restingOrder);
                    }
                    boolean isExecOrderFulfilled = execOrder.getRemainingQty().compareTo(BigInteger.ZERO) == 0;
                    if (isExecOrderFulfilled) {
                        execOrder.setStatus(OrderStatus.FULFILLED);
                    } else {
                        execOrder.setStatus(OrderStatus.PARTIALLY_FILLED);
                    }
                    final var makerFeeQty = matchTotal.multiply(config.getMakerFee()).divide(BigInteger.valueOf(100));
                    final var matchOutput = OrderMatchOutput.builder()
                            .base(config.getBase())
                            .quote(config.getQuote())
                            .makerFee(makerFeeQty)
                            .takerFee(takerFeeQty)
                            .price(matchingPrice)
                            .qty(matchQty)
                            .total(matchTotal)
                            .execOrder((OrderEvent) execOrder)
                            .remainingOrder(restingOrder)
                            .utc(Instant.now().toEpochMilli())
                            .build();
                    engineOutputPublisherService.publish(matchOutput);
                    if (isExecOrderFulfilled) {
                        // exec order fulfilled
                        break;
                    }
                }
            }
            if (restingOrders.isEmpty()) {
                removePriceLevel.add(matchingPrice); // current price level orders fulfilled
            }
        }
        for (final var rp: removePriceLevel) {
            asks.remove(rp);
        }
        if (execOrder.getRemainingQty().compareTo(BigInteger.ZERO) > 0) {
            // exec order not fulfilled, save as resting order
            newRestingBid((OrderEvent) execOrder, OrderStatus.PARTIALLY_FILLED);
        }
    }

    private void execLimitSell(LimitOrderEvent execOrder) throws JsonProcessingException {
        final var bids = orderBookService.getBids();
        if (bids.isEmpty()) {
            newRestingAsk((OrderEvent) execOrder, OrderStatus.OPEN);
            return;
        }
        final var highestBidPrice = bids.lastKey();
        if (highestBidPrice.compareTo(execOrder.getPrice()) < 0) {
            // no matching price
            newRestingAsk((OrderEvent) execOrder, OrderStatus.OPEN);
            return;
        }
        final var matchingBids = bids.subMap(execOrder.getPrice(), true, highestBidPrice, true);
        if (matchingBids.isEmpty()) {
            newRestingAsk((OrderEvent) execOrder, OrderStatus.OPEN);
            return;
        }
        final var matchingSet = matchingBids.entrySet();
        List<BigInteger> removePriceLevel = new ArrayList<>();
        for (final var matchingEntry: matchingSet) {
            final var matchingPrice = matchingEntry.getKey();
            final var level = matchingEntry.getValue();
            final var restingOrders = level.getOrders();
            int orderIndex = 0;
            while (execOrder.getRemainingQty().compareTo(BigInteger.ZERO) > 0
                    && !restingOrders.isEmpty()) {
                final var restingOrderOriginal = restingOrders.get(orderIndex);
                if (restingOrderOriginal.getType() == OrderType.LIMIT) {
                    final var restingOrder = restingOrderOriginal.clone();
                    final var matchQty = execOrder.getRemainingQty().min(restingOrder.getRemainingQty());
                    final var makerFeeQty = matchQty.multiply(config.getMakerFee()).divide(BigInteger.valueOf(100));
                    final var matchTotal = matchingPrice.multiply(matchQty).divide(BigInteger.valueOf(config.getBaseTick()));
                    final var restingRemainingQty = restingOrder.getRemainingQty().subtract(matchQty);
                    final var restingRemainingTotal = matchingPrice.multiply(restingRemainingQty).divide(BigInteger.valueOf(config.getBaseTick()));
                    restingOrder.setRemainingQty(restingRemainingQty);
                    restingOrder.setRemainingTotal(restingRemainingTotal);
                    restingOrder.setFillQty(restingOrder.getFillQty().add(matchQty));
                    restingOrder.setFillTotal(restingOrder.getFillTotal().add(matchTotal));
                    final var execRemainingQty = execOrder.getRemainingQty().subtract(matchQty);
                    final var execRemainingTotal = matchingPrice.multiply(execRemainingQty).divide(BigInteger.valueOf(config.getBaseTick()));
                    execOrder.setRemainingQty(execRemainingQty);
                    execOrder.setRemainingTotal(execRemainingTotal);
                    execOrder.setExecQty(execOrder.getExecQty().add(matchQty));
                    execOrder.setExecTotal(execOrder.getExecTotal().add(matchTotal));
                    if (restingOrder.getRemainingQty().compareTo(BigInteger.ZERO) == 0) {
                        // resting order fulfilled
                        restingOrder.setStatus(OrderStatus.FULFILLED);
                        restingOrders.remove(orderIndex);
                    } else {
                        restingOrder.setStatus(OrderStatus.PARTIALLY_FILLED);
                        restingOrders.set(orderIndex, restingOrder);
                    }
                    boolean isExecOrderFulfilled = execOrder.getRemainingQty().compareTo(BigInteger.ZERO) == 0;
                    if (isExecOrderFulfilled) {
                        execOrder.setStatus(OrderStatus.FULFILLED);
                    } else {
                        execOrder.setStatus(OrderStatus.PARTIALLY_FILLED);
                    }
                    final var takerFee = matchTotal.multiply(config.getTakerFee()).divide(BigInteger.valueOf(100));
                    final var matchOutput = OrderMatchOutput.builder()
                            .base(config.getBase())
                            .quote(config.getQuote())
                            .makerFee(makerFeeQty)
                            .takerFee(takerFee)
                            .price(matchingPrice)
                            .total(matchTotal)
                            .qty(matchQty)
                            .execOrder((OrderEvent) execOrder)
                            .remainingOrder(restingOrder)
                            .utc(Instant.now().toEpochMilli())
                            .build();
                    engineOutputPublisherService.publish(matchOutput);
                    if (isExecOrderFulfilled) {
                        // exec order fulfilled
                        break;
                    }
                }
            }
            if (restingOrders.isEmpty()) {
                removePriceLevel.add(matchingPrice); // current price level orders fulfilled
            }
        }
        for (final var rp: removePriceLevel) {
            bids.remove(rp);
        }
        if (execOrder.getRemainingQty().compareTo(BigInteger.ZERO) > 0) {
            // exec order not fulfilled, save as resting order
            newRestingAsk((OrderEvent) execOrder, OrderStatus.PARTIALLY_FILLED);
        }
    }


    private void newRestingBid(OrderEvent bid, OrderStatus status) throws JsonProcessingException {
        final var bids = orderBookService.getBids();
        final var priceLevel = bids.getOrDefault(bid.getPrice(), new OrderBookPriceLevel());
        priceLevel.setPrice(bid.getPrice());
        priceLevel.getOrders().add(bid);
        bid.setStatus(status);
        bids.put(bid.getPrice(), priceLevel);
        publishOrderOpen(bid);
    }

    private void newRestingAsk(OrderEvent ask, OrderStatus status) throws JsonProcessingException {
        final var asks = orderBookService.getAsks();
        final var priceLevel = asks.getOrDefault(ask.getPrice(), new OrderBookPriceLevel());
        priceLevel.setPrice(ask.getPrice());
        priceLevel.getOrders().add(ask);
        ask.setStatus(status);
        asks.put(ask.getPrice(), priceLevel);
        publishOrderOpen(ask);
    }

    private void publishOrderOpen(OrderEvent order) throws JsonProcessingException {
        final var openOutput = OrderOpenOutput.builder()
                .orderId(order.getId())
                .base(config.getBase())
                .quote(config.getQuote())
                .price(order.getPrice())
                .side(order.getSide())
                .qty(order.getQty())
                .total(order.getTotal())
                .execQty(order.getExecQty())
                .execTotal(order.getExecTotal())
                .remainingQty(order.getRemainingQty())
                .remainingTotal(order.getRemainingTotal())
                .utc(order.getUtc())
                .build();
        engineOutputPublisherService.publish(openOutput);
    }

}
