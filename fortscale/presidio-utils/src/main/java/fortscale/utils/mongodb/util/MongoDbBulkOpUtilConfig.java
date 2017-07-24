package fortscale.utils.mongodb.util;

import fortscale.utils.mongodb.index.DynamicIndexApplicationListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Created by barak_schuster on 7/19/17.
 */
@Configuration
public class MongoDbBulkOpUtilConfig {
    @Autowired
    public DynamicIndexApplicationListener dynamicIndexApplicationListener;
    @Autowired
    public MongoTemplate mongoTemplate;

    @Bean
    public MongoDbBulkOpUtil mongoDbBulkOpUtil() {
        return new MongoDbBulkOpUtil(dynamicIndexApplicationListener,mongoTemplate);
    }
}
