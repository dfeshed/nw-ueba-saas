package presidio.rsa.auth;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import presidio.rsa.auth.duplicates.Token;



public class PresidioUiAuthenticationProvieder implements AuthenticationProvider {


    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {

          if (authentication !=null && authentication.isAuthenticated()){
              return authentication;
          } else {
              return null;
          }

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(
                PresidioUiNwAuthenticationToken.class);
    }
}
