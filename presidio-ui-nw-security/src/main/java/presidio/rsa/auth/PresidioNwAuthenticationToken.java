package presidio.rsa.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import presidio.rsa.auth.duplicates.Token;

import java.util.Collection;

public class PresidioNwAuthenticationToken implements Authentication {

    private Token token;

    public PresidioNwAuthenticationToken(Token token) {
        this.token = token;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return token.getGrantedAuthorities();
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getDetails() {
        return token.getUserName();
    }

    @Override
    public Object getPrincipal() {
        return token.getUserName();
    }

    @Override
    public boolean isAuthenticated() {
        return  token !=null;
    }

    @Override
    public void setAuthenticated(boolean b) throws IllegalArgumentException {

    }

    @Override
    public boolean equals(Object another) {
        return false;
    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String getName() {
        return null;
    }
}
