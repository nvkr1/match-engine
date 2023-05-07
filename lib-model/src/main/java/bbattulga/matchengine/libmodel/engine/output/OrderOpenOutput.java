package bbattulga.matchengine.libmodel.engine.output;

import bbattulga.matchengine.libmodel.consts.OrderSide;
import bbattulga.matchengine.libmodel.consts.OrderType;
import bbattulga.matchengine.libmodel.consts.OutputType;
import lombok.*;

import java.math.BigInteger;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderOpenOutput extends OutputEvent implements IOrderOpenOutput {
    @Builder.Default
    private OutputType outputType = OutputType.OPEN;
    private String orderId;
    private OrderType type;
    private OrderSide side;
    private String uid;
    private String base;
    private String quote;
    private BigInteger qty;
    private BigInteger price;
    private BigInteger total;
    private BigInteger remainingQty;
    private BigInteger remainingTotal;
    @Builder.Default
    private BigInteger execQty = BigInteger.ZERO;
    @Builder.Default
    private BigInteger execTotal = BigInteger.ZERO;
    private long execUtc;
    private long ns;
    private long utc;
}
