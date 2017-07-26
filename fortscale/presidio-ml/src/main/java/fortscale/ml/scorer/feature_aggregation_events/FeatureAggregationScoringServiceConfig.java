package fortscale.ml.scorer.feature_aggregation_events;

import fortscale.accumulator.aggregation.store.config.AccumulatedAggregatedFeatureEventStoreConfig;
import fortscale.aggregation.feature.event.config.AggregatedFeatureEventsConfServiceConfig;
import fortscale.ml.scorer.ScoringService;
import fortscale.ml.scorer.records.RecordReaderFactoryServiceConfig;
import fortscale.ml.scorer.spring.config.ScoringSpringConfiguration;
import fortscale.utils.recordreader.RecordReaderFactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        AggregatedFeatureEventsConfServiceConfig.class,
        RecordReaderFactoryServiceConfig.class,
        ScoringSpringConfiguration.class,
        ScoredFeatureAggregatedRecordBuilderConfig.class,
})
public class FeatureAggregationScoringServiceConfig {

    @Autowired
    private RecordReaderFactoryService recordReaderFactoryService;
    @Autowired
    private ScoringService scoringService;

    @Autowired
    private ScoredFeatureAggregatedRecordBuilder scoredFeatureAggregatedRecordBuilder;

    @Bean
    public FeatureAggregationScoringService enrichedEventsScoringService() {
        return new FeatureAggregationScoringServiceImpl(
                recordReaderFactoryService,
                scoringService,
                scoredFeatureAggregatedRecordBuilder);
    }
}
