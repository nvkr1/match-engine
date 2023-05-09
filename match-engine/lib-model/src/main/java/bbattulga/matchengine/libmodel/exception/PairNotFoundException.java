package bbattulga.matchengine.libmodel.exception;

public class PairNotFoundException extends BadParameterException {
    public PairNotFoundException() {
        super("pair-not-found");
    }
}
