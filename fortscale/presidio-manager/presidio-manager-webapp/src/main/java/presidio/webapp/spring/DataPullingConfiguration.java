package presidio.webapp.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.manager.api.service.ConfigurationProcessingService;
import presidio.webapp.service.ConfigurationDataPullingService;

@Configuration
public class DataPullingConfiguration {


    @Bean(name = "configurationDataPullingService")
    public ConfigurationProcessingService configurationForwarderService() {
        return new ConfigurationDataPullingService();
    }
}
