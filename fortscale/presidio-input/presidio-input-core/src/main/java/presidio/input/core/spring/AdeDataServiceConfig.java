package presidio.input.core.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.domain.store.enriched.EnrichedDataStoreConfig;
import presidio.ade.sdk.executions.common.ADEManagerSDK;
import presidio.ade.sdk.executions.online.ADEManagerSDKConfig;
import presidio.input.core.services.data.AdeDataService;
import presidio.input.core.services.data.AdeDataServiceImpl;

@Configuration
@Import({ADEManagerSDKConfig.class})
public class AdeDataServiceConfig {

    @Autowired
    private ADEManagerSDK adeManagerSDK;

    @Bean
    public AdeDataService adeDataService() {
        return new AdeDataServiceImpl(adeManagerSDK);
    }
}
