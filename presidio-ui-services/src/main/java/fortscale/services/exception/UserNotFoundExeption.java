package fortscale.services.exception;

/**
 * Created by shays on 07/11/2017.
 */
public class UserNotFoundExeption extends Exception {
    public UserNotFoundExeption(String userId) {
        super("User with ID "+userId+" not found");
    }
}