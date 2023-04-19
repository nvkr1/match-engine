package bbattulga.matchengine.servicematchengine.dto.engine;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderBookSnapshotData {
    private List<OrderBookPriceLevel> bids;
    private List<OrderBookPriceLevel> asks;
}
