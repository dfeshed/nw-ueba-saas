package presidio.output.processor.spring;

import fortscale.utils.spring.ApplicationConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.sdk.common.AdeManagerSdk;
import presidio.ade.sdk.common.AdeManagerSdkConfig;
import presidio.output.domain.records.EnrichedEventRecordReaderFactory;
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.domain.services.event.ScoredEventService;
import presidio.output.domain.spring.EventPersistencyServiceConfig;
import presidio.output.processor.config.SupportingInformationConfig;
import presidio.output.processor.services.alert.supportinginformation.SupportingInformationForFeatureAggr;
import presidio.output.processor.services.alert.supportinginformation.SupportingInformationForScoreAggr;
import presidio.output.processor.services.alert.supportinginformation.SupportingInformationGeneratorFactory;
import presidio.output.processor.services.alert.supportinginformation.SupportingInformationUtils;
import presidio.output.processor.services.alert.supportinginformation.historicaldata.*;
import presidio.output.processor.services.alert.supportinginformation.historicaldata.fetchers.HistoricalDataFetcher;
import presidio.output.processor.services.alert.supportinginformation.transformer.AbnormalSourceMachineTransformer;
import presidio.output.processor.services.alert.supportinginformation.transformer.SupportingInformationTransformerFactory;

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

    @Autowired
    private EnrichedEventRecordReaderFactory enrichedEventRecordReaderFactory;

    @Bean
    public HistoricalDataCountByTimeForScoreFeaturePopulator historicalDataCountByTimeForScoreFeaturePopulator() {
        return new HistoricalDataCountByTimeForScoreFeaturePopulator(historicalDataFetcher);
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
    public HistoricalDataPopulatorFactory historicalDataPopulatorFactory() {
        return new HistoricalDataPopulatorFactory();
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

    @Bean
    public SupportingInformationUtils supportingInformationUtils() {
        return new SupportingInformationUtils(eventPersistencyService);
    }

    @Bean
    public SupportingInformationForScoreAggr supportingInformationForScoreAggr() {
        return new SupportingInformationForScoreAggr(
                supportingInformationConfig,
                historicalDataPopulatorFactory(),
                scoredEventService,
                supportingInformationUtils(),
                enrichedEventRecordReaderFactory);
    }

    @Bean
    public SupportingInformationForFeatureAggr supportingInformationForFeatureAggr() {
        return new SupportingInformationForFeatureAggr(
                supportingInformationConfig,
                eventPersistencyService,
                historicalDataPopulatorFactory(),
                supportingInformationUtils());
    }


    @Bean
    public SupportingInformationGeneratorFactory supportingInformationGeneratorFactory() {
        return new SupportingInformationGeneratorFactory();
    }
}
