package fortscale.utils.logging;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component("loggerFilter")
public class LoggerFilter extends OncePerRequestFilter {
	public static final String MDC_KEY_USER = "req.user";
	
	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try{
			populateMDC(request);
			filterChain.doFilter(request, response);
		}finally{
			unpopulateMDC();
		}
	}
	
	public static final void populateMDC(HttpServletRequest request){
		request.setAttribute("username", request.getRemoteUser()); // trying to pass this to logback access
		MDC.put(MDC_KEY_USER, request.getRemoteUser());
	}

	public static final void unpopulateMDC(){
		MDC.remove(MDC_KEY_USER);
	}
}
