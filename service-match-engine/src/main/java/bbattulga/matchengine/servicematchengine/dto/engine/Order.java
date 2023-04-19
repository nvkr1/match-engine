package bbattulga.matchengine.servicematchengine.dto.engine;

import bbattulga.matchengine.servicematchengine.consts.OrderSide;
import bbattulga.matchengine.servicematchengine.consts.OrderType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
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
