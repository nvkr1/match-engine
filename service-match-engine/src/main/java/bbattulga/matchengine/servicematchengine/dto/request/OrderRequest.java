package bbattulga.matchengine.servicematchengine.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    private String id;
    private String side; // buy | sell
    private String uid;
    private String type; // limit | market
    private BigInteger price;
    private BigInteger qty;
    private Long utc;
}
