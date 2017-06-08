package presidio.ade.sdk.executions.online;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.domain.store.enriched.EnrichedDataStoreConfig;

/**
 * Created by barak_schuster on 5/22/17.
 */
@Configuration
@Import(EnrichedDataStoreConfig.class)
public class ADEOnlineSDKConfig {
    @Autowired
    private EnrichedDataStore enrichedDataStore;

    @Bean
    public ADEOnlineSDK adeOnlineSDK() {
        return new ADEOnlineSDK(enrichedDataStore);
    }
}
