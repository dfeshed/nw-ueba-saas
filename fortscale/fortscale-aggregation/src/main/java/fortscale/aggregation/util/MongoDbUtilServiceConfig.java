package fortscale.aggregation.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by barak_schuster on 10/9/16.
 */
@Configuration
public class MongoDbUtilServiceConfig {
    @Bean
    public MongoDbUtilService mongoDbUtilService()
    {
        return new MongoDbUtilService();
    }
}
