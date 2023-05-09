package bbattulga.matchengine.servicematchengine.service.snapshot;

import bbattulga.matchengine.libmodel.engine.OrderBookPriceLevel;
import bbattulga.matchengine.libmodel.engine.OrderBookSnapshotData;
import bbattulga.matchengine.servicematchengine.service.place.OrderBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderBookSnapshotService {

    private final OrderBookService orderBookService;

    public OrderBookSnapshotData getSnapshot() {
        final List<OrderBookPriceLevel> bids = new ArrayList<>();
        final List<OrderBookPriceLevel> asks = new ArrayList<>();
        final var askPriceLevels = orderBookService.getAsks().keySet();
        askPriceLevels.forEach(price -> {
            final var level = orderBookService.getAsks().get(price);
            asks.add(level);
        });
        final var bidPriceLevels = orderBookService.getBids().keySet();
        bidPriceLevels.forEach(price -> {
            final var level = orderBookService.getBids().get(price);
            bids.add(level);
        });
        return OrderBookSnapshotData.builder()
                .bids(bids)
                .asks(asks)
                .build();
    }
}
