package bbattulga.matchengine.servicematchengine.service.executor;

import bbattulga.matchengine.libmodel.consts.OrderSide;
import bbattulga.matchengine.libmodel.consts.OrderType;
import bbattulga.matchengine.libmodel.engine.LimitOrderEvent;
import bbattulga.matchengine.libmodel.engine.OrderBookPriceLevel;
import bbattulga.matchengine.libmodel.engine.OrderEvent;
import bbattulga.matchengine.servicematchengine.service.OrderBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
@RequiredArgsConstructor
public class LimitOrderExecutorService {

    private final OrderBookService orderBookService;

    // TODO:: implement limit order execution
    public void executeLimitOrder(LimitOrderEvent execOrder) {
        if (execOrder.getSide() == OrderSide.BUY) {
            execLimitBuy(execOrder);
        } else if (execOrder.getSide() == OrderSide.SELL) {
            execLimitSell(execOrder);
        }
    }

    private void execLimitBuy(LimitOrderEvent execOrder) {
        final var asks = orderBookService.getAsks();
        if (asks.isEmpty()) {
            newRestingBid((OrderEvent) execOrder);
            return;
        }
        final var lowestAskPrice = asks.firstKey();
        final var matchingAsks = asks.subMap(lowestAskPrice, true, execOrder.getPrice(), true);
        if (matchingAsks.isEmpty()) {
            newRestingBid((OrderEvent) execOrder);
            return;
        }
        for (final var matchingEntry: matchingAsks.entrySet()) {
            final var matchingPrice = matchingEntry.getKey();
            final var level = matchingEntry.getValue();
            final var restingOrders = level.getOrders();
            int orderIndex = 0;
            while (execOrder.getQty().compareTo(BigInteger.ZERO) > 0 && !restingOrders.isEmpty()) {
                final var restingOrderOriginal = restingOrders.get(orderIndex);
                if (restingOrderOriginal.getType() == OrderType.LIMIT) {
                    final var restingOrder = restingOrderOriginal.clone();
                    final var matchQty = execOrder.getQty().min(restingOrder.getQty());
                    restingOrder.setQty(restingOrder.getQty().subtract(matchQty));
                    execOrder.setQty(execOrder.getQty().subtract(matchQty));
                    if (restingOrder.getQty().compareTo(BigInteger.ZERO) == 0) {
                        // resting order fulfilled
                        restingOrders.remove(orderIndex);
                    } else {
                        restingOrders.set(orderIndex, restingOrder);
                    }
                    if (execOrder.getQty().compareTo(BigInteger.ZERO) == 0) {
                        // exec order fulfilled
                        break;
                    }
                }
            }
            if (restingOrders.isEmpty()) {
                asks.remove(matchingPrice); // current price level orders fulfilled
            }
            if (execOrder.getQty().compareTo(BigInteger.ZERO) > 0) {
                // exec order not fulfilled, save as resting order
                newRestingBid((OrderEvent) execOrder);
                break;
            }
        }
    }

    private void execLimitSell(LimitOrderEvent execOrder) {
        final var bids = orderBookService.getBids();
        if (bids.isEmpty()) {
            newRestingAsk((OrderEvent) execOrder);
            return;
        }
        final var highestBidPrice = bids.lastKey();
        final var matchingBids = bids.subMap(execOrder.getPrice(), true, highestBidPrice, true);
        if (matchingBids.isEmpty()) {
            newRestingAsk((OrderEvent) execOrder);
            return;
        }
        final var matchingSet = matchingBids.entrySet();
        for (final var matchingEntry: matchingSet) {
            final var matchingPrice = matchingEntry.getKey();
            final var level = matchingEntry.getValue();
            final var restingOrders = level.getOrders();
            int orderIndex = 0;
            while (execOrder.getQty().compareTo(BigInteger.ZERO) > 0
                    && !restingOrders.isEmpty()) {
                final var restingOrderOriginal = restingOrders.get(orderIndex);
                if (restingOrderOriginal.getType() == OrderType.LIMIT) {
                    final var restingOrder = restingOrderOriginal.clone();
                    final var matchQty = execOrder.getQty().min(restingOrder.getQty());
                    restingOrder.setQty(restingOrder.getQty().subtract(matchQty));
                    execOrder.setQty(execOrder.getQty().subtract(matchQty));
                    if (restingOrder.getQty().compareTo(BigInteger.ZERO) == 0) {
                        // resting order fulfilled
                        restingOrders.remove(orderIndex);
                    } else {
                        restingOrders.set(orderIndex, restingOrder);
                    }
                    if (execOrder.getQty().compareTo(BigInteger.ZERO) == 0) {
                        // exec order fulfilled
                        break;
                    }
                }
            }
            if (restingOrders.isEmpty()) {
                bids.remove(matchingPrice); // current price level orders fulfilled
            }
            if (execOrder.getQty().compareTo(BigInteger.ZERO) > 0) {
                // exec order not fulfilled, save as resting order
                newRestingAsk((OrderEvent) execOrder);
                break;
            }
        }
    }


    private void newRestingBid(OrderEvent bid) {
        final var bids = orderBookService.getBids();
        final var priceLevel = bids.getOrDefault(bid.getPrice(), new OrderBookPriceLevel());
        priceLevel.setPrice(bid.getPrice());
        priceLevel.getOrders().add(bid);
        bids.put(bid.getPrice(), priceLevel);
    }

    private void newRestingAsk(OrderEvent ask) {
        final var asks = orderBookService.getAsks();
        final var priceLevel = asks.getOrDefault(ask.getPrice(), new OrderBookPriceLevel());
        priceLevel.setPrice(ask.getPrice());
        priceLevel.getOrders().add(ask);
        asks.put(ask.getPrice(), priceLevel);
    }
}
