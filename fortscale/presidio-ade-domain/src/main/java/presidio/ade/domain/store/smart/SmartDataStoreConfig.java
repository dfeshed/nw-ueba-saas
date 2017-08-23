package presidio.ade.domain.store.smart;

import fortscale.utils.mongodb.util.MongoDbBulkOpUtil;
import fortscale.utils.mongodb.util.MongoDbBulkOpUtilConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * @author Lior Govrin
 */
@Configuration
@Import({SmartDataToCollectionNameTranslatorConfig.class, MongoDbBulkOpUtilConfig.class})
public class SmartDataStoreConfig {
	@Autowired
	private MongoDbBulkOpUtil mongoDbBulkOpUtil;
	@Autowired
	public SmartDataToCollectionNameTranslator translator;
	@Autowired
	private MongoTemplate mongoTemplate;

	@Bean
	public SmartDataStore smartDataStore() {
		return new SmartDataStoreMongoImpl(mongoDbBulkOpUtil, translator, mongoTemplate);
	}
}
