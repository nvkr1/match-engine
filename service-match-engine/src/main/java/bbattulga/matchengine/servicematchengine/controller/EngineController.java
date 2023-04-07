package bbattulga.matchengine.servicematchengine.controller;

import bbattulga.matchengine.servicematchengine.dto.engine.Order;
import bbattulga.matchengine.servicematchengine.dto.request.OrderRequest;
import bbattulga.matchengine.servicematchengine.dto.response.OrderResponse;
import bbattulga.matchengine.servicematchengine.service.QueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class EngineController {

    private final QueueService queueService;

    @PostMapping("/order")
    public OrderResponse placeOrder(@RequestBody OrderRequest request) {
        try {
            queueService.placeOrder(Order.builder()
                    .id(request.getId())
                    .isBuy(request.getSide().equals("BUY"))
                    .price(request.getPrice())
                    .qty(request.getQty())
                    .uid(request.getUid())
                    .utc(request.getUtc())
                    .build());
        } catch (InterruptedException interruptedException) {
            return OrderResponse.builder()
                    .status("ERROR")
                    .message("InterruptedException")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return OrderResponse.builder()
                    .message("UNKNOWN ERROR")
                    .build();
        }
        return OrderResponse.builder()
                .status("SUCCESS")
                .message("SUCCESS")
                .build();
    }
}
