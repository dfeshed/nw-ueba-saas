package presidio.ade.domain.store.scored.feature_aggregation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import presidio.ade.domain.store.aggr.AggrDataStore;
import presidio.ade.domain.store.aggr.AggrDataStoreMongoImpl;

/**
 * Created by YaronDL on 6/13/2017.
 */

@Configuration
@Import({
        ScoredFeaturedDataToCollectionNameTranslatorConfig.class
})
public class ScoredFeatureAggregatedDataStoreConfig {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ScoredFeaturedDataToCollectionNameTranslator translator;

    @Bean
    public AggrDataStore aggrDataStore() {
        return new AggrDataStoreMongoImpl(mongoTemplate, translator);
    }
}
