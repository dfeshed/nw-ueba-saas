package presidio.input.core.spring;

import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import presidio.monitoring.elastic.repositories.MetricRepository;
import presidio.monitoring.spring.PresidioMonitoringConfiguration;

import java.util.Properties;

@Configuration
@Import({
        InputCoreConfigurationTest.class,
        MongodbTestConfig.class,
        PresidioMonitoringConfiguration.class})
@EnableSpringConfigured
public class InputConfigTest {

    @MockBean
    private MetricRepository metricRepository;

    @Bean
    public static TestPropertiesPlaceholderConfigurer inputCoreTestConfigurer() {
        Properties properties = new Properties();
        properties.put("page.iterator.page.size", "1000");
        properties.put("enable.metrics.export", "false");
        properties.put("output.events.limit", "1000");
        properties.put("input.events.retention.in.days", "2");
        properties.put("operation.type.category.mapping.file.path", "file:/home/presidio/presidio-core/configurations/operation-type-category-mapping.json");
        return new TestPropertiesPlaceholderConfigurer(properties);

    }
}
