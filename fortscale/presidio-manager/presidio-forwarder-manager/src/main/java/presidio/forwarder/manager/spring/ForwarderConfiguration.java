package presidio.forwarder.manager.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.forwarder.manager.service.ConfigurationForwarderService;
import presidio.manager.api.service.ConfigurationProcessingService;

@Configuration
public class ForwarderConfiguration {


    @Bean(name = "configurationForwarderServcie")
    public ConfigurationProcessingService configurationForwarderService() {
        return new ConfigurationForwarderService();
    }
}
