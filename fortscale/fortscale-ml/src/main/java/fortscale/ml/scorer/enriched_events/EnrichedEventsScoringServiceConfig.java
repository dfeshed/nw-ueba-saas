package fortscale.ml.scorer.enriched_events;

import fortscale.ml.scorer.ScorersService;
import fortscale.ml.scorer.spring.config.ScorerSpringConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.domain.store.scored.ScoredDataStore;
import presidio.ade.domain.store.scored.ScoredDataStoreMongoConfig;

/**
 * Created by YaronDL on 6/14/2017.
 */

@Configuration
@Import({ScorerSpringConfiguration.class, ScoredDataStoreMongoConfig.class})
public class EnrichedEventsScoringServiceConfig {
    @Autowired
    private ScorersService scorersService;
    @Autowired
    private ScoredDataStore scoredDataStore;

    @Bean
    public EnrichedEventsScoringService enrichedEventsScoringService(){
        return new EnrichedEventsScoringServiceImpl(scorersService, scoredDataStore);
    }
}
