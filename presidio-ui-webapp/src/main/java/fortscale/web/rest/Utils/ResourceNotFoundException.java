package fortscale.web.rest.Utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by tomerd on 21/10/2015.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public final class ResourceNotFoundException extends RuntimeException{
	public ResourceNotFoundException() { super(); }
	public ResourceNotFoundException(String s) { super(s); }
	public ResourceNotFoundException(String s, Throwable throwable) { super(s, throwable); }
	public ResourceNotFoundException(Throwable throwable) { super(throwable); }
}
