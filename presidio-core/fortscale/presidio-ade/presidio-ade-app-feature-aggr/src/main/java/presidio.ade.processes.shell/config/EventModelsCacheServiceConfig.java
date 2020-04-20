package presidio.ade.processes.shell.config;

import fortscale.ml.model.cache.EventModelsCacheService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by barak_schuster on 7/30/17.
 */
@Configuration
@Import(ModelCacheServiceInMemoryConfig.class)
public class EventModelsCacheServiceConfig
{
    @Bean
    public EventModelsCacheService eventModelsCacheService()
    {
        return new EventModelsCacheService();
    }
}
