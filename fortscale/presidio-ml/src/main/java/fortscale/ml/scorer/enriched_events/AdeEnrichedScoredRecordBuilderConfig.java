package fortscale.ml.scorer.enriched_events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.domain.record.enriched.AdeEnrichedRecordToAdeScoredEnrichedRecordResolverConfig;
import presidio.ade.domain.record.util.AdeEnrichedRecordToAdeScoredEnrichedRecordResolver;

/**
 * Created by YaronDL on 6/18/2017.
 */
@Configuration
@Import({AdeEnrichedRecordToAdeScoredEnrichedRecordResolverConfig.class})
public class AdeEnrichedScoredRecordBuilderConfig {

    @Autowired
    private AdeEnrichedRecordToAdeScoredEnrichedRecordResolver adeEnrichedRecordToAdeScoredEnrichedRecordResolver;

    @Bean
    public AdeEnrichedScoredRecordBuilder enrichedScoredRecordBuilder(){
        return new AdeEnrichedScoredRecordBuilder(adeEnrichedRecordToAdeScoredEnrichedRecordResolver);
    }
}
