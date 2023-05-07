package bbattulga.matchengine.libmodel.engine.output;

import bbattulga.matchengine.libmodel.consts.OrderSide;
import bbattulga.matchengine.libmodel.consts.OrderStatus;
import bbattulga.matchengine.libmodel.consts.OrderType;
import bbattulga.matchengine.libmodel.consts.OutputType;
import bbattulga.matchengine.libmodel.engine.OrderEvent;
import com.lmax.disruptor.EventFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OutputEvent implements IOrderOpenOutput, IOrderMatchOutput, IOrderCancelOutput {
    private OutputType outputType;
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
    private BigInteger fillQty = BigInteger.ZERO;
    private BigInteger fillTotal = BigInteger.ZERO;
    private BigInteger execQty = BigInteger.ZERO;
    private BigInteger execTotal = BigInteger.ZERO;
    private OrderEvent execOrder;
    private BigInteger makerFee;
    private BigInteger takerFee;
    private OrderStatus status;
    private OrderEvent remainingOrder;
    private long execUtc;
    private long ns;
    private long utc;
    public final static EventFactory EVENT_FACTORY
            = () -> new OutputEvent();
}
