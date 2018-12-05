package presidio.ade.processes.shell.config;

import fortscale.aggregation.creator.AggregationRecordsCreator;
import fortscale.aggregation.creator.AggregationRecordsCreatorImpl;
import fortscale.aggregation.creator.metrics.AggregationRecordsCreatorMetricsContainer;
import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucketService;
import fortscale.aggregation.feature.bucket.FeatureBucketServiceImpl;
import fortscale.aggregation.feature.bucket.metrics.FeatureBucketAggregatorMetricsContainer;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.aggregation.feature.functions.IAggrFeatureEventFunctionsService;
import fortscale.utils.recordreader.RecordReaderFactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.ade.domain.record.aggregated.ScoredFeatureAggregationRecord;
import presidio.ade.domain.store.aggr.AggregatedDataStore;
import presidio.ade.processes.shell.aggregation.LevelThreeAggregationsService;

@Configuration
public class LevelThreeAggregationsConfig {
    private final String featureBucketConfJsonFilePath;
    private final String aggregatedFeatureEventConfJsonFilePath;
    private final RecordReaderFactoryService recordReaderFactoryService;
    private final FeatureBucketAggregatorMetricsContainer featureBucketAggregatorMetricsContainer;
    private final IAggrFeatureEventFunctionsService aggrFeatureEventFunctionsService;
    private final AggregationRecordsCreatorMetricsContainer aggregationRecordsCreatorMetricsContainer;
    private final AggregatedDataStore aggregatedDataStore;

    @Autowired
    public LevelThreeAggregationsConfig(
            @Value("${presidio.level.three.feature.bucket.conf.json.file.path}") String featureBucketConfJsonFilePath,
            @Value("${presidio.level.three.aggregated.feature.event.conf.json.file.path}") String aggregatedFeatureEventConfJsonFilePath,
            RecordReaderFactoryService recordReaderFactoryService,
            FeatureBucketAggregatorMetricsContainer featureBucketAggregatorMetricsContainer,
            IAggrFeatureEventFunctionsService aggrFeatureEventFunctionsService,
            AggregationRecordsCreatorMetricsContainer aggregationRecordsCreatorMetricsContainer,
            AggregatedDataStore aggregatedDataStore) {

        this.featureBucketConfJsonFilePath = featureBucketConfJsonFilePath;
        this.aggregatedFeatureEventConfJsonFilePath = aggregatedFeatureEventConfJsonFilePath;
        this.recordReaderFactoryService = recordReaderFactoryService;
        this.featureBucketAggregatorMetricsContainer = featureBucketAggregatorMetricsContainer;
        this.aggrFeatureEventFunctionsService = aggrFeatureEventFunctionsService;
        this.aggregationRecordsCreatorMetricsContainer = aggregationRecordsCreatorMetricsContainer;
        this.aggregatedDataStore = aggregatedDataStore;
    }

    @Bean("levelThreeBucketConfigurationService")
    public BucketConfigurationService levelThreeBucketConfigurationService() {
        return new BucketConfigurationService(featureBucketConfJsonFilePath, null, null);
    }

    @Bean("levelThreeAggregatedFeatureEventsConfService")
    public AggregatedFeatureEventsConfService levelThreeAggregatedFeatureEventsConfService() {
        return new AggregatedFeatureEventsConfService(aggregatedFeatureEventConfJsonFilePath, null, null, levelThreeBucketConfigurationService());
    }

    @Bean("levelThreeAggregationsService")
    public LevelThreeAggregationsService levelThreeAggregationsService() {
        FeatureBucketService<ScoredFeatureAggregationRecord> featureBucketService = new FeatureBucketServiceImpl<>(levelThreeBucketConfigurationService(), recordReaderFactoryService, featureBucketAggregatorMetricsContainer);
        AggregationRecordsCreator aggregationRecordsCreator = new AggregationRecordsCreatorImpl(aggrFeatureEventFunctionsService, levelThreeAggregatedFeatureEventsConfService(), aggregationRecordsCreatorMetricsContainer);
        return new LevelThreeAggregationsService(featureBucketService, aggregationRecordsCreator, aggregatedDataStore);
    }
}
