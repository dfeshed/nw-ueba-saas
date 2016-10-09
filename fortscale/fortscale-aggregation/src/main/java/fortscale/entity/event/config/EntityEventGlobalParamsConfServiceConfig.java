package fortscale.entity.event.config;

import fortscale.entity.event.EntityEventGlobalParamsConfService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by barak_schuster on 10/9/16.
 */
@Configuration
public class EntityEventGlobalParamsConfServiceConfig {
    @Bean
    public EntityEventGlobalParamsConfService entityEventGlobalParamsConfService()
    {
        return new EntityEventGlobalParamsConfService();
    }
}
