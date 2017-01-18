package fortscale.aggregation.feature.bucket.state.config;

import fortscale.aggregation.feature.bucket.repository.state.FeatureBucketStateRepository;
import fortscale.aggregation.feature.bucket.state.FeatureBucketStateService;
import fortscale.aggregation.feature.bucket.state.FeatureBucketStateServiceImpl;
import fortscale.utils.monitoring.stats.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeatureBucketStateServiceConfig {

    @Autowired
    private FeatureBucketStateRepository featureBucketStateRepository;

    @Autowired
    private StatsService statsService;

    @Bean
    public FeatureBucketStateService featureBucketStateService()
    {
        return new FeatureBucketStateServiceImpl(featureBucketStateRepository, statsService);
    }
}
