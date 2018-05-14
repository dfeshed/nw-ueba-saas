package presidio.rsa.auth.spring;

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
     */
    private File keyStoreLocation =  new File("C:/repositories/auth-verify/trust-store//keystore.p12") ;

    /**
     * Secret to open the keystore.
     *
     * Warning: this is for temporary usage. Not to be used in the end application
     */
    private String secret = "netwitness";

    public File getKeyStoreLocation() {
        return keyStoreLocation;
    }

    public String getSecret() {
        return secret;
    }
}
