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
        properties.put("number.of.classifications", 23);
        properties.put("output.events.limit", 100);
        properties.put("output.events.page.size", 10);
        properties.put("indicators.contribution.limit.to.classification.percent", 0.3);
        properties.put("output.enriched.events.retention.in.days", 2);
        properties.put("output.data.retention.in.days", 90);
        properties.put("user.severity.compute.data.critical.percentage.of.users", 1);
        properties.put("user.severity.compute.data.critical.minimum.delta.factor", 1.5);
        properties.put("user.severity.compute.data.critical.maximum.users", 5);
        properties.put("user.severity.compute.data.high.percentage.of.users", 4);
        properties.put("user.severity.compute.data.high.minimum.delta.factor", 1.3);
        properties.put("user.severity.compute.data.high.maximum.users", 10);
        properties.put("user.severity.compute.data.medium.percentage.of.users", 10);
        properties.put("user.severity.compute.data.medium.minimum.delta.factor", 1.1);
        properties.put("user.severity.compute.data.low.percentage.of.users", 80);
        properties.put("indicators.store.page.size", 80);
        properties.put("events.store.page.size", 80);
        return new TestPropertiesPlaceholderConfigurer(properties);
    }

}
