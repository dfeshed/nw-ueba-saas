package presidio.manager.airlfow.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.manager.airlfow.service.ConfigurationAirflowServcie;
import presidio.manager.api.service.ConfigurationProcessingService;

@Configuration
public class AirflowConfiguration {


    @Bean(name = "configurationAirflowServcie")
    public ConfigurationProcessingService configurationAirflowServcie() {
        return new ConfigurationAirflowServcie();
    }
}
