package presidio.ade.test.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.ade.domain.store.enriched.EnrichedDataStore;

@Configuration
public class EnrichedEventsGeneratorConfig {
    @Autowired
    private EnrichedDataStore enrichedDataStore;

    @Bean
    public EnrichedEventsGenerator enrichedEventsGenerator()
    {
        return new EnrichedEventsGeneratorImpl(enrichedDataStore);
    }
}
