package bbattulga.matchengine.libmodel.engine;

import bbattulga.matchengine.libmodel.consts.OrderSide;
import bbattulga.matchengine.libmodel.consts.OrderStatus;
import bbattulga.matchengine.libmodel.consts.OrderType;
import com.lmax.disruptor.EventFactory;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class OrderEvent implements CancelOrderEvent, LimitOrderEvent, MarketOrderEvent {
    private OrderType type;
    private OrderSide side;
    private String id;
    private String uid;
    private BigInteger qty;
    private BigInteger price;
    private BigInteger total;
    private OrderStatus status;
    private long utc;
    public final static EventFactory EVENT_FACTORY
            = () -> new OrderEvent();

    // clone is used for transactional writes
    public OrderEvent clone() {
        final var clone = new OrderEvent();
        clone.setType(type);
        clone.setSide(side);
        clone.setId(id);
        clone.setUid(uid);
        clone.setQty(qty);
        clone.setPrice(price);
        clone.setTotal(total);
        clone.setUtc(utc);
        clone.setStatus(status);
        return clone;
    }
}
