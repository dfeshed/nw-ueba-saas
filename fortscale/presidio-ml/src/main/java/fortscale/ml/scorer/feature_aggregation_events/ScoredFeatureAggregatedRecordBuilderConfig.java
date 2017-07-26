package fortscale.ml.scorer.feature_aggregation_events;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ScoredFeatureAggregatedRecordBuilderConfig {

    @Bean
    public ScoredFeatureAggregatedRecordBuilder scoredFeatureAggregatedRecordBuilder() {
        return new ScoredFeatureAggregatedRecordBuilder();
    }

}

