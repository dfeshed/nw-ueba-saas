package presidio.ade.processes.shell.config;

import fortscale.aggregation.creator.AggregationRecordsCreator;
import fortscale.aggregation.feature.bucket.BucketConfigurationService;
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
import presidio.ade.processes.shell.accumulate.AccumulateAggregationsBucketService;
import presidio.ade.processes.shell.accumulate.AccumulateAggregationsBucketServiceImpl;

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
        AdeRecordReaderFactoriesConfig.class,
        FeatureBucketAggregatorMetricsContainerConfig.class
})
public class AccumulateAggregationsBucketServiceConfig {

    @Autowired
    private AggregationRecordsCreator aggregationsCreator;

    @Autowired
    private RecordReaderFactoryService recordReaderFactoryService;

    @Autowired
    @Qualifier("bucketConfigurationService")
    private BucketConfigurationService bucketConfigurationService;

    @Autowired
    private FeatureBucketAggregatorMetricsContainer featureBucketAggregatorMetricsContainer;

    @Bean
    public AccumulateAggregationsBucketService accumulateAggregationsBucketService(){
        return new AccumulateAggregationsBucketServiceImpl(aggregationsCreator,bucketConfigurationService,recordReaderFactoryService, featureBucketAggregatorMetricsContainer);
    }
}
