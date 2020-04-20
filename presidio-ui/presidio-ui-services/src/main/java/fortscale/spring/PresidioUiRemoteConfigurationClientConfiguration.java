package fortscale.spring;

import fortscale.utils.configurations.ConfigrationServerClientUtils;
import fortscale.utils.configurations.ConfigrationServerClientUtilsImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

@Configuration
@Profile("!mock-conf")
public class PresidioUiRemoteConfigurationClientConfiguration {

    @Value("${spring.cloud.config.uri}")
    String configurationServerUrl;

    @Value("${spring.cloud.config.username}")
    String configurationServerUserName;

    @Value("${spring.cloud.config.password}")
    String configurationServerUserNamePassword;




    @Bean
    ConfigrationServerClientUtils configrationServerClientUtils(){
        RestTemplate restTemplate = new RestTemplate();
        return new ConfigrationServerClientUtilsImpl(restTemplate,configurationServerUrl,configurationServerUserName,configurationServerUserNamePassword);
    }
}
