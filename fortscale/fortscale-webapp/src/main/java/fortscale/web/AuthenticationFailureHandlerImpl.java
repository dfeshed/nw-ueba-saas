package fortscale.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

public class AuthenticationFailureHandlerImpl extends SimpleUrlAuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException exception)
			throws IOException, ServletException {
		if(exception instanceof CredentialsExpiredException) {
//			@SuppressWarnings("deprecation")
//			String usernameString = ((AnalystAuth)exception.getExtraInformation()).getUsername();
//			response.sendRedirect(String.format("/fortscale-webapp/change_password.html?username=%s", usernameString));
			request.setAttribute(WebAttributes.ACCESS_DENIED_403, exception);

            // Set the 403 status code.
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            PrintWriter out = response.getWriter();
            JSONObject obj = new JSONObject();
            try {
				obj.put("reason", "change_password");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            out.print(obj);
		}else {
			request.setAttribute(WebAttributes.ACCESS_DENIED_403, exception);

            // Set the 403 status code.
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}

}
