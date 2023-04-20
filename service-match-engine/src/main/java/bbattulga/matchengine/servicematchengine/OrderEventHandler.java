package bbattulga.matchengine.servicematchengine;

import bbattulga.matchengine.libmodel.engine.OrderEvent;
import bbattulga.matchengine.servicematchengine.service.executor.CancelOrderExecutorService;
import bbattulga.matchengine.servicematchengine.service.executor.LimitOrderExecutorService;
import com.lmax.disruptor.EventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventHandler {

    private final LimitOrderExecutorService limitOrderExecutorService;
    private final CancelOrderExecutorService cancelOrderExecutorService;

    @SuppressWarnings("unchecked")
    public EventHandler<OrderEvent>[] getEventHandler() {
        final EventHandler<OrderEvent> eventHandler = (orderEvent, sequence, endOfBatch) -> {
            log.info("handle order orderEvent");
            switch (orderEvent.getType()) {
                case LIMIT:
                    limitOrderExecutorService.executeLimitOrder(orderEvent);
                    break;
                case CANCEL:
                    cancelOrderExecutorService.executeCancelOrder(orderEvent);
                    break;
                default:
                    throw new Exception("Invalid order orderEvent type");
            }
        };
        return new EventHandler[] { eventHandler };
    }
}
