package bbattulga.matchengine.serviceengineconsumer.consumer;

import bbattulga.matchengine.libmodel.engine.OrderEvent;
import bbattulga.matchengine.libmodel.engine.output.OrderMatchOutput;
import bbattulga.matchengine.libmodel.exception.BadParameterException;
import bbattulga.matchengine.libmodel.jpa.entity.Asset;
import bbattulga.matchengine.libmodel.jpa.entity.Match;
import bbattulga.matchengine.libmodel.jpa.entity.Order;
import bbattulga.matchengine.libmodel.jpa.entity.Pair;
import bbattulga.matchengine.libmodel.jpa.repository.AssetRepository;
import bbattulga.matchengine.libmodel.jpa.repository.MatchRepository;
import bbattulga.matchengine.libmodel.jpa.repository.OrderRepository;
import bbattulga.matchengine.libmodel.jpa.repository.PairRepository;
import bbattulga.matchengine.libservice.orderlog.OrderLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchConsumer {

    private final OrderRepository orderRepository;
    private final AssetRepository assetRepository;
    private final PairRepository pairRepository;
    private final MatchRepository matchRepository;
    private final OrderLogService orderLogService;

    @Transactional
    public void consume(OrderMatchOutput matchOutput) throws BadParameterException {
        final var baseAsset = assetRepository.findBySymbolAndStatus(matchOutput.getBase(), Asset.Status.ACTIVE).orElseThrow(() -> new BadParameterException("asset-not-found"));
        final var quoteAsset = assetRepository.findBySymbolAndStatus(matchOutput.getQuote(), Asset.Status.ACTIVE).orElseThrow(() -> new BadParameterException("asset-not-found"));
        final var pair = pairRepository.findByBaseAssetIdAndQuoteAssetIdAndStatus(baseAsset.getAssetId(), quoteAsset.getAssetId(), Pair.Status.ACTIVE).orElseThrow(() -> new BadParameterException("pair-not-found"));
        final var execOrderOpt = orderRepository.findByOrderCode(UUID.fromString(matchOutput.getExecOrder().getId()));
        final Order execOrder = execOrderOpt.map(order -> updateOrder(order, matchOutput.getExecOrder(), matchOutput.getNs())).orElseGet(() -> saveNewOrder(matchOutput.getExecOrder(), pair, matchOutput.getNs()));
        final var remainingOrderOpt = orderRepository.findByOrderCode(UUID.fromString(matchOutput.getRemainingOrder().getId()));
        Order remainingOrder = remainingOrderOpt.map(order -> updateOrder(order, matchOutput.getRemainingOrder(), matchOutput.getNs())).orElseGet(() -> saveNewOrder(matchOutput.getRemainingOrder(), pair, matchOutput.getNs()));
        final var match = new Match();
        match.setExecOrderId(execOrder.getOrderId());
        match.setRemainingOrderId(remainingOrder.getOrderId());
        match.setMakerFee(matchOutput.getMakerFee());
        match.setTakerFee(matchOutput.getTakerFee());
        match.setPrice(matchOutput.getPrice());
        match.setQty(matchOutput.getQty());
        match.setTotal(matchOutput.getTotal());
        match.setPairId(pair.getPairId());
        match.setBaseAssetId(baseAsset.getAssetId());
        match.setQuoteAssetId(quoteAsset.getAssetId());
        match.setUtc(matchOutput.getUtc());
        match.setCreatedAt(LocalDateTime.now());
        match.setNs(matchOutput.getNs());
        matchRepository.save(match);
        // update pair last price
        pair.setLastPrice(match.getPrice());
        pair.setUpdatedAt(LocalDateTime.now());
        pairRepository.save(pair);
    }

    private Order saveNewOrder(OrderEvent output, Pair pair, long ns) {
        final var now = LocalDateTime.now();
        final var newOrder = new Order();
        newOrder.setOrderCode(UUID.fromString(output.getId()));
        newOrder.setStatus(output.getStatus());
        newOrder.setUid(UUID.fromString(output.getUid()));
        newOrder.setPairId(pair.getPairId());
        newOrder.setType(output.getType());
        newOrder.setSide(output.getSide());
        newOrder.setPrice(output.getPrice());
        newOrder.setQty(output.getQty());
        newOrder.setTotal(output.getTotal());
        newOrder.setUtc(output.getUtc());
        newOrder.setExecQty(output.getExecQty());
        newOrder.setExecTotal(output.getExecTotal());
        newOrder.setFillQty(output.getFillQty());
        newOrder.setFillTotal(output.getFillTotal());
        newOrder.setRemainingQty(output.getRemainingQty());
        newOrder.setRemainingTotal(output.getRemainingTotal());
        newOrder.setCreatedAt(now);
        newOrder.setUpdatedAt(now);
        newOrder.setNs(ns);
        newOrder.setExecUtc(output.getUtc());
        orderRepository.save(newOrder);
        orderLogService.saveOrderLog(newOrder);
        return newOrder;
    }

    private Order updateOrder(Order order, OrderEvent output, long ns) {
        order.setUpdatedAt(LocalDateTime.now());
        order.setStatus(output.getStatus());
        order.setPrice(output.getPrice());
        order.setQty(output.getQty());
        order.setTotal(output.getTotal());
        order.setExecQty(output.getExecQty());
        order.setExecTotal(output.getExecTotal());
        order.setFillQty(output.getFillQty());
        order.setFillTotal(output.getFillTotal());
        order.setRemainingQty(output.getRemainingQty());
        order.setRemainingTotal(output.getRemainingTotal());
        order.setNs(ns);
        order.setExecUtc(output.getUtc());
        orderRepository.save(order);
        orderLogService.saveOrderLog(order);
        return order;
    }
}
