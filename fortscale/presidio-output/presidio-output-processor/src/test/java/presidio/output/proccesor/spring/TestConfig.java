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
        properties.put("alert.page.size", 10);
        properties.put("elasticsearch.clustername", EmbeddedElasticsearchInitialiser.EL_TEST_CLUSTER);
        properties.put("elasticsearch.host", "localhost");
        properties.put("elasticsearch.port", EmbeddedElasticsearchInitialiser.EL_TEST_PORT);
        properties.put("enable.metrics.export", true);
        properties.put("number.of.classifications", 28);
        properties.put("output.events.limit", 100);
        properties.put("monitoring.fixed.rate", 1000000000);
        properties.put("output.events.page.size", 10);
        properties.put("indicators.contribution.limit.to.classification.percent", 0.3);
        properties.put("output.data.retention.in.days", 90);
        properties.put("entity.severity.compute.data.critical.percentage.of.entities", 1);
        properties.put("entity.severity.compute.data.critical.minimum.delta.factor", 1.5);
        properties.put("entity.severity.compute.data.critical.maximum.entities", 5);
        properties.put("entity.severity.compute.data.high.percentage.of.entities", 4);
        properties.put("entity.severity.compute.data.high.minimum.delta.factor", 1.3);
        properties.put("entity.severity.compute.data.high.maximum.entities", 10);
        properties.put("entity.severity.compute.data.medium.percentage.of.entities", 10);
        properties.put("entity.severity.compute.data.medium.minimum.delta.factor", 1.1);
        properties.put("entity.severity.compute.data.low.percentage.of.entities", 80);
        properties.put("indicators.store.page.size", 80);
        properties.put("events.store.page.size", 80);
        return new TestPropertiesPlaceholderConfigurer(properties);
    }

}
