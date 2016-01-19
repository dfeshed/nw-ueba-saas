package fortscale.ml.scorer.factory;


public class FactoryNotInitializedException extends Exception {
    public FactoryNotInitializedException() {
    }

    public FactoryNotInitializedException(String message) {
        super(message);
    }

    public FactoryNotInitializedException(String message, Throwable cause) {
        super(message, cause);
    }

    public FactoryNotInitializedException(Throwable cause) {
        super(cause);
    }
}
