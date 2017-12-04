package presidio.monitoring.spring.test;

import fortscale.utils.spring.PropertySourceConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import presidio.monitoring.spring.PresidioMonitoringConfiguration;

import java.util.Properties;

/**
 * Created by barak_schuster on 12/3/17.
 */

@Configuration
@ActiveProfiles("useEmbeddedElastic")
@Import(PresidioMonitoringConfiguration.class)
public class PresidioMonitoringTestConfig {

    @Bean
    private static PropertySourceConfigurer presidioMonitoringTestTestPropertiesConfigurer()
    {
        Properties properties = new Properties();
        properties.put("elasticsearch.clustername", "fortscale");
        properties.put("elasticsearch.host", "localhost");
        properties.put("elasticsearch.port", 9300);
        properties.put("enable.metrics.export", false);
        return new PropertySourceConfigurer(PresidioMonitoringTestConfig.class, properties);
    }
}