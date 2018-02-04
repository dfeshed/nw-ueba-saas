package presidio.webapp.spring;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.config.server.client.ConfigurationServerClientService;
import presidio.config.server.spring.ConfigServerClientServiceConfiguration;
import presidio.forwarder.manager.spring.ForwardingConfiguration;
import presidio.manager.airlfow.spring.AirflowConfiguration;
import presidio.manager.api.service.ConfigurationProcessingService;
import presidio.manager.api.service.ManagerService;
import presidio.manager.api.service.ManagerServiceConfig;
import presidio.security.manager.spring.SecurityManagerConfiguration;
import presidio.webapp.controller.configuration.ConfigurationApi;
import presidio.webapp.controller.configuration.ConfigurationApiController;
import presidio.webapp.controller.datapipeline.DataPipelineApi;
import presidio.webapp.controller.datapipeline.DataPipelineApiController;
import presidio.webapp.service.ConfigurationManagerService;

import javax.annotation.Resource;
import java.util.List;

@Configuration
@Import(value = {SecurityManagerConfiguration.class, AirflowConfiguration.class, ManagerServiceConfig.class, ConfigServerClientServiceConfiguration.class, ForwardingConfiguration.class})
public class ManagerWebappConfiguration {

    @Value("#{'${spring.profiles.active:default}'.split(',')}")
    private List<String> activeProfiles;
    @Value("${keytab.file.path}")
    private String keytabFileLocation;

    @Autowired
    private ManagerService managerService;

    @Autowired
    @Resource(name = "configurationAirflowService")
    ConfigurationProcessingService configurationAirflowServcie;

    @Autowired
    @Resource(name = "configurationSecurityService")
    ConfigurationProcessingService configurationSecurityService;

    @Autowired
    @Resource(name = "configurationForwarderService")
    ConfigurationProcessingService configurationForwarderService;

    @Autowired
    private ConfigurationServerClientService configServerClient;

    @Bean
    ConfigurationManagerService configurationServiceImpl() {
        return new ConfigurationManagerService(configurationAirflowServcie, configurationSecurityService, configurationForwarderService);
    }

    @Bean
    ConfigurationApi configurationApi() {
        return new ConfigurationApiController(configurationServiceImpl(), configServerClient,
                activeProfiles, keytabFileLocation);
    }

    @Bean
    DataPipelineApi dataPipelineApi() {
        return new DataPipelineApiController(managerService);
    }
}
