package fortscale.utils.process.hostnameService.config;

import fortscale.utils.process.hostnameService.HostNameServiceImpl;
import fortscale.utils.process.hostnameService.HostnameService;
import fortscale.utils.process.metrics.jvm.config.JVMMetricsServiceProperties;
import fortscale.utils.spring.PropertySourceConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class HostnameServiceConfig {

    @Value("${fortscale.hostname.cacheperiod}")
    private long cachePeriodSeconds;

    @Bean
    HostnameService hostnameService() {
        return new HostNameServiceImpl(cachePeriodSeconds);
    }

    @Bean
    private static PropertySourceConfigurer HostnameServiceEnvironmentPropertyConfigurer() {
        Properties properties = HostnameServiceProperties.getProperties();

        return new PropertySourceConfigurer(HostnameServiceConfig.class, properties);
    }

}
