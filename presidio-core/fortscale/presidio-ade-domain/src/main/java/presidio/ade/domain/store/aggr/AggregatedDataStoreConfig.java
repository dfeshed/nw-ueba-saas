package presidio.ade.domain.store.aggr;

import fortscale.utils.mongodb.util.MongoDbBulkOpUtil;
import fortscale.utils.mongodb.util.MongoDbBulkOpUtilConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * @author Barak Schuster
 * @author Lior Govrin
 */
@Configuration
@Import({
        AggrDataToCollectionNameTranslatorConfig.class,
        MongoDbBulkOpUtilConfig.class
})
public class AggregatedDataStoreConfig {
    private final AggregatedDataStoreMongoImpl aggregatedDataStoreMongoImpl;

    @Autowired
    public AggregatedDataStoreConfig(
            MongoTemplate mongoTemplate,
            AggrDataToCollectionNameTranslator aggrDataToCollectionNameTranslator,
            MongoDbBulkOpUtil mongoDbBulkOpUtil) {

        aggregatedDataStoreMongoImpl = new AggregatedDataStoreMongoImpl(
                mongoTemplate,
                aggrDataToCollectionNameTranslator,
                mongoDbBulkOpUtil);
    }

    @Bean
    public AggregatedDataStore aggregatedDataStore() {
        return aggregatedDataStoreMongoImpl;
    }
}
