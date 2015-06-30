package com.rsa.asoc.sa.ui.security;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Test for {@link UnauthorizedAuthenticationEntryPoint}
 *
 * @author athielke
 */
public class UnauthorizedAuthenticationEntryPointTest {

    private static UnauthorizedAuthenticationEntryPoint entryPoint = new UnauthorizedAuthenticationEntryPoint();

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AuthenticationException authenticationException;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCommence() throws Exception {
        final String message = "Authentication failed";
        Mockito.when(authenticationException.getMessage()).thenReturn(message);

        entryPoint.commence(request, response, authenticationException);

        Mockito.verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
    }
}
