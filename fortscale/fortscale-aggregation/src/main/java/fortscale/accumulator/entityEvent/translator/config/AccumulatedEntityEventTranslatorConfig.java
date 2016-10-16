package fortscale.accumulator.entityEvent.translator.config;

import fortscale.accumulator.entityEvent.translator.AccumulatedEntityEventTranslator;
import fortscale.entity.event.translator.EntityEventTranslationService;
import fortscale.entity.event.translator.EntityEventTranslationServiceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by barak_schuster on 10/16/16.
 */
@Configuration
@Import({EntityEventTranslationServiceConfig.class})
public class AccumulatedEntityEventTranslatorConfig {

    @Autowired
    private EntityEventTranslationService aggregatedFeatureNameTranslationService;

    @Bean
    public AccumulatedEntityEventTranslator accumulatedFeatureTranslator()
    {
        return new AccumulatedEntityEventTranslator (aggregatedFeatureNameTranslationService);
    }
}

