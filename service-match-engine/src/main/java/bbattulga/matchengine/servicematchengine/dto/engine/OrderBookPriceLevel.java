package bbattulga.matchengine.servicematchengine.dto.engine;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.LinkedList;

@Getter
@Setter
public class OrderBookPriceLevel {
    private BigInteger price;
    private Boolean isBuy;
    private LinkedList<Order> orders;
}
