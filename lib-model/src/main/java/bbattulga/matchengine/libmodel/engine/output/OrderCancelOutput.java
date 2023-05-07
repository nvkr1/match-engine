package bbattulga.matchengine.libmodel.engine.output;

import bbattulga.matchengine.libmodel.consts.OrderSide;
import bbattulga.matchengine.libmodel.consts.OrderStatus;
import bbattulga.matchengine.libmodel.consts.OrderType;
import lombok.*;

import java.math.BigInteger;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCancelOutput extends OutputEvent {
    /**
     * Matching Quantity
     */
    private String orderId;
    private String uid;
    private OrderSide side;
    private OrderType type;
    private String base;
    private String quote;
    private BigInteger price;
    private BigInteger qty;
    private BigInteger total;
    private BigInteger execQty;
    private BigInteger execTotal;
    private BigInteger fillQty;
    private BigInteger fillTotal;
    private BigInteger remainingQty;
    private BigInteger remainingTotal;
    private OrderStatus status;
    private long ns;
    private long utc;
}
