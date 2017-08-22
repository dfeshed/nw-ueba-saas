package presidio.ade.smart.config;

import fortscale.utils.mongodb.util.MongoDbBulkOpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import presidio.ade.domain.store.smart.SmartDataStore;
import presidio.ade.domain.store.smart.SmartDataStoreMongoImpl;

/**
 * @author Lior Govrin
 */
@Configuration
public class SmartApplicationSmartDataStoreConfig {
	@Autowired
	private MongoDbBulkOpUtil mongoDbBulkOpUtil;
	@Autowired
	private MongoTemplate mongoTemplate;

	@Bean
	public SmartDataStore smartDataStore() {
		return new SmartDataStoreMongoImpl(mongoDbBulkOpUtil, mongoTemplate);
	}
}
