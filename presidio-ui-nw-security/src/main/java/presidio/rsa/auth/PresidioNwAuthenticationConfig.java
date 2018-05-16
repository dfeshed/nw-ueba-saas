package presidio.rsa.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import presidio.rsa.auth.spring.KeyStoreConfigProperties;
import presidio.rsa.auth.token.bearer.CookieBearerTokenExtractor;

import javax.servlet.http.HttpServletResponse;


@Configuration
@EnableScheduling
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class PresidioNwAuthenticationConfig extends WebSecurityConfigurerAdapter {

    @Value("${presidio.ui.role:presidio-ui}")
    private String presidioUiRoleName;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.
                csrf().disable().
                sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).
                and().
                authorizeRequests().

                anyRequest().hasAnyRole(presidioUiRoleName).

                and().
                anonymous().disable().
                exceptionHandling().authenticationEntryPoint(unauthorizedEntryPoint());

        http.addFilterBefore(new PresidioNwAuthenticationFilter(authenticationManager(),cookieBearerTokenExtractor()), BasicAuthenticationFilter.class)

        ;
    }



    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.
                authenticationProvider(tokenAuthenticationProvider());
    }
    @Bean
    KeyStoreConfigProperties keyStoreConfigProperties(){
        return new KeyStoreConfigProperties();
    }

    @Bean
    CookieBearerTokenExtractor cookieBearerTokenExtractor(){
        return new CookieBearerTokenExtractor();
    }

    @Bean
    public PresidioNwTokenService tokenService() {
        return new PresidioNwTokenService();
    }



    @Bean
    public AuthenticationProvider tokenAuthenticationProvider() {
        return new PresidioNwTokenAuthenticationProvider(tokenService(),presidioNwAuthService());
    }

    @Bean
    PresidioNwAuthService presidioNwAuthService(){
        return new PresidioNwAuthServiceImpl(keyStoreConfigProperties());
    }

    @Bean
    public AuthenticationEntryPoint unauthorizedEntryPoint() {
        return (request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }


    @Bean
    RoleHierarchy roleHierarchy() {
        return new PresidioNwRoleHierarchy();

    }
}