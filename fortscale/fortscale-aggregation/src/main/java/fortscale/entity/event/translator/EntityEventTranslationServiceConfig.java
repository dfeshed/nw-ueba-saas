package fortscale.entity.event.translator;

/**
 * Created by barak_schuster on 10/9/16.
 */

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EntityEventTranslationServiceConfig {
    @Value("${streaming.event.field.type.entity_event}")
    private String eventTypeFieldValue;

    @Bean
    public EntityEventTranslationService entityEventTranslationService()
    {
        return new EntityEventTranslationService(eventTypeFieldValue);
    }

}
