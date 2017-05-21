package fortscale.utils.mongodb.index;

import fortscale.utils.mongodb.util.MongoDbUtilService;
import fortscale.utils.mongodb.util.MongoDbUtilServiceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Created by barak_schuster on 5/21/17.
 */
@Configuration
@Import(MongoDbUtilServiceConfig.class)
public class MongoIndexCreatorConfig {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private MongoDbUtilService mongoDbUtilService;

    @Bean
    public MongoIndexCreator mongoIndexCreator() {
        return new MongoIndexCreatorImpl(mongoTemplate,mongoDbUtilService);
    }
}
