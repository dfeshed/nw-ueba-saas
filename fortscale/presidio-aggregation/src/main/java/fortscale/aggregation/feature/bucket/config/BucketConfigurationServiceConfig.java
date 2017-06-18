package fortscale.aggregation.feature.bucket.config;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by barak_schuster on 10/8/16.
 */
@Configuration
public class BucketConfigurationServiceConfig {
    @Bean
    public BucketConfigurationService bucketConfigurationService()
    {
        return new BucketConfigurationService();
    }
}
