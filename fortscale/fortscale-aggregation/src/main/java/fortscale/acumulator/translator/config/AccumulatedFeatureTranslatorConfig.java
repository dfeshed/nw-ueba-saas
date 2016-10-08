package fortscale.acumulator.translator.config;

import fortscale.acumulator.translator.AccumulatedFeatureTranslator;
import fortscale.aggregation.feature.event.store.translator.AggregatedFeatureNameTranslationService;
import fortscale.aggregation.feature.event.store.translator.AggregatedFeatureNameTranslationServiceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(AggregatedFeatureNameTranslationServiceConfig.class)
public class AccumulatedFeatureTranslatorConfig {

    @Autowired
    AggregatedFeatureNameTranslationService aggregatedFeatureNameTranslationService;

    @Bean
    public AccumulatedFeatureTranslator accumulatedFeatureTranslator()
    {
        return new AccumulatedFeatureTranslator(aggregatedFeatureNameTranslationService);
    }
}
