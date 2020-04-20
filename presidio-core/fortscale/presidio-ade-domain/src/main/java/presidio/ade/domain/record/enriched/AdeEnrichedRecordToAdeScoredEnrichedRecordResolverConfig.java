package presidio.ade.domain.record.enriched;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.ade.domain.record.util.AdeEnrichedRecordToAdeScoredEnrichedRecordResolver;

/**
 * Created by YaronDL on 6/15/2017.
 */
@Configuration
public class AdeEnrichedRecordToAdeScoredEnrichedRecordResolverConfig {
    @Bean
    public AdeEnrichedRecordToAdeScoredEnrichedRecordResolver adeEventTypeToAdeScoredEnrichedRecordClassResolver(){
        return new AdeEnrichedRecordToAdeScoredEnrichedRecordResolver(this.getClass().getPackage().getName());
    }
}
