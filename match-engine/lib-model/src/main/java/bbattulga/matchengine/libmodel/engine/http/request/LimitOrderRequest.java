package bbattulga.matchengine.libmodel.engine.http.request;

import bbattulga.matchengine.libmodel.consts.OrderSide;
import lombok.*;

import java.math.BigInteger;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LimitOrderRequest {
    private String id;
    private OrderSide side;
    private String uid;
    private BigInteger price;
    private BigInteger qty;
    private BigInteger total;
    private long utc;
}
