package presidio.output.processor.spring;

import fortscale.accumulator.aggregation.AccumulationsCache;
import fortscale.accumulator.aggregation.AccumulationsCacheConfig;
import fortscale.accumulator.aggregation.AccumulatorService;
import fortscale.aggregation.creator.AggregationRecordsCreator;
import fortscale.aggregation.creator.AggregationRecordsCreatorImpl;
import fortscale.aggregation.creator.metrics.AggregationRecordsCreatorMetricsContainer;
import fortscale.aggregation.creator.metrics.AggregationRecordsCreatorMetricsContainerConfig;
import fortscale.aggregation.feature.bucket.InMemoryFeatureBucketAggregator;
import fortscale.aggregation.feature.bucket.InMemoryFeatureBucketAggregatorConfig;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.aggregation.feature.functions.AggrFeatureFuncServiceConfig;
import fortscale.aggregation.feature.functions.IAggrFeatureEventFunctionsService;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.recordreader.RecordReaderFactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.domain.store.enriched.EnrichedDataStoreConfig;
import presidio.ade.sdk.aggregation_records.AggregatedFeatureEventsConfServiceConfig;
import presidio.ade.sdk.common.AdeManagerSdk;
import presidio.ade.sdk.common.AdeManagerSdkConfig;
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.domain.services.event.ScoredEventService;
import presidio.output.domain.spring.EventPersistencyServiceConfig;
import presidio.output.processor.config.SupportingInformationConfig;
import presidio.output.processor.services.alert.indicator.IndicatorsGeneratorFactory;
import presidio.output.processor.services.alert.indicator.IndicatorsGeneratorForFeatureAggr;
import presidio.output.processor.services.alert.indicator.IndicatorsGeneratorForScoreAggr;
import presidio.output.processor.services.alert.supportinginformation.SupportingInformationForFeatureAggr;
import presidio.output.processor.services.alert.supportinginformation.SupportingInformationForScoreAggr;
import presidio.output.processor.services.alert.supportinginformation.SupportingInformationGeneratorFactory;
import presidio.output.processor.services.alert.supportinginformation.SupportingInformationUtils;
import presidio.output.processor.services.alert.supportinginformation.historicaldata.*;
import presidio.output.processor.services.alert.supportinginformation.historicaldata.fetchers.HistoricalDataFetcher;
import presidio.output.processor.services.alert.supportinginformation.historicaldata.fetchers.HistoricalDataFetcherADEModelsBased;
import presidio.output.processor.services.alert.supportinginformation.transformer.AbnormalSourceMachineTransformer;
import presidio.output.processor.services.alert.supportinginformation.transformer.SupportingInformationTransformerFactory;

@Configuration
@Import({
        AggrFeatureFuncServiceConfig.class,
        AggregatedFeatureEventsConfServiceConfig.class,
        AggregationRecordsCreatorMetricsContainerConfig.class,
        AccumulationsCacheConfig.class,
        AdeManagerSdkConfig.class,
        EnrichedDataStoreConfig.class,
        InMemoryFeatureBucketAggregatorConfig.class,
        EventPersistencyServiceConfig.class
})
public class SupportingInformationServiceConfig {
    private final AdeManagerSdk adeManagerSdk;
    private final HistoricalDataFetcher historicalDataFetcher;
    private final SupportingInformationConfig supportingInformationConfig;
    private final EventPersistencyService eventPersistencyService;
    private final SupportingInformationUtils supportingInformationUtils;
    private final ScoredEventService scoredEventService;

    @Autowired
    private RecordReaderFactoryService recordReaderFactoryService;

    @Autowired
    public SupportingInformationServiceConfig(
            IAggrFeatureEventFunctionsService aggrFeatureEventFunctionsService,
            AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService,
            AggregationRecordsCreatorMetricsContainer aggregationRecordsCreatorMetricsContainer,
            AccumulationsCache accumulationsCache,
            AdeManagerSdk adeManagerSdk,
            EnrichedDataStore enrichedDataStore,
            InMemoryFeatureBucketAggregator inMemoryFeatureBucketAggregator,
            SupportingInformationConfig supportingInformationConfig,
            EventPersistencyService eventPersistencyService,
            ScoredEventService scoredEventService,
            @Value("${daily.histograms.lru.map.max.size:1000}") int dailyHistogramsLruMapMaxSize) {

        AggregationRecordsCreator aggregationRecordsCreator = new AggregationRecordsCreatorImpl(
                aggrFeatureEventFunctionsService,
                aggregatedFeatureEventsConfService,
                aggregationRecordsCreatorMetricsContainer);

        AccumulatorService accumulatorService = new AccumulatorService(
                accumulationsCache,
                FixedDurationStrategy.DAILY,
                FixedDurationStrategy.HOURLY);

        this.historicalDataFetcher = new HistoricalDataFetcherADEModelsBased(
                adeManagerSdk,
                enrichedDataStore,
                inMemoryFeatureBucketAggregator,
                aggregationRecordsCreator,
                accumulatorService,
                accumulationsCache,
                dailyHistogramsLruMapMaxSize);

        this.adeManagerSdk = adeManagerSdk;
        this.supportingInformationConfig = supportingInformationConfig;
        this.eventPersistencyService = eventPersistencyService;
        this.supportingInformationUtils = new SupportingInformationUtils(eventPersistencyService);
        this.scoredEventService = scoredEventService;
    }

    @Bean
    public AggregationDataCountByTimeForScoreFeaturePopulator aggregationDataCountByTimeForScoreFeaturePopulator() {
        return new AggregationDataCountByTimeForScoreFeaturePopulator(historicalDataFetcher);
    }

    @Bean
    public AggregationDataCountByTimePopulator aggregationDataCountByTimePopulator() {
        return new AggregationDataCountByTimePopulator(historicalDataFetcher);
    }

    @Bean
    public AggregationDataCountByTimeForLastDayPopulator aggregationDataCountByTimeForNewOccurrencesPopulator() {
        return new AggregationDataCountByTimeForLastDayPopulator(historicalDataFetcher);
    }

    @Bean
    public AggregationDataCountByTimeGlobalPopulator aggregationDataCountByTimeGlobalPopulator() {
        return new AggregationDataCountByTimeGlobalPopulator(historicalDataFetcher);
    }

    @Bean
    public AggregationDataCountByValuePopulator aggregationDataCountByValuePopulator() {
        return new AggregationDataCountByValuePopulator(historicalDataFetcher);
    }

    @Bean
    public AggregationDataCountByWeekdayPopulator aggregationDataCountByWeekdayPopulator() {
        return new AggregationDataCountByWeekdayPopulator(historicalDataFetcher);
    }

    @Bean
    public AggregationDataPopulatorFactory aggregationDataPopulatorFactory() {
        return new AggregationDataPopulatorFactory();
    }

    @Bean
    public SupportingInformationForFeatureAggr supportingInformationForFeatureAggr() {
        return new SupportingInformationForFeatureAggr(supportingInformationConfig, eventPersistencyService, aggregationDataPopulatorFactory(), supportingInformationUtils);
    }

    @Bean
    public SupportingInformationForScoreAggr supportingInformationForScoreAggr() {
        return new SupportingInformationForScoreAggr(supportingInformationConfig, aggregationDataPopulatorFactory(), scoredEventService, supportingInformationUtils, adeManagerSdk, recordReaderFactoryService);
    }

    @Bean
    public SupportingInformationGeneratorFactory supportingInformationGeneratorFactory() {
        return new SupportingInformationGeneratorFactory();
    }

    @Bean
    public IndicatorsGeneratorForFeatureAggr indicatorsGeneratorForFeatureAggr() {
        return new IndicatorsGeneratorForFeatureAggr(supportingInformationConfig);
    }

    @Bean
    public IndicatorsGeneratorForScoreAggr indicatorsGeneratorForScoreAggr() {
        return new IndicatorsGeneratorForScoreAggr(supportingInformationConfig, adeManagerSdk, recordReaderFactoryService);
    }

    @Bean
    public IndicatorsGeneratorFactory indicatorsGeneratorGeneratorFactory() {
        return new IndicatorsGeneratorFactory();
    }

    @Bean(name = "abnormalSourceMachineTransformer")
    public AbnormalSourceMachineTransformer abnormalSourceMachineTransformer() {
        return new AbnormalSourceMachineTransformer();
    }

    @Bean
    public ServiceLocatorFactoryBean serviceLocatorFactoryBeanForSupportingInformationTransformerFactory() {
        ServiceLocatorFactoryBean factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(SupportingInformationTransformerFactory.class);
        return factoryBean;
    }
}
