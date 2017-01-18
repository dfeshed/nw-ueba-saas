package fortscale.domain.Exceptions;

/**
 * Created by shays on 18/12/2016.
 */
public class PasswordDecryptionException  extends  Exception{
    public PasswordDecryptionException() {
        super();
    }

    public PasswordDecryptionException(String message) {
        super(message);
    }

    public PasswordDecryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
