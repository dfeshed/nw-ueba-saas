package presidio.rsa.auth.mock;


import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import presidio.rsa.auth.PresidioNwAuthService;
import presidio.rsa.auth.PresidioNwAuthenticationToken;
import presidio.rsa.auth.PresidioNwTokenService;
import presidio.rsa.auth.duplicates.Token;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

public class PresidioNwTokenAuthenticationMockProvider implements AuthenticationProvider {

    private String grantedAuthority;

    public PresidioNwTokenAuthenticationMockProvider( String grantedAuthority) {
        this.grantedAuthority = grantedAuthority;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Optional<String> encodedJwtToken = (Optional) authentication.getPrincipal();
        if (!encodedJwtToken.isPresent() || encodedJwtToken.get().isEmpty()) {
            throw new BadCredentialsException("Invalid token");
        }

        Token token = new Token();
        token.setAuthorities(new HashSet<>(Arrays.asList(grantedAuthority)));
        token.setExp(LocalDateTime.now().plusDays(365).toInstant(ZoneOffset.UTC).getEpochSecond());
        token.setRefresh(false);
        token.setUserName("Mock User Name");
        return new PresidioNwAuthenticationToken(token);



    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(PreAuthenticatedAuthenticationToken.class);
    }
}
