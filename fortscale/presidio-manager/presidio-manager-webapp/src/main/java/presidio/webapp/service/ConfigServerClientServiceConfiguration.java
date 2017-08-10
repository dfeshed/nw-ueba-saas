package presidio.webapp.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Created by efratn on 09/08/2017.
 */
@Configuration
public class ConfigServerClientServiceConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ConfigurationServerClientService configurationServerClientService() {
        return new ConfigurationServerClientImpl(restTemplate());
    }
}
