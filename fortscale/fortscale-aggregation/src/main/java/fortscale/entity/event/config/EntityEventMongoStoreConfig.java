package fortscale.entity.event.config;

import fortscale.entity.event.EntityEventMongoStore;
import fortscale.utils.mongodb.util.MongoDbUtilServiceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by barak_schuster on 10/9/16.
 */
@Configuration
@Import({MongoDbUtilServiceConfig.class,EntityEventConfServiceConfig.class})
public class EntityEventMongoStoreConfig {

    @Bean
    public EntityEventMongoStore entityEventMongoStore()
    {
        return new EntityEventMongoStore();
    }
}
