package fortscale.web.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Amir Keren on 31/08/15.
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Invalid Parameter")
public class InvalidParameterException extends IllegalArgumentException {

    public InvalidParameterException(String message) {
        super(message);

    }

}