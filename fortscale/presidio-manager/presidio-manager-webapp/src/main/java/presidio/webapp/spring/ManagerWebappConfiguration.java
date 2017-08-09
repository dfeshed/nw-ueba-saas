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
import presidio.webapp.model.configuration.DataConfiguration;
import presidio.webapp.model.configuration.ModelConfiguration;
import presidio.webapp.model.configuration.SystemConfiguration;
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
    SystemConfiguration systemConfiguration() {
        return new SystemConfiguration();
    }

    @Bean
    DataConfiguration dataConfiguration() {
        return new DataConfiguration();
    }

    @Bean
    ModelConfiguration modelConfiguration() {
        return new ModelConfiguration().system(systemConfiguration()).dataPipeline(dataConfiguration());
    }

    @Bean
    ConfigurationProcessingManager configurationServiceImpl() {
        return new ConfigurationProcessingManager(ConfigurationProcessingServiceAirflow, ConfigurationProcessingServiceSecurityManager, modelConfiguration());
    }

    @Bean
    ConfigurationApi configurationApi() {
        return new ConfigurationApiController(configurationServiceImpl());
    }
}
