package presidio.output.manager.spring;

import fortscale.utils.elasticsearch.config.EmbeddedElasticsearchInitialiser;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.output.manager.config.OutputManagerConfiguration;

import java.util.Properties;

@Configuration
@Import({
        OutputManagerConfiguration.class
})
public class OutputManagerTestConfiguration {

    @Bean
    public static TestPropertiesPlaceholderConfigurer testPropertiesPlaceholderConfigurer() {
        Properties properties = new Properties();
        properties.put("elasticsearch.clustername", EmbeddedElasticsearchInitialiser.EL_TEST_CLUSTER);
        properties.put("elasticsearch.host", "localhost");
        properties.put("elasticsearch.port", EmbeddedElasticsearchInitialiser.EL_TEST_PORT);
        properties.put("enable.metrics.export", true);
        properties.put("monitoring.fixed.rate", 1000000000);
        properties.put("output.enriched.events.retention.in.days", 2);
        return new TestPropertiesPlaceholderConfigurer(properties);
    }
}
