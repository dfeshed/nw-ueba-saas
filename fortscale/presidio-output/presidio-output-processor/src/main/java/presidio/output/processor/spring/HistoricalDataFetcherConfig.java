package presidio.output.processor.spring;

import fortscale.accumulator.aggregation.AccumulationsCache;
import fortscale.accumulator.aggregation.AccumulationsCacheConfig;
import fortscale.accumulator.aggregation.AccumulatorService;
import fortscale.aggregation.creator.AggregationRecordsCreator;
import fortscale.aggregation.creator.AggregationRecordsCreatorImpl;
import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.InMemoryFeatureBucketAggregator;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.aggregation.feature.functions.AggrFeatureFuncServiceConfig;
import fortscale.aggregation.feature.functions.IAggrFeatureEventFunctionsService;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.recordreader.RecordReaderFactoryService;
import fortscale.utils.spring.ApplicationConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.domain.record.RecordReaderFactoryServiceConfig;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.domain.store.enriched.EnrichedDataStoreConfig;
import presidio.ade.sdk.aggregation_records.AggregatedFeatureEventsConfServiceConfig;
import presidio.ade.sdk.aggregation_records.BucketConfigurationServiceConfig;
import presidio.ade.sdk.common.AdeManagerSdk;
import presidio.ade.sdk.common.AdeManagerSdkConfig;
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.domain.spring.EventPersistencyServiceConfig;
import presidio.output.processor.services.alert.supportinginformation.historicaldata.fetchers.HistoricalDataFetcher;
import presidio.output.processor.services.alert.supportinginformation.historicaldata.fetchers.HistoricalDataFetcherADEModelsBaesd;

@Configuration
@Import({
        EventPersistencyServiceConfig.class,
        AdeManagerSdkConfig.class,
        BucketConfigurationServiceConfig.class,
        EnrichedDataStoreConfig.class,
        RecordReaderFactoryServiceConfig.class,
        AggrFeatureFuncServiceConfig.class,
        AggregatedFeatureEventsConfServiceConfig.class,
        AccumulationsCacheConfig.class

})
public class HistoricalDataFetcherConfig {

    @Autowired
    EventPersistencyService eventPersistencyService;

    @Autowired
    AdeManagerSdk adeManagerSdk;

    @Autowired
    BucketConfigurationService bucketConfigurationService;

    @Autowired
    RecordReaderFactoryService recordReaderFactoryService;

    @Autowired
    EnrichedDataStore enrichedDataStore;

    @Autowired
    private IAggrFeatureEventFunctionsService aggrFeatureEventFunctionsService;

    @Autowired
    private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;

    @Autowired
    private AccumulationsCache accumulationsCache;


    @Bean
    public InMemoryFeatureBucketAggregator inMemoryFeatureBucketAggregator() {
        return new InMemoryFeatureBucketAggregator(bucketConfigurationService, recordReaderFactoryService);
    }

    @Bean
    public AggregationRecordsCreator aggregationRecordsCreator() {
        return new AggregationRecordsCreatorImpl(aggrFeatureEventFunctionsService, aggregatedFeatureEventsConfService);
    }

    @Bean
    public AccumulatorService accumulatorService() {
        return new AccumulatorService(accumulationsCache, FixedDurationStrategy.DAILY, FixedDurationStrategy.HOURLY);
    }

    @Bean
    HistoricalDataFetcher historicalDataFetcher() {
        return new HistoricalDataFetcherADEModelsBaesd(adeManagerSdk,
                                                enrichedDataStore,
                                                inMemoryFeatureBucketAggregator(),
                                                aggregationRecordsCreator(),
                                                accumulatorService(),
                                                accumulationsCache
                                                );
    }





}
