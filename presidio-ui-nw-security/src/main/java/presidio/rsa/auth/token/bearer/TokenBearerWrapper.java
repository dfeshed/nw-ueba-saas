package presidio.rsa.auth.token.bearer;

public class TokenBearerWrapper {
    private String token;
    private TokenBearerOrigin origin;

    public TokenBearerWrapper(String token, TokenBearerOrigin origin) {
        this.token = token;
        this.origin = origin;
    }

    public String getToken() {
        return token;
    }

    public TokenBearerOrigin getOrigin() {
        return origin;
    }
}
