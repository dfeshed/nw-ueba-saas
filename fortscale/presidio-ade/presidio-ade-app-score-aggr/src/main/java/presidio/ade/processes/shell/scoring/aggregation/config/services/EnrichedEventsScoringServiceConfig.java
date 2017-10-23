package presidio.ade.processes.shell.scoring.aggregation.config.services;

import fortscale.ml.scorer.ScoringService;
import fortscale.ml.scorer.algorithms.SmartWeightsScorerAlgorithmConfig;
import fortscale.ml.scorer.enriched_events.AdeEnrichedScoredRecordBuilder;
import fortscale.ml.scorer.enriched_events.AdeEnrichedScoredRecordBuilderConfig;
import fortscale.ml.scorer.enriched_events.EnrichedEventsScoringService;
import fortscale.ml.scorer.enriched_events.EnrichedEventsScoringServiceImpl;
import fortscale.utils.recordreader.RecordReaderFactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.domain.store.scored.ScoredEnrichedDataStore;
import presidio.ade.domain.store.scored.ScoredEnrichedDataStoreMongoConfig;

/**
 * Created by barak_schuster on 7/25/17.
 */
@Configuration
@Import({
//        application-specific confs
        ScoringServiceConfig.class,
        ScoringAggregationsRecordReaderFactoryServiceConfig.class,
//        common application confs
        ScoredEnrichedDataStoreMongoConfig.class,
        AdeEnrichedScoredRecordBuilderConfig.class
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
