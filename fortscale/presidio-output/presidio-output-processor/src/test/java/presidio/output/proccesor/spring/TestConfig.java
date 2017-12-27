package presidio.output.proccesor.spring;

import fortscale.utils.elasticsearch.config.EmbeddedElasticsearchInitialiser;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;

import java.util.Properties;

@Configuration
@EnableSpringConfigured
public class TestConfig {
    @Bean
    public static TestPropertiesPlaceholderConfigurer testPropertiesPlaceholderConfigurer() {
        Properties properties = new Properties();
        properties.put("severity.critical", 95);
        properties.put("severity.high", 90);
        properties.put("severity.mid", 80);
        properties.put("smart.threshold.score", 0);
        properties.put("smart.page.size", 50);
        properties.put("elasticsearch.clustername", EmbeddedElasticsearchInitialiser.EL_TEST_CLUSTER);
        properties.put("elasticsearch.host", "localhost");
        properties.put("elasticsearch.port", EmbeddedElasticsearchInitialiser.EL_TEST_PORT);
        properties.put("enable.metrics.export", false);
        properties.put("number.of.classifications", 19);
        properties.put("output.events.limit", 100);
        properties.put("output.events.page.size", 10);
        properties.put("user.severities.percent.threshold.critical", 75);
        properties.put("user.severities.percent.threshold.high", 50);
        properties.put("user.severities.percent.threshold.medium", 25);
        properties.put("indicators.contribution.limit.to.classification.percent", 0.3);
        properties.put("output.enriched.events.retention.in.days", 0);
        properties.put("output.result.events.retention.in.days", 0);
        return new TestPropertiesPlaceholderConfigurer(properties);
    }

}
