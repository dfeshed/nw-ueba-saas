package com.rsa.asoc.sa.ui.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CsrfToken;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A REST-based authentication handler that sets HTTP status codes
 *
 * @author athielke
 */
public class RestAuthenticationHandler implements
        AuthenticationSuccessHandler, AuthenticationFailureHandler, LogoutSuccessHandler {

    private static final String REQUEST_ATTRIBUTE_NAME = "_csrf";
    private static final String RESPONSE_HEADER_NAME = "X-CSRF-HEADER";
    private static final String RESPONSE_PARAM_NAME = "X-CSRF-PARAM";
    private static final String RESPONSE_TOKEN_NAME = "X-CSRF-TOKEN";

    private final ObjectMapper objectMapper;

    public RestAuthenticationHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Returns HTTP CREATED (201) on successful authentication.
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                        Authentication authentication) throws IOException, ServletException {

        final CsrfToken token = (CsrfToken)httpServletRequest.getAttribute(REQUEST_ATTRIBUTE_NAME);
        if (token != null) {
            httpServletResponse.setHeader(RESPONSE_HEADER_NAME, token.getHeaderName());
            httpServletResponse.setHeader(RESPONSE_PARAM_NAME, token.getParameterName());

            // The token is an instance of SaveOnAccessCsrfToken which has the getToken() method  that will
            // set the newly generated CSRF token on the HttpSession. This should be done onAuthenticationSuccess.
            // If not, MissingCsrfTokenException will be thrown on the sub-sequence POST.
            httpServletResponse.setHeader(RESPONSE_TOKEN_NAME , token.getToken());
        }
        httpServletResponse.setStatus(HttpServletResponse.SC_CREATED);
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON.toString());
        objectMapper.writeValue(httpServletResponse.getOutputStream(), authentication.getPrincipal());
    }

    /**
     * Returns HTTP UNAUTHORIZED (401) on failed authentication.
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                        AuthenticationException exception) throws IOException, ServletException {
        httpServletResponse.addHeader("X-Authentication-Exception", exception.getClass().getName());
        httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, exception.getMessage());
    }

    /**
     * Returns HTTP NO CONTENT (204) on successful logout.
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                Authentication authentication) throws IOException, ServletException {
        httpServletResponse.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}
