package bbattulga.matchengine.servicematchengine.service.queue;

import bbattulga.matchengine.libmodel.consts.OrderType;
import bbattulga.matchengine.libmodel.engine.LimitOrderEvent;
import bbattulga.matchengine.libmodel.engine.OrderEvent;
import bbattulga.matchengine.servicematchengine.dto.request.LimitOrderRequest;
import com.lmax.disruptor.RingBuffer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LimitOrderPlaceService {

    private final RingBuffer<OrderEvent> ringBuffer;

    public void placeLimitOrder(LimitOrderRequest order) {
        long sequenceId = ringBuffer.next();
        final var orderEvent = ringBuffer.get(sequenceId);
        orderEvent.setType(OrderType.LIMIT);
        final var limitOrder = (LimitOrderEvent) orderEvent;
        limitOrder.setId(order.getId());
        limitOrder.setSide(order.getSide());
        limitOrder.setUid(order.getUid());
        limitOrder.setQty(order.getQty());
        limitOrder.setPrice(order.getPrice());
        limitOrder.setUtc(order.getUtc());
        ringBuffer.publish(sequenceId);
    }

}
