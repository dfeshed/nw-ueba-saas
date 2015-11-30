package com.rsa.asoc.sa.ui.common.config;

import com.rsa.asoc.sa.ui.common.authentication.provider.SecurityAnalyticsAuthenticationProvider;
import com.rsa.asoc.sa.ui.common.web.client.SslSupportingHttpClientBuilder;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.web.client.RestTemplate;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;

/**
 * Configures Spring Security with form-based login and authentication using Security Analytics
 *
 * @author Jay Garala
 * @since 10.6.0
 */
@Configuration
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
@Profile({"!development"})
public class ExternalAuthenticationSecurityConfig extends BaseSecurityConfig {

    @Bean
    public SecuritySettings securitySettings() {
        return new SecuritySettings();
    }

    @Bean
    public RestTemplate restTemplate() {
        SecuritySettings securitySettings = securitySettings();

        HttpClient httpClient;
        if (securitySettings.getAuth().getServer().isAllowInsecureSsl()) {
            httpClient =
                    SslSupportingHttpClientBuilder.create().doNotVerifyHostname().trustSelfSignedSslCerts().build();
        } else {
            httpClient = SslSupportingHttpClientBuilder.create().build();
        }

        return new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
    }

    @Bean
    public SecurityAnalyticsAuthenticationProvider authenticationProvider() {
        return new SecurityAnalyticsAuthenticationProvider(securitySettings(), restTemplate());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider()).eraseCredentials(true);
    }
}
