package bbattulga.matchengine.libmodel.engine.output;

import bbattulga.matchengine.libmodel.consts.OrderSide;
import bbattulga.matchengine.libmodel.consts.OrderType;

import java.math.BigInteger;

public interface IOrderOpenOutput {
    String getOrderId();
    OrderType getType();
    OrderSide getSide();
    String getUid();
    String getBase();
    String getQuote();
    BigInteger getQty();
    BigInteger getPrice();
    BigInteger getTotal();
    BigInteger getRemainingQty();
    BigInteger getRemainingTotal();
    BigInteger getExecQty();
    BigInteger getExecTotal();
    long getExecUtc();
    long getNs();
    long getUtc();
}
