package presidio.security.manager.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.config.server.client.ConfigurationServerClientService;
import presidio.config.server.spring.ConfigServerClientServiceConfiguration;
import presidio.manager.api.service.ConfigurationProcessingService;
import presidio.security.manager.service.ConfigurationSecurityService;

@Configuration
@Import(ConfigServerClientServiceConfiguration.class)
public class SecurityManagerConfiguration {

    @Autowired
    @Qualifier("configurationServerClientService")
    private ConfigurationServerClientService configurationServerClientService;

    @Bean(name = "configurationSecurityService")
    public ConfigurationProcessingService configurationSecurityService() {
        return new ConfigurationSecurityService(configurationServerClientService);
    }
}
