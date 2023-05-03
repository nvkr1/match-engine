package bbattulga.matchengine.serviceengineconsumer.consumer;

import bbattulga.matchengine.libmodel.consts.OrderStatus;
import bbattulga.matchengine.libmodel.exception.BadParameterException;
import bbattulga.matchengine.libmodel.jpa.repository.OrderRepository;
import bbattulga.matchengine.libmodel.engine.output.OrderOpenOutput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderOpenConsumer {

    private final OrderRepository orderRepository;

    @Transactional
    public void consume(OrderOpenOutput openOrder) throws BadParameterException {
        final var order = orderRepository.findById(UUID.fromString(openOrder.getOrderId())).orElseThrow(() -> new BadParameterException("order-not-found"));
        order.setStatus(OrderStatus.OPEN);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }
}
