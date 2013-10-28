package fortscale.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import fortscale.web.exceptions.handlers.RestExceptionHandler;

public class AccessDeniedHandlerImpl implements AccessDeniedHandler{
	
	private RestExceptionHandler restExceptionHandler;
	
	public void setRestExceptionHandler(RestExceptionHandler restExceptionHandler) {
		this.restExceptionHandler = restExceptionHandler;
	}

	@Override
	public void handle(HttpServletRequest request,
			HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException,
			ServletException {
		restExceptionHandler.resolveException(request, response, null, accessDeniedException);
	}

}
