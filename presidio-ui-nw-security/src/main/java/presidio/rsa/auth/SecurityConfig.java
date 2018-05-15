package presidio.rsa.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import presidio.rsa.auth.spring.KeyStoreConfigProperties;

import javax.servlet.http.HttpServletResponse;

@Configuration
//@EnableWebMvcSecurity
@EnableScheduling
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

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
                anyRequest().authenticated().
                and().
                anonymous().disable().
                exceptionHandling().authenticationEntryPoint(unauthorizedEntryPoint());

        http.addFilterBefore(new AuthenticationFilter(authenticationManager(),cookieBearerTokenExtractor()), BasicAuthenticationFilter.class)
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
    public TokenService tokenService() {
        return new TokenService();
    }

//    @Bean
//    public ExternalServiceAuthenticator someExternalServiceAuthenticator() {
//        return new SomeExternalServiceAuthenticator();
//    }

//    @Bean
//    public AuthenticationProvider domainUsernamePasswordAuthenticationProvider() {
//        return new DomainUsernamePasswordAuthenticationProvider(tokenService(), someExternalServiceAuthenticator());
//    }

//    @Bean
//    public AuthenticationProvider backendAdminUsernamePasswordAuthenticationProvider() {
//        return new BackendAdminUsernamePasswordAuthenticationProvider();
//    }

    @Bean
    public AuthenticationProvider tokenAuthenticationProvider() {
        return new TokenAuthenticationProvider(tokenService(),presidioNwAuthService());
    }

    @Bean
    PresidioNwAuthService presidioNwAuthService(){
        return new PresidioNwAuthServiceImpl(keyStoreConfigProperties());
    }

    @Bean
    public AuthenticationEntryPoint unauthorizedEntryPoint() {
        return (request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }



//      <cache name="restApiAuthTokenCache" eternal="false" maxElementsInMemory="0" overflowToDisk="false"
//           timeToLiveSeconds="14400" memoryStoreEvictionPolicy="LRU">
//        <persistence strategy="none"/>
//    </cache>
}