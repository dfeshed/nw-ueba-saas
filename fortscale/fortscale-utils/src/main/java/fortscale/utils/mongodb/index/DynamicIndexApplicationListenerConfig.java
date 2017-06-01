package fortscale.utils.mongodb.index;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Created by barak_schuster on 6/1/17.
 */
@Configuration
public class DynamicIndexApplicationListenerConfig {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Bean
    public DynamicIndexApplicationListener dynamicIndexApplicationListener() {
        return new DynamicIndexApplicationListener(mongoTemplate);
    }
}
