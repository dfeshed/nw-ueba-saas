package presidio.ade.domain.store.scored.feature_aggregation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.ade.domain.store.aggr.AggrDataToCollectionNameTranslator;


@Configuration
public class ScoredFeaturedDataToCollectionNameTranslatorConfig extends AggrDataToCollectionNameTranslator {

    @Bean
    public ScoredFeaturedDataToCollectionNameTranslator scoredFeaturedDataToCollectionNameTranslator() {
        return new ScoredFeaturedDataToCollectionNameTranslator();
    }
}

