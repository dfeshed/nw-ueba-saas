package presidio.output.processor.spring;

import fortscale.accumulator.aggregation.AccumulationsCacheConfig;
import fortscale.aggregation.feature.functions.AggrFeatureFuncServiceConfig;
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
import presidio.output.processor.config.SupportingInformationConfig;
import presidio.output.processor.services.alert.supportinginformation.SupportingInformationForFeatureAggr;
import presidio.output.processor.services.alert.supportinginformation.SupportingInformationForScoreAggr;
import presidio.output.processor.services.alert.supportinginformation.SupportingInformationGeneratorFactory;
import presidio.output.processor.services.alert.supportinginformation.historicaldata.*;
import presidio.output.processor.services.alert.supportinginformation.historicaldata.fetchers.HistoricalDataFetcher;

@Configuration
@Import({
       // MongoConfig.class,
        EventPersistencyServiceConfig.class,
        AdeManagerSdkConfig.class,
        BucketConfigurationServiceConfig.class,
        EnrichedDataStoreConfig.class,
        RecordReaderFactoryServiceConfig.class,
        AggrFeatureFuncServiceConfig.class,
        AggregatedFeatureEventsConfServiceConfig.class,
        AccumulationsCacheConfig.class,
        HistoricalDataFetcherConfig.class

})
public class SupporingInformationServiceConfig extends ApplicationConfiguration{

    @Autowired
    EventPersistencyService eventPersistencyService;

    @Autowired
    AdeManagerSdk adeManagerSdk;

    @Autowired
    RecordReaderFactoryService recordReaderFactoryService;

    @Autowired
    EnrichedDataStore enrichedDataStore;

    @Autowired
    HistoricalDataFetcher historicalDataFetcher;

    @Bean
    public SupportingInformationConfig supportingInformationConfig() {
        return bindPropertiesToTarget(SupportingInformationConfig.class, null, "classpath:supporting_information_config.yml");
    }

    @Bean
    public HistoricalDataCountByTimePopulator historicalDataCountByTimePopulator() {
        return new HistoricalDataCountByTimePopulator(historicalDataFetcher);
    }

    @Bean
    public HistoricalDataCountByValuePopulator historicalDataCountByValuePopulator() {
        return new HistoricalDataCountByValuePopulator(historicalDataFetcher);
    }

    @Bean
    public HistoricalDataCountByWeekdayPopulator historicalDataCountByWeekdayPopulator() {
        return new HistoricalDataCountByWeekdayPopulator(historicalDataFetcher);
    }

    @Bean
    public HistoricalDataPopulatorFactory historicalDataPopulatorFactory()  {
        return  new HistoricalDataPopulatorFactory();
    }


    @Bean
    public SupportingInformationForScoreAggr supportingInformationForScoreAggr() {
        return new SupportingInformationForScoreAggr(supportingInformationConfig(),
                adeManagerSdk,
                eventPersistencyService,
                historicalDataPopulatorFactory());
    }

    @Bean
    public SupportingInformationForFeatureAggr supportingInformationForFeatureAggr() {
        return new SupportingInformationForFeatureAggr(supportingInformationConfig(),
                                                       eventPersistencyService,
                                                        historicalDataPopulatorFactory());
    }

    @Bean
    public SupportingInformationGeneratorFactory supportingInformationGeneratorFactory(){
        return new SupportingInformationGeneratorFactory();
    }
}
