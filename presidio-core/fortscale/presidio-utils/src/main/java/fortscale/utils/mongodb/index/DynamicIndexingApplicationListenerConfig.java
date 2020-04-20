package fortscale.utils.mongodb.index;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

@Configuration
public class DynamicIndexingApplicationListenerConfig {
    @Autowired
    private MongoMappingContext mappingContext;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Bean
    public DynamicIndexingApplicationListener dynamicIndexingApplicationListener() {
        return new DynamicIndexingApplicationListener(mappingContext, mongoTemplate);
    }
}
