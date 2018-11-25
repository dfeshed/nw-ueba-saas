package presidio.output.processor.spring;

import fortscale.accumulator.aggregation.AccumulationsCache;
import fortscale.accumulator.aggregation.AccumulationsCacheConfig;
import fortscale.accumulator.aggregation.AccumulatorService;
import fortscale.aggregation.creator.AggregationRecordsCreator;
import fortscale.aggregation.creator.AggregationRecordsCreatorImpl;
import fortscale.aggregation.creator.metrics.AggregationRecordsCreatorMetricsContainer;
import fortscale.aggregation.creator.metrics.AggregationRecordsCreatorMetricsContainerConfig;
import fortscale.aggregation.feature.bucket.InMemoryFeatureBucketAggregator;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.aggregation.feature.functions.AggrFeatureFuncServiceConfig;
import fortscale.aggregation.feature.functions.IAggrFeatureEventFunctionsService;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.domain.store.enriched.EnrichedDataStoreConfig;
import presidio.ade.sdk.aggregation_records.AggregatedFeatureEventsConfServiceConfig;
import presidio.ade.sdk.common.AdeManagerSdk;
import presidio.ade.sdk.common.AdeManagerSdkConfig;
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.domain.spring.EventPersistencyServiceConfig;
import presidio.output.processor.services.alert.supportinginformation.historicaldata.fetchers.HistoricalDataFetcher;
import presidio.output.processor.services.alert.supportinginformation.historicaldata.fetchers.HistoricalDataFetcherADEModelsBased;

@Configuration
@Import({
        EventPersistencyServiceConfig.class,
        AdeManagerSdkConfig.class,
        EnrichedDataStoreConfig.class,
        AggrFeatureFuncServiceConfig.class,
        AggregatedFeatureEventsConfServiceConfig.class,
        AccumulationsCacheConfig.class,
        AggregationRecordsCreatorMetricsContainerConfig.class
})
public class HistoricalDataFetcherConfig {
    @Autowired
    private EventPersistencyService eventPersistencyService;

    @Autowired
    private AdeManagerSdk adeManagerSdk;

    @Autowired
    private EnrichedDataStore enrichedDataStore;

    @Autowired
    private IAggrFeatureEventFunctionsService aggrFeatureEventFunctionsService;

    @Autowired
    private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;

    @Autowired
    private AccumulationsCache accumulationsCache;

    @Autowired
    private InMemoryFeatureBucketAggregator inMemoryFeatureBucketAggregator;

    @Autowired
    private AggregationRecordsCreatorMetricsContainer aggregationRecordsCreatorMetricsContainer;

    @Bean
    public AggregationRecordsCreator aggregationRecordsCreator() {
        return new AggregationRecordsCreatorImpl(aggrFeatureEventFunctionsService, aggregatedFeatureEventsConfService, aggregationRecordsCreatorMetricsContainer);
    }

    @Bean
    public AccumulatorService accumulatorService() {
        return new AccumulatorService(accumulationsCache, FixedDurationStrategy.DAILY, FixedDurationStrategy.HOURLY);
    }

    @Bean
    public HistoricalDataFetcher historicalDataFetcher() {
        return new HistoricalDataFetcherADEModelsBased(adeManagerSdk, enrichedDataStore, inMemoryFeatureBucketAggregator, aggregationRecordsCreator(), accumulatorService(), accumulationsCache);
    }
}
