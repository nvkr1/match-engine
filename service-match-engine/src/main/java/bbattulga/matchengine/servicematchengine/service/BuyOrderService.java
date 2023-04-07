package bbattulga.matchengine.servicematchengine.service;

import bbattulga.matchengine.servicematchengine.dto.engine.Order;
import bbattulga.matchengine.servicematchengine.dto.engine.OrderBookPriceLevel;
import bbattulga.matchengine.servicematchengine.dto.engine.OrderMatch;
import lombok.RequiredArgsConstructor;
import org.jgrapht.util.AVLTree;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.LinkedList;

@Service
@RequiredArgsConstructor
public class BuyOrderService {

    private final OrderBookService orderBookService;

    public LinkedList<OrderMatch> placeLimitBuyOrder(Order order) {
        final LinkedList<OrderMatch> matches = new LinkedList<>();
        AVLTree.TreeNode<OrderBookPriceLevel> lowestPriceLevel = orderBookService.getMin();
        if (lowestPriceLevel == null || order.getPrice().compareTo(lowestPriceLevel.getValue().getPrice()) < 0) {
            // new lowest price
            final var newPriceLevel = buildNewPriceLevel(order);
            orderBookService.addMin(newPriceLevel);
            return matches;
        }
        while (lowestPriceLevel != null && order.getPrice().compareTo(lowestPriceLevel.getValue().getPrice()) >= 0) {
            if (lowestPriceLevel.getValue().getIsBuy()) {

            }
            // buy price >= lowest sell order
            final var sellOrders = lowestPriceLevel.getValue().getOrders();
            while (!sellOrders.isEmpty() && order.getQty().compareTo(BigInteger.ZERO) >= 0) {
                Order sellOrder = sellOrders.peek();
                if (sellOrder == null) {
                    if (order.getQty().compareTo(BigInteger.ZERO) > 0) {
                        lowestPriceLevel.getValue().getOrders().add(order);
                    }
                    break;
                }
                final var sellQty = sellOrder.getQty();
                final var buyQty = order.getQty();
                final var matchQty = sellQty.subtract(sellQty.min(buyQty));
                final var sellRemainingQty = sellQty.subtract(matchQty);
                final var buyRemainingQty = buyQty.subtract(matchQty);
                sellOrder.setQty(sellRemainingQty);
                order.setQty(buyRemainingQty);
                final var matchUtc = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
                final var match = OrderMatch.builder()
                        .price(lowestPriceLevel.getValue().getPrice())
                        .qty(matchQty)
                        .executingOrder(order)
                        .remainingOrder(sellOrder)
                        .utc(matchUtc)
                        .build();
                matches.add(match);
                if (sellOrder.getQty().compareTo(BigInteger.ZERO) == 0) {
                    // current sell order fulfilled, goto next sell order
                    sellOrders.remove();
                } else if (order.getQty().compareTo(BigInteger.ZERO) == 0) {
                    // order fulfilled
                    break;
                }
            }
            // current price level finished, goto next price level
            final var nextPriceLevel = orderBookService.successor(lowestPriceLevel);
            if (nextPriceLevel != null && nextPriceLevel.getValue().getPrice().compareTo(order.getPrice()) > 0) {
                if (order.getQty().compareTo(BigInteger.ZERO) > 0) {
                    lowestPriceLevel.getValue().getOrders().add(order);
                }
            }
            lowestPriceLevel = nextPriceLevel;
        }
        return matches;
    }

    private OrderBookPriceLevel buildNewPriceLevel(Order order) {
        final var priceLevel = new OrderBookPriceLevel();
        priceLevel.setIsBuy(true);
        priceLevel.setPrice(order.getPrice());
        final LinkedList<Order> buyOrders = new LinkedList<>();
        buyOrders.add(order);
        priceLevel.setOrders(buyOrders);
        return priceLevel;
    }
}
