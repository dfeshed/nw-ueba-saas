package presidio.forwarder.manager.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.config.server.client.ConfigurationServerClientService;
import presidio.config.server.spring.ConfigServerClientServiceConfiguration;
import presidio.forwarder.manager.service.ConfigurationFarwarderService;
import presidio.manager.api.service.ConfigurationProcessingService;

@Configuration
@Import(ConfigServerClientServiceConfiguration.class)
public class ForwarderConfiguration {

    @Autowired
    private ConfigurationServerClientService configServerClient;

    @Bean(name = "configurationForwarderServcie")
    public ConfigurationProcessingService configurationForwarderService(){
        return new ConfigurationFarwarderService(configServerClient);
    }
}
