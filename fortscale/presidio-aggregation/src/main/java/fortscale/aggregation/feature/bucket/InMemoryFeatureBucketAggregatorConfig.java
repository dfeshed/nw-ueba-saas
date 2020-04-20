package fortscale.aggregation.feature.bucket;

import fortscale.aggregation.feature.bucket.metrics.FeatureBucketAggregatorMetricsContainer;
import fortscale.aggregation.feature.bucket.metrics.FeatureBucketAggregatorMetricsContainerConfig;
import fortscale.utils.recordreader.RecordReaderFactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(FeatureBucketAggregatorMetricsContainerConfig.class)
public class InMemoryFeatureBucketAggregatorConfig {
    private final BucketConfigurationService bucketConfigurationService;
    private final RecordReaderFactoryService recordReaderFactoryService;
    private final FeatureBucketAggregatorMetricsContainer featureBucketAggregatorMetricsContainer;

    @Autowired
    public InMemoryFeatureBucketAggregatorConfig(
            BucketConfigurationService bucketConfigurationService,
            RecordReaderFactoryService recordReaderFactoryService,
            FeatureBucketAggregatorMetricsContainer featureBucketAggregatorMetricsContainer) {

        this.bucketConfigurationService = bucketConfigurationService;
        this.recordReaderFactoryService = recordReaderFactoryService;
        this.featureBucketAggregatorMetricsContainer = featureBucketAggregatorMetricsContainer;
    }

    @Bean
    public InMemoryFeatureBucketAggregator inMemoryFeatureBucketAggregator() {
        return new InMemoryFeatureBucketAggregator(
                bucketConfigurationService,
                recordReaderFactoryService,
                featureBucketAggregatorMetricsContainer);
    }
}
