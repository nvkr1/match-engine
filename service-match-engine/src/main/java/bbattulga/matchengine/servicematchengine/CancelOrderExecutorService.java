package bbattulga.matchengine.servicematchengine;

import bbattulga.matchengine.libmodel.consts.OrderSide;
import bbattulga.matchengine.libmodel.engine.CancelOrderEvent;
import bbattulga.matchengine.libmodel.engine.OrderBookPriceLevel;
import bbattulga.matchengine.libmodel.exception.BadParameterException;
import bbattulga.matchengine.libmodel.engine.output.OrderCancelOutput;
import bbattulga.matchengine.servicematchengine.config.MatchEngineConfig;
import bbattulga.matchengine.servicematchengine.service.output.EngineOutputPublisherService;
import bbattulga.matchengine.servicematchengine.service.place.OrderBookService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.OptionalInt;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class CancelOrderExecutorService {

    private final OrderBookService orderBookService;
    private final MatchEngineConfig config;
    private final EngineOutputPublisherService engineOutputPublisherService;

    // TODO:: implement cancel order
    public void executeCancelOrder(CancelOrderEvent cancelOrder) throws BadParameterException, JsonProcessingException {
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
        } else {
            throw new BadParameterException("invalid-order-side");
        }
        final var cancelOutput = OrderCancelOutput.builder()
                .orderId(cancelOrder.getId())
                .base(config.getBase())
                .quote(config.getQuote())
                .build();
        engineOutputPublisherService.publish(cancelOutput);
    }
}
