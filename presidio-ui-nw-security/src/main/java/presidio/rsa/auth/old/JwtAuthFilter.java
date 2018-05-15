//package presidio.rsa.auth.spring;
//
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
//import org.springframework.stereotype.Component;
//import presidio.rsa.auth.CookieBearerTokenExtractor;
//import presidio.rsa.auth.PresidioNwAuthService;
//import presidio.rsa.auth.PresidioUiNwAuthenticationToken;
//import presidio.rsa.auth.duplicates.Token;
//
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import java.security.GeneralSecurityException;
//
//@Component
//public class JwtAuthFilter extends AbstractAuthenticationProcessingFilter {
//
//    private  final Logger logger = LoggerFactory.getLogger(this.getClass());
//    CookieBearerTokenExtractor cookieBearerTokenExtractor;
//    PresidioNwAuthService keyStoreService;
//
//
//    public JwtAuthFilter(CookieBearerTokenExtractor cookieBearerTokenExtractor, PresidioNwAuthService keyStoreService) {
//        super( "/*" );
//        this.cookieBearerTokenExtractor = cookieBearerTokenExtractor;
//        this.keyStoreService = keyStoreService;
//    }
//
//
//    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response ) throws AuthenticationException
//    {
//
//        String encodedJwtToken = cookieBearerTokenExtractor.retrieveToken((HttpServletRequest)request);
//
//        logger.debug("token : {}", encodedJwtToken);
//
//
//        Token token = null;
//        try {
//            token = keyStoreService.verifyAccess(encodedJwtToken);
//        } catch (GeneralSecurityException e) {
//            e.printStackTrace();
//        }
//
//
//        // return a new authentication token to be processed by the authentication provider
//        return new PresidioUiNwAuthenticationToken(token);
//    }
//
////    public void init(FilterConfig filterConfig) throws ServletException {
////
////    }
////
////    @Override
////    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//////        HttpServletRequest servletRequest = (HttpServletRequest) request;
//////        String authorization = servletRequest.getHeader("Authorization");
//////        if (authorization != null) {
//////            JwtAuthToken token = new JwtAuthToken(authorization.replaceAll("Bearer ", ""));
//////            SecurityContextHolder.getContext().setAuthentication(token);
//////        }
////        String encodedJwtToken = cookieBearerTokenExtractor.retrieveToken((HttpServletRequest)request);
////
////        logger.debug("token : {}", encodedJwtToken);
////
////
////        Token token = null;
////        try {
////            token = keyStoreService.verifyAccess(encodedJwtToken);
////        } catch (GeneralSecurityException e) {
////            e.printStackTrace();
////        }
////
////        if (token == null) {
//////            return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).build();
////        }
////        else {
////
////            SecurityContextHolder.getContext().setAuthentication(new PresidioUiNwAuthenticationToken(token));
////
////        }
////        chain.doFilter(request, response);
////
////    }
////
////    @Override
////    public void destroy() {
////
////    }
//}
//
