package presidio.ade.manager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.manager.AdeManagerApplicationService;
import presidio.ade.sdk.common.AdeManagerSdk;
import presidio.ade.sdk.common.AdeManagerSdkConfig;

import java.time.Duration;

/**
 * Created by maria_dorohin on 9/6/17.
 */
@Configuration
@Import({
        AdeManagerSdkConfig.class
})
public class AdeManagerApplicationConfig {

    @Autowired
    private AdeManagerSdk adeManagerSdk;
    @Value("#{T(java.time.Duration).parse('${presidio.enriched.ttl.duration}')}")
    private Duration enrichedTtl;
    @Value("#{T(java.time.Duration).parse('${presidio.enriched.cleanup.interval}')}")
    private Duration enrichedCleanupInterval;


    @Bean
    public AdeManagerApplicationService managerApplicationService(){
        return new AdeManagerApplicationService(adeManagerSdk, enrichedTtl, enrichedCleanupInterval);
    }
}
