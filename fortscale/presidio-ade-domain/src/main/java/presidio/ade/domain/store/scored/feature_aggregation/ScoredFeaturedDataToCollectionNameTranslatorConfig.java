package presidio.ade.domain.store.scored.feature_aggregation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ScoredFeaturedDataToCollectionNameTranslatorConfig {

    @Bean
    public ScoredFeaturedDataToCollectionNameTranslator scoredFeaturedDataToCollectionNameTranslator() {
        return new ScoredFeaturedDataToCollectionNameTranslator();
    }
}

