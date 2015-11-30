package com.rsa.asoc.sa.ui.common.authentication.provider;

import com.rsa.asoc.sa.ui.common.authentication.domain.bean.DetailedUserPasswordAuthenticationToken;
import com.rsa.asoc.sa.ui.common.config.SecuritySettings;
import java.util.Collections;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link SecurityAnalyticsAuthenticationProvider}
 *
 * @author Jay Garala
 * @since 10.6.0
 */
public class SecurityAnalyticsAuthenticationProviderTest {

    RestTemplate restTemplate = new RestTemplate();

    MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);

    SecurityAnalyticsAuthenticationProvider provider;

    Authentication authentication = new UsernamePasswordAuthenticationToken("admin", "netwitness");

    @Before
    public void init() {
        provider = new SecurityAnalyticsAuthenticationProvider(new SecuritySettings(), restTemplate);
    }

    @Test(expected = AuthenticationServiceException.class)
    public void testBadUrl() throws Exception {
        String badUrl = "dfdf:\foo()";
        SecuritySettings securitySettings = new SecuritySettings();
        securitySettings.getAuth().getServer().setUrl(badUrl);
        provider = new SecurityAnalyticsAuthenticationProvider(securitySettings, restTemplate);

        mockServer.expect(requestTo(badUrl)).andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("OK", MediaType.TEXT_PLAIN));

        provider.authenticate(authentication);
    }

    @Test
    public void testSuccessfulAuthentication() throws Exception {
        String json =
                "{\"name\":\"admin\",\"principal\":{\"username\":\"admin\",\"password\":null," +
                        "\"authorities\":[{\"authority\":\"Administrators\"},{\"authority\":\"Administrators\"}," +
                        "{\"authority\":\"PRIVILEGED_CONNECTION_AUTHORITY\"}],\"enabled\":true," +
                        "\"accountNonExpired\":true,\"credentialsNonExpired\":true,\"accountNonLocked\":true}," +
                        "\"credentials\":null,\"displayName\":\"Administrator\",\"description\":\"System " +
                        "Administrator\",\"authorities\":[\"PRIVILEGED_CONNECTION_AUTHORITY\",\"Administrators\"]," +
                        "\"details\":{\"username\":\"admin\",\"password\":null," +
                        "\"authorities\":[{\"authority\":\"Administrators\"},{\"authority\":\"Administrators\"}," +
                        "{\"authority\":\"PRIVILEGED_CONNECTION_AUTHORITY\"}],\"enabled\":true," +
                        "\"accountNonExpired\":true,\"credentialsNonExpired\":true,\"accountNonLocked\":true}," +
                        "\"permissions\":[\"accessViewAndManageIncidents\",\"exportList\"," +
                        "\"deleteAlertsAndIncidents\"]}";

        mockServer.expect(requestTo("https://localhost/api/auth/login")).andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        Authentication auth = provider.authenticate(authentication);

        assertTrue(auth instanceof DetailedUserPasswordAuthenticationToken);
        DetailedUserPasswordAuthenticationToken dupa = (DetailedUserPasswordAuthenticationToken) auth;
        Object principal = auth.getPrincipal();
        if ( principal instanceof String) {
            assertEquals("admin", auth.getPrincipal());

        } else {
            assertEquals("admin", ((Map)auth.getPrincipal()).get("username"));
        }
        assertEquals(2, auth.getAuthorities().size());
        assertTrue(dupa.getPermissions().size() > 0);
    }

    @Test(expected = BadCredentialsException.class)
    public void testBadCredentials() throws Exception {
        testError("Bad credentials", "org.springframework.security.authentication.BadCredentialsException");
    }

    @Test(expected = LockedException.class)
    public void testAccountLocked() throws Exception {
        testError("User account is locked", "org.springframework.security.authentication.LockedException");
    }

    @Test(expected = DisabledException.class)
    public void testAccountDisabled() throws Exception {
        testError("User account is disabled", "org.springframework.security.authentication.DisabledException");
    }

    @Test(expected = BadCredentialsException.class)
    public void testOtherError() throws Exception {
        testError("Something else went wrong", null);
    }

    private void testError(String message, String exceptionClass) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        if (exceptionClass != null) {
            headers.put("X-Authentication-Exception", Collections.singletonList(exceptionClass));
        }

        mockServer.expect(requestTo("https://localhost/api/auth/login")).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED).headers(headers).body(message));

        provider.authenticate(authentication);
    }
}