package fortscale.ml.processes.shell;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.config.BucketConfigurationServiceConfig;
import fortscale.ml.scorer.records.AdeRecordReaderFactoriesConfig;
import fortscale.ml.scorer.records.RecordReaderFactoryServiceConfig;
import fortscale.ml.scorer.records.TransformationConfig;
import fortscale.utils.recordreader.RecordReaderFactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by YaronDL on 7/2/2017.
 */
@Configuration
@Import({BucketConfigurationServiceConfig.class,
        TransformationConfig.class,
        RecordReaderFactoryServiceConfig.class,
        AdeRecordReaderFactoriesConfig.class,
})
public class ScoreAggregationsBucketServiceConfiguration {

    @Autowired
    private BucketConfigurationService bucketConfigurationService;
    @Autowired
    private RecordReaderFactoryService recordReaderFactoryService;


    @Bean
    public ScoreAggregationsBucketService getScoreAggregationsBucketService(){
        return new ScoreAggregationsBucketServiceImpl(bucketConfigurationService,recordReaderFactoryService);
    }
}
