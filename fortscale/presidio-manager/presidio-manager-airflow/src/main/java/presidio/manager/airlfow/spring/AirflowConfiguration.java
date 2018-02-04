package presidio.manager.airlfow.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.config.server.client.ConfigurationServerClientService;
import presidio.config.server.spring.ConfigServerClientServiceConfiguration;
import presidio.manager.airlfow.service.ConfigurationAirflowService;
import presidio.manager.api.service.ConfigurationProcessingService;

import java.util.List;

@Configuration
@Import(ConfigServerClientServiceConfiguration.class)
public class AirflowConfiguration {

    @Value("#{'${spring.profiles.active:default}'.split(',')}")
    private List<String> activeProfiles;
    @Value("${presidio.workflows.moduleName:workflows}")
    private String moduleName;
    @Value("${presidio.workflows.config.path:/home/presidio/presidio-core/configurations/airflow}")
    private String configurationFilePath;

    @Autowired
    private ConfigurationServerClientService configServerClient;


    @Bean(name = "configurationAirflowService")
    public ConfigurationProcessingService configurationAirflowServcie() {
        return new ConfigurationAirflowService(configServerClient, moduleName, activeProfiles, configurationFilePath);
    }
}
