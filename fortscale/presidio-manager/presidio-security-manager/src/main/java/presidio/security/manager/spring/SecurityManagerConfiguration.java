package presidio.security.manager.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.manager.api.service.ConfigurationProcessingService;
import presidio.security.manager.service.ConfigurationSecurityService;

@Configuration
public class SecurityManagerConfiguration {


    @Bean(name = "configurationSecurityService")
    public ConfigurationProcessingService configurationSecurityService() {
        return new ConfigurationSecurityService();
    }
}
