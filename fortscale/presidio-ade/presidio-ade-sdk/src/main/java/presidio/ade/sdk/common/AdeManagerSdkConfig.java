package presidio.ade.sdk.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.domain.store.enriched.EnrichedDataStoreConfig;
import presidio.ade.domain.store.smart.SmartDataStore;
import presidio.ade.domain.store.smart.SmartDataStoreMongoConfig;

/**
 * @author Barak Schuster
 */
@Configuration
@Import({EnrichedDataStoreConfig.class, SmartDataStoreMongoConfig.class})
public class AdeManagerSdkConfig {
    @Autowired
    private EnrichedDataStore enrichedDataStore;

    @Autowired
    private SmartDataStore smartDataStore;

    @Bean
    public AdeManagerSdk adeManagerSdk() {
        return new AdeManagerSdkImpl(enrichedDataStore, smartDataStore);
    }
}
