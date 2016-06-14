package com.rsa.asoc.sa.ui.common.authentication.provider;

import com.rsa.asoc.sa.ui.common.authentication.domain.bean.DetailedUserInfo;
import com.rsa.asoc.sa.ui.common.authentication.domain.bean.DetailedUserPasswordAuthenticationToken;
import com.rsa.asoc.sa.ui.common.config.SecuritySettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Use Security Analytics as authentication provider.  See {@link SecuritySettings} to configure the
 * server.url.
 *
 * @author Jay Garala
 * @since 10.6.0
 */
@Component
@Profile({"!development"})
public class SecurityAnalyticsAuthenticationProvider implements AuthenticationProvider {

    private static final Logger LOG = LoggerFactory.getLogger(SecurityAnalyticsAuthenticationProvider.class);

    private final RestTemplate restTemplate;

    private final String loginUrl;

    @Autowired
    public SecurityAnalyticsAuthenticationProvider(SecuritySettings securitySettings,
            RestTemplate restTemplate) {

        this.restTemplate = restTemplate;
        this.restTemplate.setErrorHandler(new AuthenticationResponseErrorHandler());

        loginUrl = securitySettings.getAuth().getServer().getUrl();
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        LOG.debug("Authenticating: %s", authentication.getPrincipal());

        MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
        request.add("username", authentication.getName());
        request.add("password", authentication.getCredentials().toString());
        try {
            ResponseEntity<DetailedUserInfo> userInfoResponseEntity =
                    restTemplate.postForEntity(new URI(loginUrl), request, DetailedUserInfo.class);

            DetailedUserInfo detailedUserInfo = userInfoResponseEntity.getBody();

            Set<GrantedAuthority> grantedAuthorities =
                    detailedUserInfo.getAuthorities().stream().map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toSet());

            return new DetailedUserPasswordAuthenticationToken(detailedUserInfo.getPrincipal(),
                    authentication.getCredentials(),
                    grantedAuthorities,
                    detailedUserInfo);

        } catch (URISyntaxException ex) {
            throw new AuthenticationServiceException(String.format("Invalid server url: %s",loginUrl), ex);
        } catch (RestClientException ex) {
            String msg = String.format("Failed to connect to the authentication server: %s",
                    ex.getMessage());
            LOG.error(msg, ex);
            throw new AuthenticationServiceException(msg, ex);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
