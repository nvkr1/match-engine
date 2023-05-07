package bbattulga.matchengine.servicematchengine;

import bbattulga.matchengine.libmodel.consts.OrderSide;
import bbattulga.matchengine.libmodel.consts.OrderStatus;
import bbattulga.matchengine.libmodel.engine.CancelOrderEvent;
import bbattulga.matchengine.libmodel.engine.OrderEvent;
import bbattulga.matchengine.libmodel.engine.output.OrderCancelOutput;
import bbattulga.matchengine.libmodel.exception.BadParameterException;
import bbattulga.matchengine.servicematchengine.config.MatchEngineConfig;
import bbattulga.matchengine.servicematchengine.service.place.OrderBookService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.OptionalInt;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class CancelOrderExecutorService {

    private final OrderBookService orderBookService;
    private final MatchEngineConfig config;
    private final SequentialOutputService sequentialOutputService;
    private boolean isPublish;

    public void executeCancelOrder(CancelOrderEvent cancelOrder, long nsStart, boolean isPublish) throws BadParameterException, JsonProcessingException {
        this.isPublish = isPublish;
        OrderEvent cancelledOrder = null;
        if (cancelOrder.getSide() == OrderSide.BUY) {
            final var bids = orderBookService.getBids();
            final var level = bids.get(cancelOrder.getPrice());
            if (level == null) {
                // TODO:: publish reject
                return;
            }
            final var restingOrders = level.getOrders();
            OptionalInt optIdx = IntStream.range(0, restingOrders.size()).filter((idx) -> restingOrders.get(idx).getId().equals(cancelOrder.getId())).findFirst();
            if (optIdx.isPresent()) {
                cancelledOrder = restingOrders.get(optIdx.getAsInt());
                restingOrders.remove(optIdx.getAsInt());
            }
            if (restingOrders.isEmpty()) {
                bids.remove(level.getPrice());
            }
        } else if (cancelOrder.getSide() == OrderSide.SELL) {
            final var asks = orderBookService.getAsks();
            final var level = asks.get(cancelOrder.getPrice());
            if (level == null) {
                // TODO:: publish reject
                return;
            }
            final var restingOrders = level.getOrders();
            OptionalInt optIdx = IntStream.range(0, restingOrders.size()).filter((idx) -> restingOrders.get(idx).getId().equals(cancelOrder.getId())).findFirst();
            if (optIdx.isPresent()) {
                cancelledOrder = restingOrders.get(optIdx.getAsInt());
                restingOrders.remove(optIdx.getAsInt());
            }
            if (restingOrders.isEmpty()) {
                asks.remove(level.getPrice());
            }
        } else {
            throw new BadParameterException("invalid-order-side");
        }
        if (isPublish && cancelledOrder != null) {
            final var cancelNs = System.nanoTime() - nsStart;
            final var cancelOutput = OrderCancelOutput.builder()
                    .orderId(cancelOrder.getId())
                    .uid(cancelledOrder.getUid())
                    .base(config.getBase())
                    .quote(config.getQuote())
                    .price(cancelledOrder.getPrice())
                    .qty(cancelledOrder.getQty())
                    .total(cancelledOrder.getTotal())
                    .execQty(cancelledOrder.getExecQty())
                    .execTotal(cancelledOrder.getExecTotal())
                    .fillQty(cancelledOrder.getFillQty())
                    .fillTotal(cancelledOrder.getFillTotal())
                    .remainingQty(cancelledOrder.getRemainingQty())
                    .remainingTotal(cancelledOrder.getRemainingTotal())
                    .ns(cancelNs)
                    .utc(Instant.now().toEpochMilli())
                    .status(OrderStatus.CANCELLED)
                    .build();
            sequentialOutputService.publish(cancelOutput);
        }
    }
}
