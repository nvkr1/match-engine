package bbattulga.matchengine.libmodel.engine.output;

import bbattulga.matchengine.libmodel.engine.OrderEvent;
import lombok.*;

import java.math.BigInteger;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderMatchOutput {
    /**
     * Matching Quantity
     */
    private String base;
    private String quote;
    private BigInteger qty;
    private BigInteger price;
    private BigInteger total;
    private OrderEvent execOrder;
    private OrderEvent remainingOrder;
    private BigInteger makerFee;
    private BigInteger takerFee;
    private long utc;
}
