package presidio.ade.processes.shell.feature.aggregation.buckets.config;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.InMemoryFeatureBucketAggregator;
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
@Import({ModelAggregationBucketConfigurationServiceConfig.class,
        TransformationConfig.class,
        RecordReaderFactoryServiceConfig.class,
        AdeRecordReaderFactoriesConfig.class,
})
public class InMemoryFeatureBucketAggregatorConfig {

    @Autowired
    @Qualifier("modelBucketConfigService")
    private BucketConfigurationService bucketConfigurationService;
    @Autowired
    private RecordReaderFactoryService recordReaderFactoryService;

    @Bean
    public InMemoryFeatureBucketAggregator getInMemoryFeatureBucketAggregator(){
        return new InMemoryFeatureBucketAggregator(bucketConfigurationService, recordReaderFactoryService);
    }
}
