package presidio.ade.domain.store.scored;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by YaronDL on 6/13/2017.
 */
@Configuration
public class ScoredDataToCollectionNameTranslatorConfig {

    @Bean
    public ScoredDataAdeToCollectionNameTranslator scoredDataToCollectionNameTranslator() {
        return new ScoredDataAdeToCollectionNameTranslator();
    }
}
