package fortscale.utils.mongodb.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Created by barak_schuster on 10/9/16.
 */
@Configuration
public class MongoDbUtilServiceConfig {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Bean
    public MongoDbUtilService mongoDbUtilService()
    {
        return new MongoDbUtilService(mongoTemplate);
    }
}
