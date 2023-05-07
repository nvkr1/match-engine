package bbattulga.matchengine.libmodel.engine.output;

import bbattulga.matchengine.libmodel.consts.OrderSide;
import bbattulga.matchengine.libmodel.consts.OrderStatus;
import bbattulga.matchengine.libmodel.consts.OrderType;

import java.math.BigInteger;

public interface IOrderCancelOutput {
    String getOrderId();
    String getUid();
    OrderSide getSide();
    OrderType getType();
    String getBase();
    String getQuote();
    BigInteger getPrice();
    BigInteger getQty();
    BigInteger getTotal();
    BigInteger getExecQty();
    BigInteger getExecTotal();
    BigInteger getFillQty();
    BigInteger getFillTotal();
    BigInteger getRemainingQty();
    BigInteger getRemainingTotal();
    OrderStatus getStatus();
    long getNs();
    long getUtc();
}
