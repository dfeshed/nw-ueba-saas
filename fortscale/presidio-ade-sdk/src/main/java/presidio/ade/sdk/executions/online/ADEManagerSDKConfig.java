package presidio.ade.sdk.executions.online;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.domain.store.enriched.EnrichedDataStoreConfig;
import presidio.ade.domain.store.smart.SmartDataStore;
import presidio.ade.domain.store.smart.SmartDataStoreMongoConfig;
import presidio.ade.sdk.executions.common.ADEManagerSDK;
import presidio.ade.sdk.executions.common.ADEManagerSDKImpl;

/**
 * Created by barak_schuster on 5/22/17.
 */
@Configuration
@Import({EnrichedDataStoreConfig.class, SmartDataStoreMongoConfig.class})
public class ADEManagerSDKConfig {
    @Autowired
    private EnrichedDataStore enrichedDataStore;

    @Autowired
    private SmartDataStore smartDataStore;

    @Bean
    public ADEManagerSDK adeOnlineSDK() {
        return new ADEManagerSDKImpl(enrichedDataStore, smartDataStore);
    }
}
