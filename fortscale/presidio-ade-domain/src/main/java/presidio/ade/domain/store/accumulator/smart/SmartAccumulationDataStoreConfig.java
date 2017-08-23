package presidio.ade.domain.store.accumulator.smart;

import fortscale.utils.mongodb.util.MongoDbBulkOpUtil;
import fortscale.utils.mongodb.util.MongoDbBulkOpUtilConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;


@Configuration
@Import({SmartAccumulatedDataToCollectionNameTranslatorConfig.class, MongoDbBulkOpUtilConfig.class})
public class SmartAccumulationDataStoreConfig {
    @Autowired
    public MongoTemplate mongoTemplate;
    @Autowired
    public SmartAccumulatedDataToCollectionNameTranslator translator;
    @Autowired
    public MongoDbBulkOpUtil mongoDbBulkOpUtil;

    @Bean
    public SmartAccumulationDataStore smartAccumulationDataStore() {
        return new SmartAccumulationDataStoreMongoImpl(mongoTemplate, translator, mongoDbBulkOpUtil);
    }
}
