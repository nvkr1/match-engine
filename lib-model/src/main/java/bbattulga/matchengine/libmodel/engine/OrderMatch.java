package bbattulga.matchengine.libmodel.engine;

import lombok.*;

import java.math.BigInteger;

@Getter
@Setter
@Builder
public class OrderMatch {
    private BigInteger price;
    private BigInteger qty;
    private Long utc;
}
