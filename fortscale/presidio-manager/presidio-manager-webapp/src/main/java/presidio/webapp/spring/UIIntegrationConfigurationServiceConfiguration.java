package presidio.webapp.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.manager.api.service.ConfigurationProcessingService;
import presidio.webapp.service.ConfigurationDataPullingService;
import presidio.webapp.service.UIIntegrationConfigurationService;

@Configuration
public class UIIntegrationConfigurationServiceConfiguration {
    @Bean(name = "uiIntegrationConfigurationService")
    public ConfigurationProcessingService uiIntegrationConfigurationService() {
        return new UIIntegrationConfigurationService();
    }
}
