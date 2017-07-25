package presidio.ade.processes.shell.feature.aggregation;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.InMemoryFeatureBucketAggregator;
import fortscale.aggregation.feature.bucket.config.BucketConfigurationServiceConfig;
import fortscale.ml.processes.shell.model.aggregation.ModelAggregationBucketConfigurationServiceConfig;
import fortscale.ml.scorer.records.AdeRecordReaderFactoriesConfig;
import fortscale.ml.scorer.records.RecordReaderFactoryServiceConfig;
import fortscale.ml.scorer.records.TransformationConfig;
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
})
public class InMemoryFeatureAggregatorConfig {

    @Autowired
    @Qualifier("featureAggregationBucketConfigService")
    private BucketConfigurationService bucketConfigurationService;
    @Autowired
    private RecordReaderFactoryService recordReaderFactoryService;

    @Bean
    public InMemoryFeatureBucketAggregator getInMemoryFeatureBucketAggregator() {
        return new InMemoryFeatureBucketAggregator(bucketConfigurationService, recordReaderFactoryService);
    }
}
