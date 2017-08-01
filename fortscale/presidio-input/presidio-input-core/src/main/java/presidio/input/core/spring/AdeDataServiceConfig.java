package presidio.input.core.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.sdk.common.AdeManagerSdk;
import presidio.ade.sdk.common.AdeManagerSdkConfig;
import presidio.input.core.services.data.AdeDataService;
import presidio.input.core.services.data.AdeDataServiceImpl;

@Configuration
@Import(AdeManagerSdkConfig.class)
public class AdeDataServiceConfig {
    @Autowired
    private AdeManagerSdk adeManagerSdk;

    @Bean
    public AdeDataService adeDataService() {
        return new AdeDataServiceImpl(adeManagerSdk);
    }
}
