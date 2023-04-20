package bbattulga.matchengine.libmodel.engine;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.LinkedList;

@Getter
@Setter
@NoArgsConstructor
public class OrderBookPriceLevel {
    private BigInteger price;
    private LinkedList<OrderEvent> orders = new LinkedList<>();
}
