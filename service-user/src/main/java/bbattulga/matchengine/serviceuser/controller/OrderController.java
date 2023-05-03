package bbattulga.matchengine.serviceuser.controller;

import bbattulga.matchengine.serviceuser.dto.request.OrderRequest;
import bbattulga.matchengine.serviceuser.dto.response.OrderResponse;
import bbattulga.matchengine.serviceuser.service.LimitOrderPlaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final LimitOrderPlaceService limitOrderPlaceService;

    // TODO:: Authentication
    @PostMapping("/order/limit")
    public OrderResponse placeLimitOrder(@RequestBody OrderRequest request) {
        final var order = limitOrderPlaceService.placeOrder(request);
        return OrderResponse.builder()
                .orderId(order.getOrderId().toString())
                .build();
    }
}
