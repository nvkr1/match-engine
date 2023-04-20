package bbattulga.matchengine.libmodel.engine;

import bbattulga.matchengine.libmodel.consts.OrderSide;
import bbattulga.matchengine.libmodel.consts.OrderType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@Builder
public class Order {
    private String id;
    private String uid;
    private OrderSide side;
    private OrderType type; // limit | market
    private BigInteger price;
    private BigInteger qty;
    private Long utc;
}
