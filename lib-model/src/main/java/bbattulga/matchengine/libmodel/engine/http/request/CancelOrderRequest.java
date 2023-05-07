package bbattulga.matchengine.libmodel.engine.http.request;

import bbattulga.matchengine.libmodel.consts.OrderSide;
import lombok.*;

import java.math.BigInteger;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CancelOrderRequest {
    private String id;
    private OrderSide side;
    private BigInteger price;
}
