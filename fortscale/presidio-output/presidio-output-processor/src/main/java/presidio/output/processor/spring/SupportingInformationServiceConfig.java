package presidio.output.processor.spring;

import fortscale.utils.spring.ApplicationConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.sdk.common.AdeManagerSdk;
import presidio.ade.sdk.common.AdeManagerSdkConfig;
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.domain.services.event.ScoredEventService;
import presidio.output.domain.spring.EventPersistencyServiceConfig;
import presidio.output.processor.config.SupportingInformationConfig;
import presidio.output.processor.services.alert.supportinginformation.SupportingInformationForFeatureAggr;
import presidio.output.processor.services.alert.supportinginformation.SupportingInformationForScoreAggr;
import presidio.output.processor.services.alert.supportinginformation.SupportingInformationGeneratorFactory;
import presidio.output.processor.services.alert.supportinginformation.historicaldata.HistoricalDataCountByTimePopulator;
import presidio.output.processor.services.alert.supportinginformation.historicaldata.HistoricalDataCountByValuePopulator;
import presidio.output.processor.services.alert.supportinginformation.historicaldata.HistoricalDataCountByWeekdayPopulator;
import presidio.output.processor.services.alert.supportinginformation.historicaldata.HistoricalDataPopulatorFactory;
import presidio.output.processor.services.alert.supportinginformation.historicaldata.fetchers.HistoricalDataFetcher;

@Configuration
@Import({
        EventPersistencyServiceConfig.class,
        AdeManagerSdkConfig.class,
        HistoricalDataFetcherConfig.class
})
public class SupportingInformationServiceConfig extends ApplicationConfiguration {
    @Autowired
    private EventPersistencyService eventPersistencyService;

    @Autowired
    private AdeManagerSdk adeManagerSdk;

    @Autowired
    private HistoricalDataFetcher historicalDataFetcher;

    @Autowired
    private SupportingInformationConfig supportingInformationConfig;

    @Autowired
    private ScoredEventService scoredEventService;

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
    public HistoricalDataPopulatorFactory historicalDataPopulatorFactory() {
        return new HistoricalDataPopulatorFactory();
    }

    @Bean
    public SupportingInformationForScoreAggr supportingInformationForScoreAggr() {
        return new SupportingInformationForScoreAggr(
                supportingInformationConfig,
                adeManagerSdk,
                eventPersistencyService,
                historicalDataPopulatorFactory(),
                scoredEventService);
    }

    @Bean
    public SupportingInformationForFeatureAggr supportingInformationForFeatureAggr() {
        return new SupportingInformationForFeatureAggr(
                supportingInformationConfig,
                eventPersistencyService,
                historicalDataPopulatorFactory());
    }

    @Bean
    public SupportingInformationGeneratorFactory supportingInformationGeneratorFactory() {
        return new SupportingInformationGeneratorFactory();
    }
}
