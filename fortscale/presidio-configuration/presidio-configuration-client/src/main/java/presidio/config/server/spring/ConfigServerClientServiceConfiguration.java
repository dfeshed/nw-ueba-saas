package presidio.config.server.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.client.RestTemplate;
import presidio.config.server.client.ConfigurationServerClientService;
import presidio.config.server.client.ConfigurationServerClientServiceImpl;

@Configuration
@PropertySource("classpath:configServerClientBootstrap.properties")
public class ConfigServerClientServiceConfiguration {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Value("${spring.cloud.config.uri}")
    private  String configServerUri;

    @Value("${spring.cloud.config.username}")
    private  String configServerUserName;

    @Value("${spring.cloud.config.password}")
    private  String configServerPassword;

    @Bean
    public ConfigurationServerClientService configurationServerClientService() {
        final RestTemplate restTemplate = new RestTemplate();
        return new ConfigurationServerClientServiceImpl(restTemplate, configServerUri, configServerUserName, configServerPassword);
    }
}
