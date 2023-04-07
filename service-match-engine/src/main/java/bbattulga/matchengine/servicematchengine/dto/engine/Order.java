package bbattulga.matchengine.servicematchengine.dto.engine;

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
    private Boolean isBuy; // !isBuy == sell
    private String type; // limit | market
    private BigInteger price;
    private BigInteger qty;
    private Long utc;
    public Boolean getIsSell() {
        return !isBuy;
    }
}
