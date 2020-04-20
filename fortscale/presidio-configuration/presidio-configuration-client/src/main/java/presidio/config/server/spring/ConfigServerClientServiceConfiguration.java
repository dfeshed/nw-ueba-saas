package presidio.config.server.spring;

import fortscale.utils.RestTemplateConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.client.RestTemplate;
import presidio.config.server.client.ConfigurationServerClientService;
import presidio.config.server.client.ConfigurationServerClientServiceImpl;

@Configuration
@PropertySource("classpath:configServerClientBootstrap.properties")
@Import(RestTemplateConfig.class)
public class ConfigServerClientServiceConfiguration {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Autowired
    private RestTemplate restTemplate;
    @Value("${spring.cloud.config.uri}")
    private  String configServerUri;

    @Value("${spring.cloud.config.username}")
    private  String configServerUserName;

    @Value("${spring.cloud.config.password}")
    private  String configServerPassword;

    @Bean
    public ConfigurationServerClientService configurationServerClientService() {
        return new ConfigurationServerClientServiceImpl(restTemplate, configServerUri, configServerUserName, configServerPassword);
    }
}
