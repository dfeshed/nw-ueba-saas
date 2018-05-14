package presidio.rsa.auth;


import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;


/**
 * CookieBearerTokenExtractor inspects request for access_token
 */
@Component
public class CookieBearerTokenExtractor extends BearerTokenExtractor {

    public String retrieveToken(HttpServletRequest request) {
        String tokenValue = extractToken(request);
        if (tokenValue == null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(OAuth2AccessToken.ACCESS_TOKEN)) {
                    tokenValue = cookie.getValue();
                    break;
                }
            }
        }

        return tokenValue;
    }
}

