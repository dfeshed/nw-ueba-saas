package presidio.rsa.auth;


import com.rsa.jsafe.crypto.CryptoJ;
import com.rsa.jsafe.crypto.CryptoJVersion;
import com.rsa.jsafe.provider.JsafeJCE;
import com.rsa.jsse.JsseProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.jwt.crypto.sign.SignatureVerifier;
import org.springframework.stereotype.Service;
import presidio.rsa.auth.duplicates.LaunchUtils;
import presidio.rsa.auth.duplicates.ServiceInterruptException;
import presidio.rsa.auth.duplicates.Token;
import presidio.rsa.auth.spring.KeyStoreConfigProperties;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A service that validates incoming token and and checks if the user has required roles.
 *
 * @author Anu Upputuri
 * @since 0.1.0
 * TODO: https://bedfordjira.na.rsa.net/browse/ASOC-55722
 */
@Service
public class PresidioNwAuthServiceImpl implements PresidioNwAuthService{

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * The service keystore uses the PKCS#12 format
     */
    static final String FORMAT = "PKCS12";


    private KeyStoreConfigProperties configProperties;

    /**
     * The registered token verifiers.
     */
    private List<RsaVerifier> tokenVerifiers;

    private KeyStore keyStore;

    /**
     * The approved JCE providers.
     */
    private static final Set<String> APPROVED;
    static {

        APPROVED = Collections.unmodifiableSet(
                new HashSet(
                        Arrays.asList("JsafeJCE", "RsaJsse", "SUN", "SunJSSE")
                )

        );
    }


    @Autowired
    public PresidioNwAuthServiceImpl(KeyStoreConfigProperties configProperties) {
        this.configProperties = configProperties;

    }


    @Override
    @PostConstruct
    public void initializeKeyStore() throws GeneralSecurityException,
            IOException, ServiceInterruptException {

        File keyStoreFile =  new File(configProperties.getKeyStoreLocation()) ;
        if (!keyStoreFile.exists()) {
            throw new IllegalArgumentException(configProperties.getKeyStoreLocation() + " does not exist");
        }

        initializeCryptoJ(CryptoJ.NON_FIPS140_MODE, false);
        keyStore = KeyStore.getInstance(FORMAT);

        try (FileInputStream fis = new FileInputStream(configProperties.getKeyStoreLocation())) {
            keyStore.load(fis, configProperties.getSecret().toCharArray());
        }

        loadTokenVerifiers();
    }

    /**
     * Verifies the encoded token signature and returns the decoded jwt token if valid.
     */
    @Override
    public Token verifyAccess(String encodedJwtToken) throws GeneralSecurityException {

        Jwt jwt = JwtHelper.decode(encodedJwtToken);

        Token token = LaunchUtils.fromJson(JwtHelper.decode(encodedJwtToken).getClaims(), Token.class);

        // Expired tokens are no good.
        if (token.isExpired()) {
            throw new CredentialsExpiredException("Expired Token");
        }

        // Is this a refresh token and we didn't issue it
        if (token.isRefresh()) {
            logger.warn("Fortscale does not support refresh tokens");
        }

        logger.info("Authentication Token {}", token);

        // Not expired, but can anyone vouch for it.
        boolean valid = tokenVerifiers.stream()
                .anyMatch(verifier -> verifySignature(jwt, token, verifier));

        if (valid){
            return token;
        }
        else {
            return null;
        }
    }

    /**
     * Verifies the digital signature of the jwt token
     */
    public boolean verifySignature(Jwt token, Token claimed, SignatureVerifier verifier) {
        try {
            token.verifySignature(verifier);
            logger.info("Accepted token for {} issued by {}", claimed.getUserName(), claimed.getIss());
            return true;
        }
        catch (Exception e) {
            logger.trace("Token verification failure", e);
            return false;
        }
    }

    private void loadTokenVerifiers() throws GeneralSecurityException {

        // Get the certificates the service uses itself.
        List<X509Certificate> chain = getCertificateChain(this.configProperties.getAlias());

        // Set verifiers to accept signatures from the self and all trusted keys
        tokenVerifiers = Stream.concat(Stream.of(chain.get(0)), getTrustedCertificates().stream())
                .map(Certificate::getPublicKey)
                .filter(x -> x instanceof RSAPublicKey)
                .map(RSAPublicKey.class::cast)
                .map(RsaVerifier::new)
                .collect(Collectors.toList());

        logger.debug("Token verifiers size:" + tokenVerifiers.size());

    }

    public synchronized List<X509Certificate> getCertificateChain(String alias) throws GeneralSecurityException {
        return Arrays.stream(keyStore.getCertificateChain(alias))
                .filter(c -> c instanceof X509Certificate)
                .map(X509Certificate.class::cast)
                .collect(Collectors.toList());
    }

    /**
     * Get the currently trusted certificates.
     */

    public synchronized List<X509Certificate> getTrustedCertificates() throws GeneralSecurityException {


        List<X509Certificate> result = new ArrayList<>();
        Enumeration<String> aliases = keyStore.aliases();

        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();

            if (keyStore.isCertificateEntry(alias)) {
                pickX509From(Stream.of(keyStore.getCertificate(alias))).forEach(result::add);
            }
        }

        return result;
    }


    private void initializeCryptoJ(int mode, boolean onlyApproved) throws ServiceInterruptException {
        try {
            // Set the crypto mode based on FIPS stance
            CryptoJ.setMode(mode);

            // Make RSA Crypto the preferred provider
            JsafeJCE jsafeJce = new JsafeJCE();
            JsseProvider jsseProvider = new JsseProvider();
            Security.insertProviderAt(jsafeJce, 1);
            Security.insertProviderAt(jsseProvider, 2);

            //If we must use only approved providers exclusively
            if (onlyApproved) {
                // Remove the others
                Arrays.stream(Security.getProviders())
                        .filter(p -> !APPROVED.contains(p.getName()))
                        .forEach(p -> Security.removeProvider(p.getName()));
            }

            logger.info("Initialized service cryptography with {} providers (BSAFE={}, FIPS-140={}).",
                    Security.getProviders().length, CryptoJVersion.getProductID(), CryptoJ.isFIPS140Compliant());

            // Register this class for crypto-events
//            CryptoLoggerConfig.addListener(new SecurityAuditor());
        }
        catch (Exception e) {
            throw new ServiceInterruptException("Crypto initialization", e);
        }
    }



    /**
     * Extract X509 certificates from the incoming stream.
     */
    private static Stream<X509Certificate> pickX509From(Stream<?> stream) {
        return stream.filter(x -> x instanceof X509Certificate).map(X509Certificate.class::cast);
    }

}
