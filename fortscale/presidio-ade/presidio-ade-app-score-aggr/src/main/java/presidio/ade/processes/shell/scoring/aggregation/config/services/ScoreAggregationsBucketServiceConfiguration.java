package presidio.ade.processes.shell.scoring.aggregation.config.services;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
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
        ScoringAggregationsRecordReaderFactoryServiceConfig.class
})
public class ScoreAggregationsBucketServiceConfiguration {
    @Autowired
    @Qualifier("scoreAggregationBucketConfigService")
    private BucketConfigurationService bucketConfigurationService;
    @Autowired
    private RecordReaderFactoryService recordReaderFactoryService;

    @Bean
    public ScoreAggregationsBucketService getScoreAggregationsBucketService() {
        return new ScoreAggregationsBucketServiceImpl(bucketConfigurationService, recordReaderFactoryService);
    }
}
