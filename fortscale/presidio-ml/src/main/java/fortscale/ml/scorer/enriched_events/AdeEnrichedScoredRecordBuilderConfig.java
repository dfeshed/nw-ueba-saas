package fortscale.ml.scorer.enriched_events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.domain.record.enriched.AdeEventTypeToAdeScoredEnrichedRecordClassResolver;
import presidio.ade.domain.record.enriched.AdeEventTypeToAdeScoredEnrichedRecordClassResolverConfig;

/**
 * Created by YaronDL on 6/18/2017.
 */
@Configuration
@Import({AdeEventTypeToAdeScoredEnrichedRecordClassResolverConfig.class})
public class AdeEnrichedScoredRecordBuilderConfig {

    @Autowired
    private AdeEventTypeToAdeScoredEnrichedRecordClassResolver dataSourceToAdeScoredEnrichedRecordClassResolver;

    @Bean
    public AdeEnrichedScoredRecordBuilder enrichedScoredRecordBuilder(){
        return new AdeEnrichedScoredRecordBuilder(dataSourceToAdeScoredEnrichedRecordClassResolver);
    }
}
