package bbattulga.matchengine.libmodel.exception;

public class MatchNotFoundException extends BadParameterException {
    public MatchNotFoundException() {
        super("match-not-found");
    }
}
