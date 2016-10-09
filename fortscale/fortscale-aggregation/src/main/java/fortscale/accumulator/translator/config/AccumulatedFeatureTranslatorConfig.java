package fortscale.accumulator.translator.config;

import fortscale.accumulator.translator.AccumulatedFeatureTranslator;
import fortscale.aggregation.feature.event.store.translator.AggregatedFeatureNameTranslationService;
import fortscale.aggregation.feature.event.store.translator.AggregatedFeatureNameTranslationServiceConfig;
import fortscale.entity.event.translator.EntityEventTranslationService;
import fortscale.entity.event.translator.EntityEventTranslationServiceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({AggregatedFeatureNameTranslationServiceConfig.class,EntityEventTranslationServiceConfig.class})
public class AccumulatedFeatureTranslatorConfig {

    @Autowired
    private AggregatedFeatureNameTranslationService aggregatedFeatureNameTranslationService;
    @Autowired
    private EntityEventTranslationService entityEventTranslationService;

    @Bean
    public AccumulatedFeatureTranslator accumulatedFeatureTranslator()
    {
        return new AccumulatedFeatureTranslator(aggregatedFeatureNameTranslationService, entityEventTranslationService);
    }
}
