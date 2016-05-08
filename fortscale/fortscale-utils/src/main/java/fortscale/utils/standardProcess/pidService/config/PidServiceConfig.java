package fortscale.utils.standardProcess.pidService.config;

import fortscale.utils.spring.PropertySourceConfigurer;
import fortscale.utils.standardProcess.pidService.PidService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * Created by baraks on 5/2/2016.
 */
@Configuration
public class PidServiceConfig {
    @Value("${fortscale.pid.folder}")
    private String pidDir;

    @Bean
    public PidService pidService()
    {
        return new PidService(pidDir);
    }

    @Bean
    private static PropertySourceConfigurer pidServicePropertyConfigurer() {
        Properties properties = PidServiceProperties.getProperties();
        PropertySourceConfigurer configurer = new PropertySourceConfigurer(PidServiceConfig.class, properties);
        return configurer;
    }

}
