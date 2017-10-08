package fortscale.utils.mongodb.util;

import fortscale.utils.mongodb.index.DynamicIndexingApplicationListener;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.IsNewAwareAuditingHandler;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoDbBulkOpUtilConfig {
    @Autowired
    private ObjectFactory<IsNewAwareAuditingHandler> auditingHandlerFactory;
    @Autowired
    private DynamicIndexingApplicationListener dynamicIndexingApplicationListener;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Bean
    public MongoDbBulkOpUtil mongoDbBulkOpUtil() {
        return new MongoDbBulkOpUtil(auditingHandlerFactory, dynamicIndexingApplicationListener, mongoTemplate);
    }
}
