package bbattulga.matchengine.libmodel.engine.output;

import lombok.*;

import java.math.BigInteger;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderOpenOutput {
    /**
     * Matching Quantity
     */
    private String orderId;
    private String uid;
    private String base;
    private String quote;
    private BigInteger qty;
    private BigInteger price;
    private BigInteger total;
    private long utc;
}
