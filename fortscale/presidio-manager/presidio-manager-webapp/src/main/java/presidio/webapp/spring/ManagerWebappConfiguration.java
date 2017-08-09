package presidio.webapp.spring;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.manager.air.flow.spring.AirFlowConfiguration;
import presidio.manager.api.service.ConfigurationProcessingService;
import presidio.security.manager.spring.SecurityManagerConfiguration;
import presidio.webapp.controller.configuration.ConfigurationApi;
import presidio.webapp.controller.configuration.ConfigurationApiController;
import presidio.webapp.service.ConfigurationProcessingManager;

import javax.annotation.Resource;

@Configuration
@Import(value = {SecurityManagerConfiguration.class, AirFlowConfiguration.class})
public class ManagerWebappConfiguration {

    @Autowired
    @Resource(name = "airflowConfigurationProcessingService")
    ConfigurationProcessingService ConfigurationProcessingServiceAirflow;

    @Autowired
    @Resource(name = "securityManagerConfigurationProcessingService")
    ConfigurationProcessingService ConfigurationProcessingServiceSecurityManager;


    @Bean
    ConfigurationProcessingManager configurationServiceImpl() {
        return new ConfigurationProcessingManager(ConfigurationProcessingServiceAirflow, ConfigurationProcessingServiceSecurityManager);
    }

    @Bean
    ConfigurationApi configurationApi() {
        return new ConfigurationApiController(configurationServiceImpl());
    }
}
