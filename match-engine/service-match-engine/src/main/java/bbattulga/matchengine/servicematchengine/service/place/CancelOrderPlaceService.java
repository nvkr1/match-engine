package bbattulga.matchengine.servicematchengine.service.place;

import bbattulga.matchengine.libmodel.consts.OrderType;
import bbattulga.matchengine.libmodel.engine.CancelOrderEvent;
import bbattulga.matchengine.libmodel.engine.OrderEvent;
import bbattulga.matchengine.libmodel.engine.http.request.CancelOrderRequest;
import com.lmax.disruptor.RingBuffer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CancelOrderPlaceService {

    private final RingBuffer<OrderEvent> ringBuffer;

    public void placeCancelOrder(CancelOrderRequest request) {
        long sequenceId = ringBuffer.next();
        final var orderEvent =ringBuffer.get(sequenceId);
        orderEvent.setType(OrderType.CANCEL);
        final var cancelOrderEvent = (CancelOrderEvent) orderEvent;
        cancelOrderEvent.setId(request.getId());
        cancelOrderEvent.setPrice(request.getPrice());
        cancelOrderEvent.setSide(request.getSide());
        ringBuffer.publish(sequenceId);
    }

}
