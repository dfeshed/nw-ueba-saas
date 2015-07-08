package com.rsa.asoc.sa.ui.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.DelegatingServletOutputStream;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.security.Principal;

/**
 * Test for {@link RestAuthenticationHandler}
 *
 * @author athielke
 */
public class RestAuthenticationHandlerTest {

    private static RestAuthenticationHandler restAuthenticationHandler =
            new RestAuthenticationHandler(new ObjectMapper());

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @Mock
    private AuthenticationException authenticationException;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testOnAuthenticationSuccess() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DelegatingServletOutputStream servletOutputStream = new DelegatingServletOutputStream(outputStream);

        Principal principal = new UsernamePasswordAuthenticationToken("admin", "netwitness");
        Mockito.when(authentication.getPrincipal()).thenReturn(principal);
        Mockito.when(response.getOutputStream()).thenReturn(servletOutputStream);

        restAuthenticationHandler.onAuthenticationSuccess(request, response, authentication);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_CREATED);
        Mockito.verify(response).setContentType(MediaType.APPLICATION_JSON.toString());

        Assert.assertEquals("admin", JsonPath.read(outputStream.toString("UTF-8"), "$.name"));
    }

    @Test
    public void testOnAuthenticationFailure() throws Exception {
        final String message = "Authentication failed";
        Mockito.when(authenticationException.getMessage()).thenReturn(message);

        restAuthenticationHandler.onAuthenticationFailure(request, response, authenticationException);

        Mockito.verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
    }

    @Test
    public void testOnLogoutSuccess() throws Exception {
        restAuthenticationHandler.onLogoutSuccess(request, response, authentication);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}
