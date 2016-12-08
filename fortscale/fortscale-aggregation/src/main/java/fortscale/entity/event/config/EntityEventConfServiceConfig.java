package fortscale.entity.event.config;

import fortscale.entity.event.EntityEventConfService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by barak_schuster on 10/9/16.
 */
@Configuration
@Import({EntityEventGlobalParamsConfServiceConfig.class})
public class EntityEventConfServiceConfig {
    @Bean
    public EntityEventConfService entityEventConfService()
    {
        return new EntityEventConfService();
    }
}
