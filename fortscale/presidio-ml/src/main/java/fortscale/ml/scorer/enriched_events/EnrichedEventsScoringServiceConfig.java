package fortscale.ml.scorer.enriched_events;

import fortscale.ml.model.config.ModelingEngineConfiguration;
import fortscale.ml.scorer.ScoringService;
import fortscale.ml.scorer.records.AdeRecordReaderFactoriesConfig;
import fortscale.ml.scorer.records.RecordReaderFactoryServiceConfig;
import fortscale.ml.scorer.records.TransformationConfig;
import fortscale.ml.scorer.spring.config.ScoringSpringConfiguration;
import fortscale.utils.recordreader.RecordReaderFactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.domain.store.scored.ScoredEnrichedDataStore;
import presidio.ade.domain.store.scored.ScoredEnrichedDataStoreMongoConfig;

@Configuration
@Import({
        ModelingEngineConfiguration.class,
        ScoringSpringConfiguration.class,
        ScoredEnrichedDataStoreMongoConfig.class,
        AdeEnrichedScoredRecordBuilderConfig.class,
        TransformationConfig.class,
        RecordReaderFactoryServiceConfig.class,
        AdeRecordReaderFactoriesConfig.class
})
public class EnrichedEventsScoringServiceConfig {
    @Autowired
    private RecordReaderFactoryService recordReaderFactoryService;
    @Autowired
    private ScoringService scoringService;
    @Autowired
    private ScoredEnrichedDataStore scoredEnrichedDataStore;
    @Autowired
    private AdeEnrichedScoredRecordBuilder adeEnrichedScoredRecordBuilder;

    @Bean
    public EnrichedEventsScoringService enrichedEventsScoringService() {
        return new EnrichedEventsScoringServiceImpl(
                recordReaderFactoryService,
                scoringService,
                scoredEnrichedDataStore,
                adeEnrichedScoredRecordBuilder);
    }
}
