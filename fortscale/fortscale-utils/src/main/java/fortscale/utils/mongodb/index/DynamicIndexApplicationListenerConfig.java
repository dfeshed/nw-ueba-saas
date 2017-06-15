package fortscale.utils.mongodb.index;

import fortscale.utils.mongodb.util.MongoDbUtilService;
import fortscale.utils.mongodb.util.MongoDbUtilServiceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

/**
 * Created by barak_schuster on 6/1/17.
 */
@Configuration
@Import(MongoDbUtilServiceConfig.class)
public class DynamicIndexApplicationListenerConfig {
    @Autowired
    private MongoMappingContext mappingContext;
    @Autowired
    private MongoDbFactory mongoDbFactory;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private MongoDbUtilService mongoDbUtilService;

    @Bean
    public DynamicIndexApplicationListener dynamicIndexApplicationListener() {
        return new DynamicIndexApplicationListener(mongoTemplate, mongoDbUtilService, mappingContext, mongoDbFactory);
    }
}
