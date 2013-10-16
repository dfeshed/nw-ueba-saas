package fortscale.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import fortscale.domain.analyst.AnalystAuth;

public class AuthenticationFailureHandlerImpl extends SimpleUrlAuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException exception)
			throws IOException, ServletException {
		if(exception instanceof CredentialsExpiredException) {
			@SuppressWarnings("deprecation")
			String usernameString = ((AnalystAuth)exception.getExtraInformation()).getUsername();
			response.sendRedirect(String.format("/fortscale-webapp/change_password.html?username=%s", usernameString));
		}else {
			response.getWriter().write(exception.getMessage());
			//super.onAuthenticationFailure(request, response, exception);
		}
	}

}
