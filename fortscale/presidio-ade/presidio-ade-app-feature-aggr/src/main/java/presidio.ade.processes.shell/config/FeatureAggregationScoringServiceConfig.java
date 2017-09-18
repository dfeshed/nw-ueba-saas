package presidio.ade.processes.shell.config;



import fortscale.ml.scorer.ScoringService;
import fortscale.ml.scorer.feature_aggregation_events.FeatureAggregationScoringService;
import fortscale.ml.scorer.feature_aggregation_events.FeatureAggregationScoringServiceImpl;
import fortscale.ml.scorer.feature_aggregation_events.ScoredFeatureAggregatedRecordBuilder;
import presidio.ade.domain.record.RecordReaderFactoryServiceConfig;
import fortscale.utils.recordreader.RecordReaderFactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        AggregatedFeatureEventsConfServiceConfig.class,
        RecordReaderFactoryServiceConfig.class,
        ScoringServiceConfig.class,
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
