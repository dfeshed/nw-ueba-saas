package presidio.security.manager.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.manager.api.service.ConfigurationProcessingService;
import presidio.security.manager.service.ConfigurationProcessingServiceImpl;

@Configuration
public class SecurityManagerConfiguration {


    @Bean(name = "securityManagerConfigurationProcessingService")
    public ConfigurationProcessingService securityManagerConfigurationProcessingService() {
        return new ConfigurationProcessingServiceImpl();
    }
}
