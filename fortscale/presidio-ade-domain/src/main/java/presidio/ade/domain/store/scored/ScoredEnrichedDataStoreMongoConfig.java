package presidio.ade.domain.store.scored;

import fortscale.utils.mongodb.util.MongoDbBulkOpUtil;
import fortscale.utils.mongodb.util.MongoDbBulkOpUtilConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * @author Yaron DL
 * @author Lior Govrin
 */
@Configuration
@Import({
        AdeScoredEnrichedRecordToCollectionNameTranslatorConfig.class,
        MongoDbBulkOpUtilConfig.class
})
public class ScoredEnrichedDataStoreMongoConfig {
    private final ScoredEnrichedDataStoreMongoImpl scoredEnrichedDataStoreMongoImpl;

    @Autowired
    public ScoredEnrichedDataStoreMongoConfig(
            MongoTemplate mongoTemplate,
            AdeScoredEnrichedRecordToCollectionNameTranslator adeScoredEnrichedRecordToCollectionNameTranslator,
            MongoDbBulkOpUtil mongoDbBulkOpUtil) {

        scoredEnrichedDataStoreMongoImpl = new ScoredEnrichedDataStoreMongoImpl(
                mongoTemplate,
                adeScoredEnrichedRecordToCollectionNameTranslator,
                mongoDbBulkOpUtil);
    }

    @Bean
    public ScoredEnrichedDataStore scoredEnrichedDataStore() {
        return scoredEnrichedDataStoreMongoImpl;
    }
}
