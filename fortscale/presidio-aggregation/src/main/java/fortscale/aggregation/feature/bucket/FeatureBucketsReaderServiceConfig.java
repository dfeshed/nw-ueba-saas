package fortscale.aggregation.feature.bucket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by barak_schuster on 6/29/17.
 */
@Configuration
public class FeatureBucketsReaderServiceConfig {
    @Bean
    public FeatureBucketsMongoStore featureBucketsMongoStore() {
        return new FeatureBucketsMongoStore();
    }
    @Bean
    public FeatureBucketsReaderService featureBucketsReaderService()
    {
        return new FeatureBucketsReaderService();
    }
}
