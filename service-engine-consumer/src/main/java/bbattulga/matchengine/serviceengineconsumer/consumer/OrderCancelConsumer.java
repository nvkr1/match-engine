package bbattulga.matchengine.serviceengineconsumer.consumer;

import bbattulga.matchengine.libmodel.consts.OrderStatus;
import bbattulga.matchengine.libmodel.engine.output.OrderCancelOutput;
import bbattulga.matchengine.libmodel.exception.BadParameterException;
import bbattulga.matchengine.libmodel.jpa.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderCancelConsumer {

    private OrderRepository orderRepository;

    @Transactional
    public void consume(OrderCancelOutput cancelOrder) throws BadParameterException {
        final var order = orderRepository.findById(UUID.fromString(cancelOrder.getOrderId())).orElseThrow(() -> new BadParameterException("order-not-found"));
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }
}
