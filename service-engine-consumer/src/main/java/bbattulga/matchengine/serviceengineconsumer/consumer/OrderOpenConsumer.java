package bbattulga.matchengine.serviceengineconsumer.consumer;

import bbattulga.matchengine.libmodel.consts.OrderStatus;
import bbattulga.matchengine.libmodel.consts.OrderType;
import bbattulga.matchengine.libmodel.engine.output.OrderOpenOutput;
import bbattulga.matchengine.libmodel.exception.BadParameterException;
import bbattulga.matchengine.libmodel.exception.PairNotFoundException;
import bbattulga.matchengine.libmodel.jpa.entity.Order;
import bbattulga.matchengine.libmodel.jpa.entity.Pair;
import bbattulga.matchengine.libmodel.jpa.repository.OrderRepository;
import bbattulga.matchengine.libmodel.jpa.repository.PairRepository;
import bbattulga.matchengine.libservice.orderlog.OrderLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderOpenConsumer {

    private final OrderRepository orderRepository;
    private final OrderLogService orderLogService;
    private final PairRepository pairRepository;

    @Transactional
    public void consume(OrderOpenOutput openOrder) throws BadParameterException {
        final var orderOpt = orderRepository.findByOrderCode(UUID.fromString(openOrder.getOrderId()));
        final var now = LocalDateTime.now();
        if (orderOpt.isEmpty()) {
            final var symbol = String.format("%s/%s", openOrder.getBase(), openOrder.getQuote());
            final var pair = pairRepository.findBySymbolAndStatus(symbol, Pair.Status.ACTIVE).orElseThrow((PairNotFoundException::new));
            final var newOrder = new Order();
            newOrder.setOrderCode(UUID.fromString(openOrder.getOrderId()));
            newOrder.setStatus(OrderStatus.OPEN);
            newOrder.setUid(UUID.fromString(openOrder.getUid()));
            newOrder.setPairId(pair.getPairId());
            newOrder.setType(OrderType.LIMIT);
            newOrder.setSide(openOrder.getSide());
            newOrder.setPrice(openOrder.getPrice());
            newOrder.setQty(openOrder.getQty());
            newOrder.setTotal(openOrder.getTotal());
            newOrder.setUtc(openOrder.getUtc());
            newOrder.setExecQty(openOrder.getExecQty());
            newOrder.setExecTotal(openOrder.getExecTotal());
            newOrder.setFillQty(BigInteger.ZERO);
            newOrder.setFillTotal(BigInteger.ZERO);
            newOrder.setRemainingQty(openOrder.getRemainingQty());
            newOrder.setRemainingTotal(openOrder.getRemainingTotal());
            newOrder.setCreatedAt(now);
            newOrder.setUpdatedAt(now);
            newOrder.setNs(openOrder.getNs());
            newOrder.setExecUtc(openOrder.getExecUtc());
            orderRepository.save(newOrder);
            orderLogService.saveOrderLog(newOrder);
        } else {
            final var order = orderOpt.get();
            order.setStatus(OrderStatus.OPEN);
            order.setExecQty(openOrder.getExecQty());
            order.setExecTotal(openOrder.getExecTotal());
            order.setNs(openOrder.getNs());
            order.setExecUtc(openOrder.getExecUtc());
            order.setUpdatedAt(now);
            orderRepository.save(order);
            orderLogService.saveOrderLog(order);
        }
    }
}
