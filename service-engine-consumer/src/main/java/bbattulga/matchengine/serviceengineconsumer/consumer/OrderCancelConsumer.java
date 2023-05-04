package bbattulga.matchengine.serviceengineconsumer.consumer;

import bbattulga.matchengine.libmodel.consts.OrderStatus;
import bbattulga.matchengine.libmodel.engine.output.OrderCancelOutput;
import bbattulga.matchengine.libmodel.exception.BadParameterException;
import bbattulga.matchengine.libmodel.jpa.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderCancelConsumer {

    private OrderRepository orderRepository;

    @Transactional
    public void consume(OrderCancelOutput cancelOrder) throws BadParameterException {
        final var orderOpt = orderRepository.findByOrderCode(UUID.fromString(cancelOrder.getOrderId()));
        if (orderOpt.isPresent()) {
            final var order = orderOpt.get();
            order.setStatus(OrderStatus.CANCELLED);
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);
        }
    }
}
