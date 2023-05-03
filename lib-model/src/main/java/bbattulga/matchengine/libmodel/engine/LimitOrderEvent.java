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

    // order qty
    BigInteger getTotal();
    void setTotal(BigInteger qty);

    BigInteger getRemainingQty();
    void setRemainingQty(BigInteger remainingQty);

    BigInteger getRemainingTotal();
    void setRemainingTotal(BigInteger remainingTotal);

    BigInteger getExecQty();
    void setExecQty(BigInteger execQty);

    BigInteger getExecTotal();
    void setExecTotal(BigInteger execTotal);

    BigInteger getFillQty();
    void setFillQty(BigInteger fillQty);

    BigInteger getFillTotal();
    void setFillTotal(BigInteger fillTotal);

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
