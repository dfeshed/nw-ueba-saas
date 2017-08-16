package presidio.webapp.spring;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;
import presidio.config.server.client.ConfigurationServerClientService;
import presidio.config.server.client.ConfigurationServerClientServiceImpl;
import presidio.config.server.spring.ConfigServerClientServiceConfiguration;
import presidio.manager.airlfow.spring.AirflowConfiguration;
import presidio.manager.api.service.ConfigurationProcessingService;
import presidio.security.manager.spring.SecurityManagerConfiguration;
import presidio.webapp.controller.configuration.ConfigurationApi;
import presidio.webapp.controller.configuration.ConfigurationApiController;
import presidio.webapp.service.ConfigurationManagerService;

import javax.annotation.Resource;

@Configuration
@Import(value = {SecurityManagerConfiguration.class, AirflowConfiguration.class, ConfigServerClientServiceConfiguration.class})
public class ManagerWebappConfiguration {

    @Autowired
    @Resource(name = "configurationAirflowServcie")
    ConfigurationProcessingService configurationAirflowServcie;

    @Autowired
    @Resource(name = "configurationSecurityService")
    ConfigurationProcessingService configurationSecurityService;

    @Autowired
    private RestTemplate restTemplate;

    @Bean
    ConfigurationManagerService configurationServiceImpl() {
        return new ConfigurationManagerService(configurationAirflowServcie, configurationSecurityService);
    }

    @Bean
    public ConfigurationServerClientService configServerClient() {
        return new ConfigurationServerClientServiceImpl(restTemplate);
    }

    @Bean
    ConfigurationApi configurationApi() {
        return new ConfigurationApiController(configurationServiceImpl(), configServerClient());
    }
}
