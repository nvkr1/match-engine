package bbattulga.matchengine.serviceengineconsumer.consumer;

import bbattulga.matchengine.libmodel.consts.OrderStatus;
import bbattulga.matchengine.libmodel.engine.output.OrderCancelOutput;
import bbattulga.matchengine.libmodel.exception.BadParameterException;
import bbattulga.matchengine.libmodel.jpa.entity.Asset;
import bbattulga.matchengine.libmodel.jpa.entity.Order;
import bbattulga.matchengine.libmodel.jpa.entity.Pair;
import bbattulga.matchengine.libmodel.jpa.repository.AssetRepository;
import bbattulga.matchengine.libmodel.jpa.repository.OrderRepository;
import bbattulga.matchengine.libmodel.jpa.repository.PairRepository;
import bbattulga.matchengine.libservice.orderlog.OrderLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderCancelConsumer {

    private final OrderRepository orderRepository;
    private final OrderLogService orderLogService;
    private final PairRepository pairRepository;
    private final AssetRepository assetRepository;

    @Transactional
    public void consume(OrderCancelOutput output) throws BadParameterException {
        final var orderOpt = orderRepository.findByOrderCode(UUID.fromString(output.getOrderId()));
        final var baseAsset = assetRepository.findBySymbolAndStatus(output.getBase(), Asset.Status.ACTIVE).orElseThrow(() -> new BadParameterException("asset-not-found"));
        final var quoteAsset = assetRepository.findBySymbolAndStatus(output.getQuote(), Asset.Status.ACTIVE).orElseThrow(() -> new BadParameterException("asset-not-found"));
        final var pair = pairRepository.findByBaseAssetIdAndQuoteAssetIdAndStatus(baseAsset.getAssetId(), quoteAsset.getAssetId(), Pair.Status.ACTIVE).orElseThrow(() -> new BadParameterException("pair-not-found"));
        if (orderOpt.isPresent()) {
            updateOrder(orderOpt.get(), output);
        } else {
            saveNewOrder(output, pair);
        }
    }

    private Order saveNewOrder(OrderCancelOutput output, Pair pair) {
        final var now = LocalDateTime.now();
        final var newOrder = new Order();
        newOrder.setOrderCode(UUID.fromString(output.getOrderId()));
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
        newOrder.setNs(output.getNs());
        newOrder.setExecUtc(output.getUtc());
        orderRepository.save(newOrder);
        orderLogService.saveOrderLog(newOrder);
        return newOrder;
    }

    private Order updateOrder(Order order, OrderCancelOutput output) {
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
        order.setNs(output.getNs());
        order.setExecUtc(output.getUtc());
        orderRepository.save(order);
        orderLogService.saveOrderLog(order);
        return order;
    }
}
