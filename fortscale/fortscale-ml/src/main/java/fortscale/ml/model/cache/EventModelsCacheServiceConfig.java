package fortscale.ml.model.cache;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by barak_schuster on 6/15/17.
 */
@Configuration
@Import(ModelCacheServiceInMemoryConfig.class)
public class EventModelsCacheServiceConfig {
    @Bean
    public EventModelsCacheService eventModelsCacheService()
    {
        return new EventModelsCacheService();
    }
}
