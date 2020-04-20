package presidio.ade.processes.shell.config;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.InMemoryFeatureBucketAggregator;
import fortscale.aggregation.feature.bucket.metrics.FeatureBucketAggregatorMetricsContainer;
import fortscale.aggregation.feature.bucket.metrics.FeatureBucketAggregatorMetricsContainerConfig;
import presidio.ade.domain.record.AdeRecordReaderFactoriesConfig;
import presidio.ade.domain.record.RecordReaderFactoryServiceConfig;
import presidio.ade.domain.record.TransformationConfig;
import fortscale.utils.recordreader.RecordReaderFactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by YaronDL on 7/2/2017.
 */
@Configuration
@Import({FeatureAggregationBucketConfigurationServiceConfig.class,
        TransformationConfig.class,
        RecordReaderFactoryServiceConfig.class,
        AdeRecordReaderFactoriesConfig.class,
        FeatureBucketAggregatorMetricsContainerConfig.class
})
public class InMemoryFeatureAggregatorConfig {

    @Autowired
    @Qualifier("bucketConfigurationService")
    private BucketConfigurationService bucketConfigurationService;
    @Autowired
    private RecordReaderFactoryService recordReaderFactoryService;
    @Autowired
    private FeatureBucketAggregatorMetricsContainer featureBucketAggregatorMetricsContainer;

    @Bean
    public InMemoryFeatureBucketAggregator getInMemoryFeatureBucketAggregator() {
        return new InMemoryFeatureBucketAggregator(bucketConfigurationService, recordReaderFactoryService, featureBucketAggregatorMetricsContainer);
    }
}
