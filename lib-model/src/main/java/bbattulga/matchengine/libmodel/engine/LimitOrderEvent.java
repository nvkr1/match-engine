package bbattulga.matchengine.libmodel.engine;

import bbattulga.matchengine.libmodel.consts.OrderSide;
import bbattulga.matchengine.libmodel.consts.OrderStatus;

import java.math.BigInteger;

public interface LimitOrderEvent {

    // order id
    String getId();
    void setId(String id);

    // user id
    String getUid();
    void setUid(String uid);

    // price
    BigInteger getPrice();
    void setPrice(BigInteger price);

    // order qty
    BigInteger getQty();
    void setQty(BigInteger qty);

    // order timestamp
    long getUtc();
    void setUtc(long utc);

    // order side
    OrderSide getSide();
    void setSide(OrderSide side);

    // order status
    OrderStatus getStatus();
    void setStatus(OrderStatus status);

    LimitOrderEvent clone();
}
