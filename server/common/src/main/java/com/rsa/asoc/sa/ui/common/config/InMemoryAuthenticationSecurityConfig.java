package com.rsa.asoc.sa.ui.common.config;

import com.rsa.asoc.sa.ui.common.environment.profile.Development;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

/**
 * Configures Spring Security with form-based login and an in-memory authentication manager.
 *
 * @author athielke
 */
@Configuration
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
@Development
public class InMemoryAuthenticationSecurityConfig extends BaseSecurityConfig {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.inMemoryAuthentication()
                .withUser("admin").password("netwitness").roles("ADMIN", "USER").and()
                .withUser("user").password("netwitness").roles("USER");
    }
}
