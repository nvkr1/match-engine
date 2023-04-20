package bbattulga.matchengine.servicematchengine.service.executor;

import bbattulga.matchengine.libmodel.consts.OrderSide;
import bbattulga.matchengine.libmodel.engine.CancelOrderEvent;
import bbattulga.matchengine.libmodel.engine.OrderBookPriceLevel;
import bbattulga.matchengine.servicematchengine.service.OrderBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.OptionalInt;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class CancelOrderExecutorService {

    private final OrderBookService orderBookService;

    // TODO:: implement cancel order
    public void executeCancelOrder(CancelOrderEvent cancelOrder) {
        if (cancelOrder.getSide() == OrderSide.BUY) {
            final var bids = orderBookService.getBids();
            final var level = bids.getOrDefault(cancelOrder.getPrice(), new OrderBookPriceLevel());
            final var restingOrders = level.getOrders();
            OptionalInt optIdx = IntStream.range(0, restingOrders.size()).filter((idx) -> restingOrders.get(idx).getId().equals(cancelOrder.getId())).findFirst();
            if (optIdx.isPresent()) {
                restingOrders.remove(optIdx.getAsInt());
            }
            if (restingOrders.isEmpty()) {
                bids.remove(level.getPrice());
            }
        } else if (cancelOrder.getSide() == OrderSide.SELL) {
            final var asks = orderBookService.getAsks();
            final var level = asks.getOrDefault(cancelOrder.getPrice(), new OrderBookPriceLevel());
            final var restingOrders = level.getOrders();
            OptionalInt optIdx = IntStream.range(0, restingOrders.size()).filter((idx) -> restingOrders.get(idx).getId().equals(cancelOrder.getId())).findFirst();
            if (optIdx.isPresent()) {
                restingOrders.remove(optIdx.getAsInt());
            }
            if (restingOrders.isEmpty()) {
                asks.remove(level.getPrice());
            }
        }
    }
}
