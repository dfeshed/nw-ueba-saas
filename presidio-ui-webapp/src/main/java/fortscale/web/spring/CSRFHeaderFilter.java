package fortscale.web.spring;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by rans on 01/11/15.
 */
public class CSRFHeaderFilter extends OncePerRequestFilter {

    protected static final String REQUEST_ATTRIBUTE_NAME = "_csrf";
    protected static final String RESPONSE_HEADER_NAME = "X-CSRF-HEADER";
    protected static final String RESPONSE_PARAM_NAME = "X-CSRF-PARAM";
    protected static final String RESPONSE_TOKEN_NAME = "X-CSRF-TOKEN";
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        CsrfToken token = (CsrfToken) httpServletRequest.getAttribute(REQUEST_ATTRIBUTE_NAME);
        if (token != null) {
            httpServletResponse.setHeader(RESPONSE_HEADER_NAME, token.getHeaderName());
            httpServletResponse.setHeader(RESPONSE_PARAM_NAME, token.getParameterName());
            httpServletResponse.setHeader(RESPONSE_TOKEN_NAME , token.getToken());
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

}
