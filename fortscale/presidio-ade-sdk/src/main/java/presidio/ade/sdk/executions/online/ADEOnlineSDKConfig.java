package presidio.ade.sdk.executions.online;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.domain.store.input.store.ADEInputDataStore;
import presidio.ade.domain.store.input.store.ADEInputDataStoreConfig;

/**
 * Created by barak_schuster on 5/22/17.
 */
@Configuration
@Import(ADEInputDataStoreConfig.class)
public class ADEOnlineSDKConfig {
    @Autowired
    private ADEInputDataStore adeInputDataStore;

    @Bean
    public ADEOnlineSDK adeOnlineSDK() {
        return new ADEOnlineSDK(adeInputDataStore);
    }
}
