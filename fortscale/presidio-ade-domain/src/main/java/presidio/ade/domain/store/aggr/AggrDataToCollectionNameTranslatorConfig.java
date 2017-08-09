package presidio.ade.domain.store.aggr;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by barak_schuster on 7/10/17.
 */
@Configuration
public class AggrDataToCollectionNameTranslatorConfig {
    @Bean
    public ScoreAggrDataToCollectionNameTranslator aggrDataToCollectionNameTranslator() {
        return new ScoreAggrDataToCollectionNameTranslator();
    }
}
