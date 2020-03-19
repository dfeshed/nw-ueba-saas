package kms;

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClient;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.bootstrap.encrypt.EnvironmentDecryptApplicationInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * This config must be applied to the bootstrap context, which is done by META-INF/spring.factories.<br/>
 * The properties here can be configured in bootstrap.[yml|xml|properties], but not in application.[yml]xml|properties]
 */
@Configuration
@ConditionalOnProperty(prefix = "aws.kms", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(KmsProperties.class)
public class KmsEncryptionConfiguration {
    public static final Logger logger = Logger.getLogger("KmsEncryptionConfiguration");

    private final KmsTextEncryptor kmsTextEncryptor;

    @Autowired
    public KmsEncryptionConfiguration(KmsTextEncryptor kmsTextEncryptor) {
        this.kmsTextEncryptor = kmsTextEncryptor;
    }

    @Bean
    public EnvironmentDecryptApplicationInitializer environmentDecryptApplicationInitializer() {
        return new EnvironmentDecryptApplicationInitializer(kmsTextEncryptor);
    }

    @Configuration
    public static class KmsTextEncryptorConfiguration {

        private final KmsProperties properties;

        private final AWSKMS kms;

        @Autowired
        public KmsTextEncryptorConfiguration(KmsProperties properties, AWSKMS kms) {
            this.properties = properties;
            this.kms = kms;
        }

        @Bean
        public KmsTextEncryptor kmsTextEncryptor() {
            return new KmsTextEncryptor(kms, properties.getKeyId());
        }
    }

    @Configuration
    @ConditionalOnMissingBean(AWSKMS.class)
    public static class KmsConfiguration {

        private final KmsProperties properties;

        @Autowired
        public KmsConfiguration(KmsProperties properties) {
            this.properties = properties;
        }

        @Bean
        public AWSKMS kms() {
            final AWSKMSClientBuilder builder = AWSKMSClient.builder();

            if (Optional.ofNullable(properties.getEndpoint()).isPresent()) {
                builder.withEndpointConfiguration(new EndpointConfiguration(properties.getEndpoint().getServiceEndpoint(), properties.getEndpoint().getSigningRegion()));
            } else {
                Optional.ofNullable(properties.getRegion()).ifPresent(builder::setRegion);
            }

            return builder.build();
        }

    }

}
