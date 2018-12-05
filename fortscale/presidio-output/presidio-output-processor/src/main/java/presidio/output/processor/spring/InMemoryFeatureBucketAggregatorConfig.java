package presidio.output.processor.spring;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.InMemoryFeatureBucketAggregator;
import fortscale.aggregation.feature.bucket.metrics.FeatureBucketAggregatorMetricsContainer;
import fortscale.aggregation.feature.bucket.metrics.FeatureBucketAggregatorMetricsContainerConfig;
import fortscale.utils.recordreader.RecordReaderFactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.sdk.aggregation_records.BucketConfigurationServiceConfig;

@Configuration
@Import({
        BucketConfigurationServiceConfig.class,
        FeatureBucketAggregatorMetricsContainerConfig.class
})
public class InMemoryFeatureBucketAggregatorConfig {
    @Autowired
    private BucketConfigurationService bucketConfigurationService;

    @Autowired
    private RecordReaderFactoryService recordReaderFactoryService;

    @Autowired
    private FeatureBucketAggregatorMetricsContainer featureBucketAggregatorMetricsContainer;

    @Bean
    public InMemoryFeatureBucketAggregator inMemoryFeatureBucketAggregator() {
        return new InMemoryFeatureBucketAggregator(
                bucketConfigurationService,
                recordReaderFactoryService,
                featureBucketAggregatorMetricsContainer);
    }
}
