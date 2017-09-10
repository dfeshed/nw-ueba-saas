package presidio.ade.manager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.manager.ManagerApplicationService;
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
public class ManagerApplicationConfig {

    @Autowired
    private AdeManagerSdk adeManagerSdk;
    @Value("#{T(java.time.Duration).parse('${presidio.enriched.ttl.duration}')}")
    private Duration ttl;
    @Value("#{T(java.time.Duration).parse('${presidio.enriched.cleanup.interval}')}")
    private Duration cleanup;


    @Bean
    ManagerApplicationService managerApplicationService(){
        return new ManagerApplicationService(adeManagerSdk, ttl, cleanup);
    }
}
