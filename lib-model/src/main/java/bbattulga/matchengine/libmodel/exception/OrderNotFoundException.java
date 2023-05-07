package bbattulga.matchengine.libmodel.exception;

public class OrderNotFoundException extends BadParameterException {
    public OrderNotFoundException() {
        super("order-not-found");
    }
}
