package fortscale.ml.model.store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class ModelStoreConfig {
	@Autowired
	private MongoTemplate mongoTemplate;

	@Bean
	public ModelStore modelStore() {
		return new ModelStore(mongoTemplate);
	}
}
