package fortscale.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import fortscale.web.exceptions.handlers.RestExceptionHandler;

public class AuthenticationFailureHandlerImpl extends SimpleUrlAuthenticationFailureHandler {

	private RestExceptionHandler restExceptionHandler;
	
	public void setRestExceptionHandler(RestExceptionHandler restExceptionHandler) {
		this.restExceptionHandler = restExceptionHandler;
	}
	
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException exception)
			throws IOException, ServletException {
		restExceptionHandler.resolveException(request, response, null, exception);
	}

}
