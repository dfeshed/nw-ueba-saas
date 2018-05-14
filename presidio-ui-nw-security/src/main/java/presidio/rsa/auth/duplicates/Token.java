package presidio.rsa.auth.duplicates;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.Serializable;
import java.security.Principal;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

/**
 * An authentication token
 *
 * @author Aldrin D'Souza
 * @since 0.13
 */

@JsonInclude(NON_DEFAULT)
public final class Token implements Principal, Serializable {

    /**
     * The subject.
     */
    @JsonProperty("user_name")
    private String userName;

    /**
     * The expiration time. https://tools.ietf.org/html/rfc7519#section-4.1.4
     */
    private long exp;

    /**
     * The issuer. See https://tools.ietf.org/html/rfc7519#section-4.1.1
     */
    private String iss;

    /**
     * Issued at. See https://tools.ietf.org/html/rfc7519#section-4.1.6
     */
    private long iat;

    /**
     * The user roles.
     */
    private Set<String> authorities;

    /**
     * Is this a refresh token?
     */
    private boolean refresh;

    public Token() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getExp() {
        return exp;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    public String getIss() {
        return iss;
    }

    public void setIss(String iss) {
        this.iss = iss;
    }

    public long getIat() {
        return iat;
    }

    public void setIat(long iat) {
        this.iat = iat;
    }

    public Set<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<String> authorities) {
        this.authorities = authorities;
    }

    public boolean isRefresh() {
        return refresh;
    }

    public void setRefresh(boolean refresh) {
        this.refresh = refresh;
    }

    /**
     * Is the token valid?
     */
    @JsonIgnore
    public boolean isExpired() {
        return exp < System.currentTimeMillis();
    }

    /**
     * Translate the roles to authorities.
     */
    @JsonIgnore
    public Set<GrantedAuthority> getGrantedAuthorities() {

        if (authorities == null) {
            return Collections.emptySet();
        }

        return authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
    }

    /**
     * A Token is a {@link Principal}
     *
     * @return The principal name.
     */
    @Override
    @JsonIgnore
    public String getName() {
        return userName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return exp == token.exp &&
                iat == token.iat &&
                refresh == token.refresh &&
                Objects.equals(userName, token.userName) &&
                Objects.equals(iss, token.iss) &&
                Objects.equals(authorities, token.authorities);
    }

    @Override
    public int hashCode() {

        return Objects.hash(userName, exp, iss, iat, authorities, refresh);
    }
}