package presidio.ade.processes.shell;

import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.processes.shell.config.AccumulateSmartConfiguration;
import presidio.monitoring.elastic.repositories.MetricRepository;
import presidio.monitoring.spring.PresidioMonitoringConfiguration;

import java.util.Properties;

@Configuration
@Import({MongodbTestConfig.class,PresidioMonitoringConfiguration.class})
public class AccumulateSmartApplicationConfigurationTest extends AccumulateSmartConfiguration {
    @MockBean
    private MetricRepository metricRepository;

    @Bean
    public static TestPropertiesPlaceholderConfigurer accumulateSmartApplicationTestProperties() {
        Properties properties = new Properties();
        properties.put("smart.pageIterator.pageSize", 1000);
        properties.put("smart.pageIterator.maxGroupSize", 100);
        properties.put("spring.application.name", "test-app-name");
        properties.put("presidio.default.ttl.duration", "PT48H");
        properties.put("presidio.default.cleanup.interval", "PT24H");
        properties.put("enable.metrics.export", false);
        properties.put("monitoring.fixed.rate","60000");
        return new TestPropertiesPlaceholderConfigurer(properties);
    }

}