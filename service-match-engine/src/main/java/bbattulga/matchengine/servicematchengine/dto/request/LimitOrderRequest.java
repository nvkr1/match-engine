package bbattulga.matchengine.servicematchengine.dto.request;

import bbattulga.matchengine.libmodel.consts.OrderSide;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LimitOrderRequest {
    private String id;
    private OrderSide side;
    private String uid;
    private BigInteger price;
    private BigInteger qty;
    private long utc;
}
