package fortscale.presidio.remote.conf.spring;

import fortscale.presidio.remote.conf.ConfigrationServerClientUtilsMock;
import fortscale.utils.configurations.ConfigrationServerClientUtils;
import fortscale.utils.configurations.ConfigrationServerClientUtilsImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.client.RestTemplate;

@Configuration
@Profile("mock-conf")
@PropertySource("classpath:config/presidio-uiconf.properties")
public class PresidioUiRemoteConfigurationClientMockConfiguration {


    @Bean
    //Util to load configuration on demand
    ConfigrationServerClientUtils configrationServerClientUtils(){

        return new ConfigrationServerClientUtilsMock();
    }
}
