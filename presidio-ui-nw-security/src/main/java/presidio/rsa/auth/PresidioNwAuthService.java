package presidio.rsa.auth;


import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.crypto.sign.SignatureVerifier;
import presidio.rsa.auth.duplicates.ServiceInterruptException;
import presidio.rsa.auth.duplicates.Token;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;



import java.util.List;

public interface PresidioNwAuthService {
    void initializeKeyStore() throws GeneralSecurityException, IOException, ServiceInterruptException;
    Token verifyAccess(String encodedJwtToken) throws GeneralSecurityException;
    boolean verifySignature(Jwt token, Token claimed, SignatureVerifier verifier);
    List<X509Certificate> getTrustedCertificates() throws GeneralSecurityException;



}
