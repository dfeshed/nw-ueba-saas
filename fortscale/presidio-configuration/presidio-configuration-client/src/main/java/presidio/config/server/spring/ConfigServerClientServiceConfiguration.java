package presidio.config.server.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import presidio.config.server.client.ConfigurationServerClientServiceImpl;
import presidio.config.server.client.ConfigurationServerClientService;

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
        return new ConfigurationServerClientServiceImpl(restTemplate());
    }
}
