package presidio.ade.domain.record.enriched;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.ade.domain.record.enriched.AdeEventTypeToAdeScoredEnrichedRecordClassResolver;

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
