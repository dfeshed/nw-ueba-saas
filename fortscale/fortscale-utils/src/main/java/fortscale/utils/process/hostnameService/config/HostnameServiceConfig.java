package fortscale.utils.process.hostnameService.config;

import fortscale.utils.process.hostnameService.HostNameServiceImpl;
import fortscale.utils.process.hostnameService.HostnameService;
import fortscale.utils.spring.PropertySourceConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class HostnameServiceConfig {

    @Value("${fortscale.process.hostname.service.cache.maxage}")
    private long cacheMaxAge;

    @Value("${fortscale.process.hostname.service.disable}")
    private long disabled;

    @Bean
    HostnameService hostnameService() {
        if (disabled != 0) {
            return null;
        }
        return new HostNameServiceImpl(cacheMaxAge);
    }

    @Bean
    private static PropertySourceConfigurer HostnameServiceEnvironmentPropertyConfigurer() {
        Properties properties = HostnameServiceProperties.getProperties();

        return new PropertySourceConfigurer(HostnameServiceConfig.class, properties);
    }

}
