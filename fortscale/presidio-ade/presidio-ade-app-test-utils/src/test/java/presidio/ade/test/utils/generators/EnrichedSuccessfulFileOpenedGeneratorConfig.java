package presidio.ade.test.utils.generators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.test.utils.EventsGenerator;

@Configuration
public class EnrichedSuccessfulFileOpenedGeneratorConfig {
    @Autowired
    private EnrichedDataStore enrichedDataStore;

    @Bean
    public EventsGenerator enrichedEventsFileOpenedGenerator()
    {
        return new EnrichedSuccessfulFileOpenedGenerator(enrichedDataStore);
    }
}
