package bbattulga.matchengine.servicematchengine.controller;

import bbattulga.matchengine.servicematchengine.dto.engine.OrderBookSnapshotData;
import bbattulga.matchengine.servicematchengine.service.snapshot.OrderBookSnapshotService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/snapshot")
@RequiredArgsConstructor
public class SnapshotController {

    private final OrderBookSnapshotService orderBookSnapshotService;

    @GetMapping("/order-book")
    public OrderBookSnapshotData orderBookSnapshotData() {
        return orderBookSnapshotService.getSnapshot();
    }
}
