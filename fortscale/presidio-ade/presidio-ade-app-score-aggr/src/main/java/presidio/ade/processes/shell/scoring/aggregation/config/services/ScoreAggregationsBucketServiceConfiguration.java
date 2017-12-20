package presidio.ade.processes.shell.scoring.aggregation.config.services;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.metrics.FeatureBucketAggregatorMetricsContainer;
import fortscale.utils.recordreader.RecordReaderFactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.processes.shell.scoring.aggregation.ScoreAggregationsBucketService;
import presidio.ade.processes.shell.scoring.aggregation.ScoreAggregationsBucketServiceImpl;

/**
 * Created by YaronDL on 7/2/2017.
 */
@Configuration
@Import({
        ScoreAggregationBucketConfigurationServiceConfig.class,
        ScoringAggregationsRecordReaderFactoryServiceConfig.class,
        FeatureBucketAggregatorMetricsContainer.class
})
public class ScoreAggregationsBucketServiceConfiguration {
    @Autowired
    @Qualifier("scoreAggregationBucketConfigService")
    private BucketConfigurationService bucketConfigurationService;
    @Autowired
    private RecordReaderFactoryService recordReaderFactoryService;
    @Autowired
    private FeatureBucketAggregatorMetricsContainer featureBucketAggregatorMetricsContainer;
    @Bean
    public ScoreAggregationsBucketService getScoreAggregationsBucketService() {
        return new ScoreAggregationsBucketServiceImpl(bucketConfigurationService, recordReaderFactoryService, featureBucketAggregatorMetricsContainer);
    }
}
