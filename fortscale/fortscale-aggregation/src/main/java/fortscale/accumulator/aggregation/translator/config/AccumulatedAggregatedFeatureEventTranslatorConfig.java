package fortscale.accumulator.aggregation.translator.config;

import fortscale.accumulator.aggregation.translator.AccumulatedAggregatedFeatureEventTranslator;
import fortscale.aggregation.feature.event.store.translator.AggregatedFeatureNameTranslationService;
import fortscale.aggregation.feature.event.store.translator.AggregatedFeatureNameTranslationServiceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({AggregatedFeatureNameTranslationServiceConfig.class})
public class AccumulatedAggregatedFeatureEventTranslatorConfig {

    @Autowired
    private AggregatedFeatureNameTranslationService aggregatedFeatureNameTranslationService;

    @Bean
    public AccumulatedAggregatedFeatureEventTranslator accumulatedFeatureTranslator()
    {
        return new AccumulatedAggregatedFeatureEventTranslator(aggregatedFeatureNameTranslationService);
    }
}
