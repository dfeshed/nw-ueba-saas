package presidio.rsa.auth.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Configuration
 *
 * @author Shay Schwartz
 */
@ConfigurationProperties
@Component
public class KeyStoreConfigProperties {

    /**
     * Location of the keystore
     * Todo: need to verify how to get the real certificate on integration environment + production
     */
    @Value("${nw.security.certificate.file.location:'C:/repositories/auth-verify/trust-store/keystore.p12'}")
    private String keyStoreLocation;


    /**
     * Secret to open the keystore.
     */
    @Value("${nw.security.certificate.secret:netwitness}")
    private String secret;

    public String getKeyStoreLocation() {
        return keyStoreLocation;
    }

    public String getSecret() {
        return secret;
    }
}
