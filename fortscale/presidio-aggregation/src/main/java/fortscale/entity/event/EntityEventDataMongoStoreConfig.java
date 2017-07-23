package fortscale.entity.event;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by barak_schuster on 7/23/17.
 */
@Configuration
public class EntityEventDataMongoStoreConfig {
    @Bean
    public EntityEventDataMongoStore entityEventDataMongoStore()
    {
        return new EntityEventDataMongoStore();
    }
}
