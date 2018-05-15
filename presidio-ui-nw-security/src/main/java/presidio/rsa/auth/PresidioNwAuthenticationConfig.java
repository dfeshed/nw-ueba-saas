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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.CollectionUtils;
import presidio.rsa.auth.spring.KeyStoreConfigProperties;
import presidio.rsa.auth.token.bearer.CookieBearerTokenExtractor;

import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Configuration
//@EnableWebMvcSecurity
@EnableScheduling
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class PresidioNwAuthenticationConfig extends WebSecurityConfigurerAdapter {

    @Value("${presidio.user.role}")
    private String presidioUiUser;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.
                csrf().disable().
                sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).
                and().
                authorizeRequests().
//                antMatchers(actuatorEndpoints()).hasRole(backendAdminRole).
                anyRequest().hasAnyRole("Administrators").
                and().
                anonymous().disable().
                exceptionHandling().authenticationEntryPoint(unauthorizedEntryPoint());

        http.addFilterBefore(new PresidioNwAuthenticationFilter(authenticationManager(),cookieBearerTokenExtractor()), BasicAuthenticationFilter.class)
//                .addFilterBefore(new ManagementEndpointAuthenticationFilter(authenticationManager()), BasicAuthenticationFilter.class)
        ;
    }

//    private String[] actuatorEndpoints() {
//        return new String[]{ApiController.AUTOCONFIG_ENDPOINT, ApiController.BEANS_ENDPOINT, ApiController.CONFIGPROPS_ENDPOINT,
//                ApiController.ENV_ENDPOINT, ApiController.MAPPINGS_ENDPOINT,
//                ApiController.METRICS_ENDPOINT, ApiController.SHUTDOWN_ENDPOINT};
//    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.
//        authenticationProvider(domainUsernamePasswordAuthenticationProvider()).
//                authenticationProvider(backendAdminUsernamePasswordAuthenticationProvider()).
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