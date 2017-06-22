package fortscale.ml.scorer.enriched_events;

import fortscale.ml.scorer.ScoringService;
import fortscale.ml.scorer.spring.config.ScoringSpringConfiguration;
import fortscale.utils.recordreader.RecordReaderFactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.domain.store.scored.ScoredEnrichedDataStore;
import presidio.ade.domain.store.scored.ScoredEnrichedDataStoreMongoConfig;

import java.util.Collections;

/**
 * Created by YaronDL on 6/14/2017.
 */
@Configuration
@Import({ScoringSpringConfiguration.class, ScoredEnrichedDataStoreMongoConfig.class, AdeEnrichedScoredRecordBuilderConfig.class})
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
    public RecordReaderFactoryService recordReaderFactoryService() {
        // TODO: Pass a collection of record reader factories
        return new RecordReaderFactoryService(Collections.emptySet());
    }

    @Bean
    public EnrichedEventsScoringService enrichedEventsScoringService() {
        return new EnrichedEventsScoringServiceImpl(recordReaderFactoryService, scoringService, scoredEnrichedDataStore, adeEnrichedScoredRecordBuilder);
    }
}
