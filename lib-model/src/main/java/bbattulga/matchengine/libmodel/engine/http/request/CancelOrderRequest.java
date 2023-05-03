package bbattulga.matchengine.libmodel.engine.http.request;

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
public class CancelOrderRequest {
    private String id;
    private OrderSide side;
    private BigInteger price;
}
