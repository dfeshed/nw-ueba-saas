package presidio.ade.test.utils.generators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.test.utils.EventsGenerator;

@Configuration
public class EnrichedFileGeneratorConfig {
    @Autowired
    private EnrichedDataStore enrichedDataStore;
    @Bean
    public EventsGenerator enrichedEventsGenerator()
    {
        return new EnrichedFileGenerator(enrichedDataStore);
    }
}
