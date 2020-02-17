package presidio.s3.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.PredefinedClientConfigurations;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.s3.S3Shellcommands;
import presidio.s3.services.NWGatewayService;

@Configuration
@Import({
        S3Shellcommands.class
})
public class S3ServiceConfig {

    @Value("${aws.bucket.name}")
    private String bucketName;

    @Value("${aws.tenant}")
    private String tenant;

    @Value("${aws.account}")
    private String account;

    @Value("${aws.region}")
    private String region;

    @Bean
    public AmazonS3 amazonS3() {
        ClientConfiguration clientConfiguration = PredefinedClientConfigurations.defaultConfig();
        clientConfiguration.setMaxErrorRetry(10);
        return AmazonS3ClientBuilder.standard().withClientConfiguration(clientConfiguration).build();
    }

    @Bean
    public NWGatewayService nwGatewayService() {
        return new NWGatewayService(bucketName, tenant, account, region, amazonS3());
    }

}
