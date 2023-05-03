package bbattulga.matchengine.libmodel.exception;

public class AssetNotFoundException extends BadParameterException {
    public AssetNotFoundException() {
        super("asset-not-found");
    }
}
