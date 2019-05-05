package presidio.manager.webapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.config.server.client.ConfigurationServerClientService;
import presidio.config.server.spring.ConfigServerClientServiceConfiguration;
import presidio.manager.webapp.configuration.ConfigurationApi;
import presidio.manager.webapp.configuration.ConfigurationApiController;

import java.util.List;

@Configuration
@Import(value = {ConfigServerClientServiceConfiguration.class})
@SuppressWarnings("SpringFacetCodeInspection")
public class ManagerWebappConfiguration {
    @Value("#{'${spring.profiles.active:default}'.split(',')}")
    private List<String> profiles;
    @Value("${keytab.file.pathname:/etc/krb5.keytab}")
    private String keytabFilePathname;
    @Value("${presidio.workflows.module.name:workflows}")
    private String workflowsModuleName;
    @Value("${presidio.workflows.configuration.path:/etc/netwitness/presidio/configserver/configurations/airflow}")
    private String workflowsConfigurationPath;

    @Autowired
    private ConfigurationServerClientService configurationServerClientService;

    @Bean
    public ConfigurationApi configurationApi() {
        return new ConfigurationApiController(
                profiles,
                keytabFilePathname,
                workflowsModuleName,
                workflowsConfigurationPath,
                configurationServerClientService);
    }
}
