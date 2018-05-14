package com.futureprocessing.spring.infrastructure.security;


import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import presidio.rsa.auth.PresidioNwAuthService;
import presidio.rsa.auth.PresidioUiNwAuthenticationToken;
import presidio.rsa.auth.duplicates.Token;

import java.security.GeneralSecurityException;
//import java.util.Collection;
import java.util.Optional;

public class TokenAuthenticationProvider implements AuthenticationProvider {

    private TokenService tokenService;

    private PresidioNwAuthService presidioNwAuthService;
    public TokenAuthenticationProvider(TokenService tokenService, PresidioNwAuthService presidioNwAuthService) {
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
        PresidioUiNwAuthenticationToken authenticationWithToken =  tokenService.retrieve(encodedJwtToken.get());

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

            PresidioUiNwAuthenticationToken resultOfAuthentication = new PresidioUiNwAuthenticationToken(token);
            tokenService.store(encodedJwtToken.get(), resultOfAuthentication);
            return resultOfAuthentication;
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        throw new BadCredentialsException("Invalid token or token expired");

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(PreAuthenticatedAuthenticationToken.class);
    }
}
