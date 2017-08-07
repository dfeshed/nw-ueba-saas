package presidio.webapp.spring;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.webapp.controller.configuration.ConfigurationApi;
import presidio.webapp.controller.configuration.ConfigurationApiController;
import presidio.webapp.service.ConfigurationService;
import presidio.webapp.service.ConfigurationServiceImpl;

@Configuration
public class ManagerWebappConfiguration {

    @Bean
    ConfigurationService configurationService(){
        return new ConfigurationServiceImpl();
    }

    @Bean
    ConfigurationApi configurationApi() {
        return new ConfigurationApiController(configurationService());
    }
}
