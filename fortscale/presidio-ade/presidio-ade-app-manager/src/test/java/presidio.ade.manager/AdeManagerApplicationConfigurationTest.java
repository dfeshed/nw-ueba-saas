package presidio.ade.manager;

import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.manager.config.AdeManagerApplicationConfig;
import presidio.monitoring.elastic.repositories.MetricRepository;
import presidio.monitoring.spring.PresidioMonitoringConfiguration;

import java.util.Properties;


@Configuration
@Import({MongodbTestConfig.class,PresidioMonitoringConfiguration.class})
public class AdeManagerApplicationConfigurationTest extends AdeManagerApplicationConfig {

    @MockBean
    private MetricRepository metricRepository;

    @Bean
    public static TestPropertiesPlaceholderConfigurer managerApplicationTestProperties() {
        Properties properties = new Properties();
        properties.put("spring.application.name", "ade-manager");
        properties.put("presidio.enriched.ttl.duration", "PT5H");
        properties.put("presidio.enriched.cleanup.interval", "PT24H");
        properties.put("enable.metrics.export", false);
        properties.put("monitoring.fixed.rate","60000");

        return new TestPropertiesPlaceholderConfigurer(properties);
    }


}