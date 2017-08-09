package presidio.manager.air.flow.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.manager.air.flow.service.ConfigurationProcessingServiceImpl;
import presidio.manager.api.service.ConfigurationProcessingService;

@Configuration
public class AirFlowConfiguration {


    @Bean(name = "airflowConfigurationProcessingService")
    public ConfigurationProcessingService airflowConfigurationProcessingService() {
        return new ConfigurationProcessingServiceImpl();
    }
}
