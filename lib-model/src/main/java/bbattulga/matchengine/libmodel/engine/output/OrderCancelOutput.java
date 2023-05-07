package bbattulga.matchengine.libmodel.engine.output;

import bbattulga.matchengine.libmodel.consts.OrderSide;
import bbattulga.matchengine.libmodel.consts.OrderStatus;
import bbattulga.matchengine.libmodel.consts.OrderType;
import bbattulga.matchengine.libmodel.consts.OutputType;
import lombok.*;

import java.math.BigInteger;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCancelOutput extends OutputEvent implements IOrderCancelOutput {
    @Builder.Default
    private OutputType outputType = OutputType.CANCEL;
    private String orderId;
    private String uid;
    private OrderSide side;
    private OrderType type;
    private String base;
    private String quote;
    private BigInteger price;
    private BigInteger qty;
    private BigInteger total;
    @Builder.Default
    private BigInteger execQty = BigInteger.ZERO;
    @Builder.Default
    private BigInteger execTotal = BigInteger.ZERO;
    @Builder.Default
    private BigInteger fillQty = BigInteger.ZERO;
    @Builder.Default
    private BigInteger fillTotal = BigInteger.ZERO;
    private BigInteger remainingQty;
    private BigInteger remainingTotal;
    private OrderStatus status;
    private long ns;
    private long utc;
}
