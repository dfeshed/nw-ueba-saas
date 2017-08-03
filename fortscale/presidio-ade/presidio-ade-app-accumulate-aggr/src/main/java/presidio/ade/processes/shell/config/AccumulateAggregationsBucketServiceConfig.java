package presidio.ade.processes.shell.config;

import fortscale.aggregation.creator.AggregationRecordsCreator;
import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.ml.scorer.records.AdeRecordReaderFactoriesConfig;
import fortscale.ml.scorer.records.RecordReaderFactoryServiceConfig;
import fortscale.ml.scorer.records.TransformationConfig;
import fortscale.utils.recordreader.RecordReaderFactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.processes.shell.accumulate.AccumulateAggregationsBucketService;
import presidio.ade.processes.shell.accumulate.AccumulateAggregationsBucketServiceImpl;
import presidio.ade.processes.shell.config.AggregationRecordsCreatorConfig;

/**
 * Created by maria_dorohin on 7/30/17.
 */
@Configuration
@Import({
        //        application-specific confs
        AggregationRecordsCreatorConfig.class,
        //        common application confs
        TransformationConfig.class,
        RecordReaderFactoryServiceConfig.class,
        AdeRecordReaderFactoriesConfig.class
})
public class AccumulateAggregationsBucketServiceConfig {

    @Autowired
    private AggregationRecordsCreator aggregationsCreator;

    @Autowired
    private RecordReaderFactoryService recordReaderFactoryService;

    @Autowired
    @Qualifier("bucketConfigurationService")
    private BucketConfigurationService bucketConfigurationService;

    @Bean
    public AccumulateAggregationsBucketService accumulateAggregationsBucketService(){
        return new AccumulateAggregationsBucketServiceImpl(aggregationsCreator,bucketConfigurationService,recordReaderFactoryService);
    }
}
