package bbattulga.matchengine.servicematchengine.service.place;

import bbattulga.matchengine.libmodel.consts.OrderType;
import bbattulga.matchengine.libmodel.engine.MarketOrderEvent;
import bbattulga.matchengine.libmodel.engine.OrderEvent;
import bbattulga.matchengine.libmodel.engine.http.request.MarketOrderRequest;
import com.lmax.disruptor.RingBuffer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarketOrderPlaceService {

    private final RingBuffer<OrderEvent> ringBuffer;

    public void placeMarketOrder(MarketOrderRequest order) {
        long sequenceId = ringBuffer.next();
        final var orderEvent = ringBuffer.get(sequenceId);
        orderEvent.setType(OrderType.MARKET);
        final var marketOrder = (MarketOrderEvent) orderEvent;
        marketOrder.setId(order.getId());
        marketOrder.setSide(order.getSide());
        marketOrder.setUid(order.getUid());
        marketOrder.setQty(order.getQty());
        marketOrder.setPrice(order.getPrice());
        marketOrder.setUtc(order.getUtc());
        ringBuffer.publish(sequenceId);
    }

}
