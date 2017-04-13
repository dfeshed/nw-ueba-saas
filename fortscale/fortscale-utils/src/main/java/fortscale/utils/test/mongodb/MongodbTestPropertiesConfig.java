package fortscale.utils.test.mongodb;

import fortscale.utils.spring.PropertySourceConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by presidio on 4/13/17.
 */
@Configuration
public class MongodbTestPropertiesConfig {
    @Bean
    private static PropertySourceConfigurer mongoDbTestPropertiesConfigurer()
    {
        return new PropertySourceConfigurer(MongodbTestConfig.class, MongoDbTestProperties.getProperties());
    }
}
