package bbattulga.matchengine.libservice.orderlog;

import bbattulga.matchengine.libmodel.jpa.entity.Order;
import bbattulga.matchengine.libmodel.jpa.entity.OrderLog;
import bbattulga.matchengine.libmodel.jpa.repository.OrderLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderLogService {

    private final OrderLogRepository orderLogRepository;

    @Transactional
    public void saveOrderLog(Order order) {
        final var log = new OrderLog();
        log.setOrderId(order.getOrderId());
        log.setPairId(order.getPairId());
        log.setSide(order.getSide());
        log.setStatus(order.getStatus());
        log.setUid(order.getUid());
        log.setPrice(order.getPrice());
        log.setQty(order.getQty());
        log.setTotal(order.getTotal());
        log.setUtc(order.getUtc());
        log.setExecQty(order.getExecQty());
        log.setExecTotal(order.getExecTotal());
        log.setFillQty(order.getFillQty());
        log.setFillTotal(order.getFillTotal());
        log.setRemainingQty(order.getRemainingQty());
        log.setRemainingTotal(order.getRemainingTotal());
        log.setCreatedAt(LocalDateTime.now());
        orderLogRepository.save(log);
    }
}
