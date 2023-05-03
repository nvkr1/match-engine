package bbattulga.matchengine.servicematchengine.service.place;

import bbattulga.matchengine.libmodel.consts.OrderType;
import bbattulga.matchengine.libmodel.engine.LimitOrderEvent;
import bbattulga.matchengine.libmodel.engine.OrderEvent;
import bbattulga.matchengine.libmodel.engine.http.request.LimitOrderRequest;
import bbattulga.matchengine.libmodel.exception.BadParameterException;
import bbattulga.matchengine.servicematchengine.config.MatchEngineConfig;
import com.lmax.disruptor.RingBuffer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class LimitOrderPlaceService {

    private final MatchEngineConfig config;
    private final RingBuffer<OrderEvent> ringBuffer;

    public void placeLimitOrder(LimitOrderRequest order) {
        checkTotal(order);
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

    private void checkTotal(LimitOrderRequest request) {
        final var qtyScaled = new BigDecimal(request.getQty()).scaleByPowerOfTen(-1*config.getBaseScale());
        final var computedTotal = new BigDecimal(request.getPrice()).multiply(qtyScaled);
        try {
            if (computedTotal.toBigIntegerExact().compareTo(request.getTotal()) != 0) {
                throw new BadParameterException("total-not-equal");
            }
        } catch (ArithmeticException e) {
            throw new BadParameterException("total-invalid");
        }
        final var computedTotalScaled = computedTotal.scaleByPowerOfTen(-1*config.getQuoteScale());
        final var requestTotalScaled = new BigDecimal(request.getTotal()).scaleByPowerOfTen(-1*config.getQuoteScale());
        if (computedTotalScaled.compareTo(requestTotalScaled) != 0) {
            throw new BadParameterException("total-not-equal");
        }
    }

}
