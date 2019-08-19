package fortscale.utils.mongodb.util;

import com.mongodb.async.client.MongoDatabase;
import fortscale.utils.mongodb.index.DynamicIndexingApplicationListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.support.IsNewStrategyFactory;

@Configuration
public class MongoDbBulkOpUtilConfig {
    @Autowired
    private DynamicIndexingApplicationListener dynamicIndexingApplicationListener;
    @Autowired
    private MongoMappingContext mappingContext;
    @Autowired
    private IsNewStrategyFactory isNewStrategyFactory;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    MongoDatabase mongoAsyncDatabase;

    @Bean
    public MongoDbBulkOpUtil mongoDbBulkOpUtil() {
        return new MongoDbBulkOpUtil(cachedIsNewAwareAuditingHandler(), dynamicIndexingApplicationListener, mongoTemplate, mongoAsyncDatabase);
    }

    @Bean
    public MongoReflectionUtils mongoReflectionUtils() {
        return new MongoReflectionUtils();
    }


    public CachedIsNewAwareAuditingHandler cachedIsNewAwareAuditingHandler() {
        return new CachedIsNewAwareAuditingHandler(mappingContext, isNewStrategyFactory);
    }
}
