package presidio.utils.s3.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.utils.s3.services.NWGatewayService;

@Configuration
@Import({
        NWGatewayService.class
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
    public NWGatewayService nwGatwayService(){
        return new NWGatewayService(bucketName, tenant, account, region);
    }

}
