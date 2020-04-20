package presidio.ade.test.utils.generators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.test.utils.EventsGenerator;
import presidio.data.generators.common.GeneratorException;

@Configuration
public class EnrichedSuccessfulFileOpenedGeneratorConfig {
    @Autowired
    private EnrichedDataStore enrichedDataStore;

    @Bean
    public EventsGenerator enrichedEventsFileOpenedGenerator() throws GeneratorException {
        return new EnrichedSuccessfulFileOpenedGenerator(enrichedDataStore);
    }
}
