package fortscale.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import fortscale.web.exceptions.SessionExpiredException;
import fortscale.web.exceptions.handlers.RestExceptionHandler;


public class FortscaleFailedEntryPoint implements AuthenticationEntryPoint {

	private RestExceptionHandler restExceptionHandler;
	
	public void setRestExceptionHandler(RestExceptionHandler restExceptionHandler) {
		this.restExceptionHandler = restExceptionHandler;
	}

	@Override
	public void commence(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException authException)
			throws IOException, ServletException {
		Exception exception = authException;
		if (!response.isCommitted()) {
			String sessionId = getSessionId(request);
			if (sessionId != null) {
				exception = new SessionExpiredException("Session expired " + sessionId, authException);
			}
        }
		
		restExceptionHandler.resolveException(request, response, null, exception);
	}

	private String getSessionId(HttpServletRequest request) {
		Cookie cookies[] = request.getCookies();
		String sessionId = null;
		if(cookies != null) {
			for(Cookie cookie: cookies) {
				if(cookie.getName().equals("JSESSIONID")) {
					sessionId = cookie.getValue();
					break;
				}
			}
		}
		return sessionId;
	}

}
