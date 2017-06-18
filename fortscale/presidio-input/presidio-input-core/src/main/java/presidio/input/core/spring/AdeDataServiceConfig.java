package presidio.input.core.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.domain.store.enriched.EnrichedDataStoreConfig;
import presidio.input.core.services.data.AdeDataService;
import presidio.input.core.services.data.AdeDataServiceImpl;

@Configuration
@Import({EnrichedDataStoreConfig.class})
public class AdeDataServiceConfig {

    @Autowired
    private EnrichedDataStore enrichedDataStore;

    @Bean
    public AdeDataService adeDataService() {
        return new AdeDataServiceImpl(enrichedDataStore);
    }
}
