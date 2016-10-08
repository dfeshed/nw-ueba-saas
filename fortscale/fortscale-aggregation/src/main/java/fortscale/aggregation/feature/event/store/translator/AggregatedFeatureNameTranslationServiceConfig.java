package fortscale.aggregation.feature.event.store.translator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AggregatedFeatureNameTranslationServiceConfig {

    @Value("${streaming.event.field.type.aggr_event}")
    private String eventType;

    @Bean
    public AggregatedFeatureNameTranslationService aggregatedFeatureNameTranslationService() {
        return new AggregatedFeatureNameTranslationService(eventType);
    }
}
