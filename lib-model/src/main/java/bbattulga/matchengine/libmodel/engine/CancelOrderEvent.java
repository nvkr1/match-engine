package bbattulga.matchengine.libmodel.engine;

import bbattulga.matchengine.libmodel.consts.OrderSide;

import java.math.BigInteger;

public interface CancelOrderEvent {

    // order id
    String getId();
    void setId(String id);

    // price
    BigInteger getPrice();
    void setPrice(BigInteger price);

    // order side
    OrderSide getSide();
    void setSide(OrderSide side);
}
