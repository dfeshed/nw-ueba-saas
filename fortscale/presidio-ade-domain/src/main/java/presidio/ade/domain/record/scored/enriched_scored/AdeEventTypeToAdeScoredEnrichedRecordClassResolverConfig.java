package presidio.ade.domain.record.scored.enriched_scored;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by YaronDL on 6/15/2017.
 */
@Configuration
public class AdeEventTypeToAdeScoredEnrichedRecordClassResolverConfig {
    @Bean
    public AdeEventTypeToAdeScoredEnrichedRecordClassResolver adeEventTypeToAdeScoredEnrichedRecordClassResolver(){
        return new AdeEventTypeToAdeScoredEnrichedRecordClassResolver(this.getClass().getPackage().getName());
    }
}
