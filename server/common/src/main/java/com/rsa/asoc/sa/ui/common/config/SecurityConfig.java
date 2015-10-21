package com.rsa.asoc.sa.ui.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rsa.asoc.sa.ui.security.RestAuthenticationHandler;
import com.rsa.asoc.sa.ui.security.UnauthorizedAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;

/**
 * Configures Spring Security with form-based login and an in-memory authentication manager.
 *
 * @author athielke
 */
@Configuration
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String LOGIN_PATH = "/api/user/login";
    private static final String LOGOUT_PATH = "/api/user/logout";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EndpointBuilder endpoint;

    @Bean
    public RestAuthenticationHandler restAuthenticationHandler() {
        return new RestAuthenticationHandler(objectMapper);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf()
                    .requireCsrfProtectionMatcher(AnyRequestMatcher.INSTANCE)
                    .ignoringAntMatchers(LOGIN_PATH, endpoint.anyWebSocketRequestAntMatcher())
                    .and()
                .formLogin()
                    .loginPage(LOGIN_PATH)
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .successHandler(restAuthenticationHandler())
                    .failureHandler(restAuthenticationHandler())
                    .and()
                .logout()
                    .logoutUrl(LOGOUT_PATH)
                    .logoutSuccessHandler(restAuthenticationHandler())
                    .deleteCookies("JSESSIONID")
                    .and()
                .httpBasic()
                    .and()
                .exceptionHandling()
                    .authenticationEntryPoint(new UnauthorizedAuthenticationEntryPoint())
                    .and()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                    .and()
                .authorizeRequests()
                    .antMatchers(LOGIN_PATH).permitAll()
                    .anyRequest().authenticated();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("admin").password("netwitness").roles("ADMIN", "USER").and()
                .withUser("user").password("netwitness").roles("USER");
    }
}
