package presidio.ade.domain.store.scored;

import fortscale.utils.mongodb.util.MongoDbBulkOpUtil;
import fortscale.utils.mongodb.util.MongoDbBulkOpUtilConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Created by YaronDL on 6/13/2017.
 */

@Configuration
@Import({
        AdeScoredEnrichedRecordToCollectionNameTranslatorConfig.class,
        MongoDbBulkOpUtilConfig.class
})
public class ScoredEnrichedDataStoreMongoConfig {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private AdeScoredEnrichedRecordToCollectionNameTranslator translator;
    @Autowired
    private MongoDbBulkOpUtil mongoDbBulkOpUtil;

    @Bean
    public ScoredEnrichedDataStore scoredDataStore() { return new ScoredEnrichedDataStoreMongoImpl(mongoTemplate, translator, mongoDbBulkOpUtil);}
}
