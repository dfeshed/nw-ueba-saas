//package presidio.rsa.auth.spring;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import presidio.rsa.auth.CookieBearerTokenExtractor;
//import presidio.rsa.auth.PresidioNwAuthService;
//import presidio.rsa.auth.PresidioNwAuthServiceImpl;
//import presidio.rsa.auth.PresidioUiAuthenticationProvieder;
//
//
///**
// * Override the config to turn off basic auth.
// * @author Shay Schwartz
// * All apps should use it or extned it.
// */
//@Configuration
//@Profile("secured")
//public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
//
//    WebSecurityConfiguration() {
//        super(true);
//    }
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        PresidioUiAuthenticationProvieder presidioUiAuthenticationProvieder = new PresidioUiAuthenticationProvieder();
//
//        http.httpBasic().disable();
//
//
////        http.addFilterBefore(jwtAuthFilter(), JwtAuthFilter.class);
//        http.authenticationProvider( presidioUiAuthenticationProvieder );
//        http.csrf().disable();
//        // setup security
//        http.authorizeRequests()
//                .anyRequest()
//                .fullyAuthenticated();
//        http.csrf().disable()
//                .authorizeRequests()
//                .anyRequest().authenticated()
//                .and();
//        /*.addFilterBefore(customUsernamePasswordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)*/;
//
//
//
//    }
//
//    @Bean
//    KeyStoreConfigProperties keyStoreConfigProperties(){
//        return new KeyStoreConfigProperties();
//    }
//
//    @Bean
//    CookieBearerTokenExtractor cookieBearerTokenExtractor(){
//        return new CookieBearerTokenExtractor();
//    }
//
//    @Bean
//    PresidioNwAuthService presidioNwAuthService(){
//        return new PresidioNwAuthServiceImpl(keyStoreConfigProperties());
//    }
//
//    @Bean
//    JwtAuthFilter jwtAuthFilter() throws Exception {
//        JwtAuthFilter jwtAuthFilter = new JwtAuthFilter(cookieBearerTokenExtractor(),presidioNwAuthService());
//        jwtAuthFilter.setAuthenticationManager(authenticationManager());
//        return jwtAuthFilter;
//    }
//
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        PresidioUiAuthenticationProvieder presidioUiAuthenticationProvieder = new PresidioUiAuthenticationProvieder();
//        auth.authenticationProvider(presidioUiAuthenticationProvieder);
//
//    }
//}