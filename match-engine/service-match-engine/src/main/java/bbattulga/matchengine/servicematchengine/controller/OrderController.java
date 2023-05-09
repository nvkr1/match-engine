package bbattulga.matchengine.servicematchengine.controller;

import bbattulga.matchengine.libmodel.engine.http.request.CancelOrderRequest;
import bbattulga.matchengine.libmodel.engine.http.request.LimitOrderRequest;
import bbattulga.matchengine.libmodel.engine.http.response.OrderResponse;
import bbattulga.matchengine.servicematchengine.service.place.CancelOrderPlaceService;
import bbattulga.matchengine.servicematchengine.service.place.LimitOrderPlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final LimitOrderPlaceService limitOrderPlaceService;
    private final CancelOrderPlaceService cancelOrderPlaceService;

    @PostMapping("/limit")
    public OrderResponse limitOrder(@RequestBody LimitOrderRequest request) {
        limitOrderPlaceService.placeLimitOrder(request);
        return OrderResponse.builder().build();
    }

    @PostMapping("/cancel")
    public OrderResponse cancelOrder(@RequestBody CancelOrderRequest request) {
        cancelOrderPlaceService.placeCancelOrder(request);
        return OrderResponse.builder().build();
    }
}
