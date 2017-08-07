package presidio.ade.test.utils.generators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.test.utils.EnrichedEventsGenerator;

@Configuration
public class EnrichedFileGeneratorConfig {
    @Autowired
    private EnrichedDataStore enrichedDataStore;
    @Bean
    public EnrichedEventsGenerator enrichedEventsGenerator()
    {
        return new EnrichedFileGenerator(enrichedDataStore);
    }
}
