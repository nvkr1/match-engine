package bbattulga.matchengine.servicematchengine.service;

import bbattulga.matchengine.servicematchengine.dto.engine.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;

@Service
@RequiredArgsConstructor
public class QueueService {

    private final LinkedBlockingQueue<Order> queue = new LinkedBlockingQueue<>();

    public void placeOrder(Order order) throws InterruptedException {
        queue.put(order);
    }

    public Optional<Order> takeOrder() throws InterruptedException {
        if (queue.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(queue.take());
    }
}
