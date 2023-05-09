package bbattulga.matchengine.libmodel.engine.output;

import bbattulga.matchengine.libmodel.engine.OrderEvent;

import java.math.BigInteger;

public interface IOrderMatchOutput {
    String getBase();
    String getQuote();
    BigInteger getQty();
    BigInteger getPrice();
    BigInteger getTotal();
    OrderEvent getExecOrder();
    OrderEvent getRemainingOrder();
    BigInteger getMakerFee();
    BigInteger getTakerFee();
    long getNs();
    long getUtc();
}
