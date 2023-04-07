package bbattulga.matchengine.servicematchengine.service;

import bbattulga.matchengine.servicematchengine.dto.engine.Order;
import bbattulga.matchengine.servicematchengine.dto.engine.OrderBookPriceLevel;
import bbattulga.matchengine.servicematchengine.dto.engine.OrderMatch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedList;

@Service
@RequiredArgsConstructor
public class SellOrderService {

    private final OrderBookService orderBookService;

    public LinkedList<OrderMatch> placeLimitSellOrder(Order order) {
        final LinkedList<Order> matches = new LinkedList<>();
        final var lowestPriceLevel = orderBookService.getMin();
        if (lowestPriceLevel == null) {
            final var newSellPriceLevel = buildNewPriceLevel(order);
            orderBookService.addMin(newSellPriceLevel);
        }
        return new LinkedList<>();
    }

    private OrderBookPriceLevel buildNewPriceLevel(Order order) {
        final var priceLevel = new OrderBookPriceLevel();
        priceLevel.setPrice(order.getPrice());
        final LinkedList<Order> sellOrders = new LinkedList<>();
        sellOrders.add(order);
        priceLevel.setSellOrders(sellOrders);
        priceLevel.setOrders(new LinkedList<>());
        return priceLevel;
    }
}
