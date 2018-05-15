package presidio.rsa.auth;


import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import presidio.rsa.auth.duplicates.Token;


import java.util.Optional;

public class PresidioNwTokenAuthenticationProvider implements AuthenticationProvider {

    private PresidioNwTokenService tokenService;

    private PresidioNwAuthService presidioNwAuthService;
    public PresidioNwTokenAuthenticationProvider(PresidioNwTokenService tokenService, PresidioNwAuthService presidioNwAuthService) {
        this.tokenService = tokenService;
        this.presidioNwAuthService = presidioNwAuthService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Optional<String> encodedJwtToken = (Optional) authentication.getPrincipal();
        if (!encodedJwtToken.isPresent() || encodedJwtToken.get().isEmpty()) {
            throw new BadCredentialsException("Invalid token");
        }
//        if (!tokenService.contains(encodedJwtToken.get())) {
//            throw new BadCredentialsException("Invalid token or token expired");
//        }
        PresidioNwAuthenticationToken authenticationWithToken =  tokenService.retrieve(encodedJwtToken.get());


        if (authenticationWithToken!=null){
            return authenticationWithToken;
        }



//        AuthenticationWithToken resultOfAuthentication = externalServiceAuthenticator.authenticate(username.get(), password.get());
//        String newToken = tokenService.generateNewToken();
//        resultOfAuthentication.setToken(newToken);
//        tokenService.store(newToken, resultOfAuthentication);
//        String encodedJwtToken = cookieBearerTokenExtractor.retrieveToken(request);

//        LOG.debug("token : {}", encodedJwtToken);


        try {
            Token token = presidioNwAuthService.verifyAccess(encodedJwtToken.get());

            PresidioNwAuthenticationToken resultOfAuthentication = new PresidioNwAuthenticationToken(token);
            tokenService.store(encodedJwtToken.get(), resultOfAuthentication);
            return resultOfAuthentication;
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid token or token expired");
        }


    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(PreAuthenticatedAuthenticationToken.class);
    }
}
