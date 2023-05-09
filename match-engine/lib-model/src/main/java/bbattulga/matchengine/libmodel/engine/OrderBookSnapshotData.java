package bbattulga.matchengine.libmodel.engine;

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
