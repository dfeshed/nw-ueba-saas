package fortscale.utils.mongodb.util;

import com.mongodb.async.client.MongoDatabase;
import fortscale.utils.mongodb.index.DynamicIndexingApplicationListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.support.IsNewStrategyFactory;

@Configuration
public class MongoDbBulkOpUtilConfig {
    @Autowired
    private DynamicIndexingApplicationListener dynamicIndexingApplicationListener;
    @Autowired
    private MappingContext mappingContext;
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

    public CachedIsNewAwareAuditingHandler cachedIsNewAwareAuditingHandler()
    {
        return new CachedIsNewAwareAuditingHandler(mappingContext,isNewStrategyFactory);
    }
}
