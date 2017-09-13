package fortscale.utils.mongodb.util;

import fortscale.utils.mongodb.index.DynamicIndexApplicationListener;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.IsNewAwareAuditingHandler;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Created by barak_schuster on 7/19/17.
 */
@Configuration
public class MongoDbBulkOpUtilConfig {
    @Autowired
    private DynamicIndexApplicationListener dynamicIndexApplicationListener;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ObjectFactory<IsNewAwareAuditingHandler> auditingHandlerFactory;
    @Bean
    public MongoDbBulkOpUtil mongoDbBulkOpUtil() {
        return new MongoDbBulkOpUtil(dynamicIndexApplicationListener,mongoTemplate,auditingHandlerFactory);
    }
}
