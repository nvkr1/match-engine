package bbattulga.matchengine.servicematchengine.service;

import bbattulga.matchengine.servicematchengine.dto.engine.Order;
import bbattulga.matchengine.servicematchengine.dto.engine.OrderMatch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedList;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final BuyOrderService buyOrderService;
    private final SellOrderService sellOrderService;

    public LinkedList<OrderMatch> placeLimitOrder(Order order) {
        if (order.getIsBuy()) {
            return buyOrderService.placeLimitBuyOrder(order);
        } else {
            return sellOrderService.placeLimitSellOrder(order);
        }
    }
}
