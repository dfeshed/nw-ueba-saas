package com.rsa.asoc.sa.ui.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

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
