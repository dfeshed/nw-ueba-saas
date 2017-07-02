package fortscale.aggregation.feature.bucket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class FeatureBucketsInMemoryConfig {
    @Bean
    public FeatureBucketsInMemory FeatureBucketsInMemory() {
        return new FeatureBucketsInMemory();
    }
}

