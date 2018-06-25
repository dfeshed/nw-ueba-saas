package fortscale.configuration.encryption;

import fortscale.utils.EncryptionUtils;
import org.springframework.security.crypto.encrypt.TextEncryptor;

public class PresidioTextEncryptor implements TextEncryptor {

    @Override
    public String encrypt(String text) {
        try {
            return EncryptionUtils.encrypt(text);
        } catch (Exception ex) {
            throw new RuntimeException(String.format("failed to encrypt %s",text), ex);
        }
    }

    @Override
    public String decrypt(String text) {
        try {
            return EncryptionUtils.decrypt(text);
        } catch (Exception ex) {
            throw new RuntimeException(String.format("failed to decrypt %s",text), ex);
        }
    }
}
