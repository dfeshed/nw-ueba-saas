package presidio.ade.domain.store.smart;

import fortscale.utils.mongodb.util.MongoDbBulkOpUtil;
import fortscale.utils.mongodb.util.MongoDbBulkOpUtilConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;


@Configuration
@Import({SmartDataToCollectionNameTranslatorConfig.class, MongoDbBulkOpUtilConfig.class})
public class SmartDataReaderConfig {
    @Autowired
    public MongoTemplate mongoTemplate;
    @Autowired
    public SmartDataToCollectionNameTranslator translator;
    @Autowired
    public MongoDbBulkOpUtil mongoDbBulkOpUtil;

    @Bean
    public SmartDataReader smartRecordDataReader() {
        return new SmartDataStoreMongoImpl(mongoDbBulkOpUtil, translator, mongoTemplate);
    }

}
