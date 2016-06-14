package com.rsa.asoc.sa.ui.common.authentication.provider;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

/**
 * Handles authentication error responses from Security Analytics
 *
 * @author Jay Garala
 * @since 10.6.0
 */
public class AuthenticationResponseErrorHandler implements ResponseErrorHandler {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationResponseErrorHandler.class);

    private static final String EXCEPTION_HEADER = "X-Authentication-Exception";

    @Override
    public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
        return clientHttpResponse.getRawStatusCode() != 200;
    }

    @Override
    public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
        String body = IOUtils.toString(clientHttpResponse.getBody());
        int statusCode = clientHttpResponse.getRawStatusCode();
        if (statusCode == 401 || statusCode == 403 ) {
            if (clientHttpResponse.getHeaders().containsKey(EXCEPTION_HEADER)) {
                String exception = clientHttpResponse.getHeaders().get(EXCEPTION_HEADER).get(0);

                switch (exception) {
                    case "org.springframework.security.authentication.DisabledException":
                        throw new DisabledException(body);

                    case "org.springframework.security.authentication.LockedException":
                        throw new LockedException(body);

                    default:
                        throw new BadCredentialsException(body);
                }

            } else {
                throw new BadCredentialsException(body);
            }
        } else {
            LOG.error("Could not connect to the authentication server.  Verify the authentication server url and"
                    + " status of the authentication server.");
            throw new AuthenticationServiceException("Verify the authentication server url.");
        }
    }
}
