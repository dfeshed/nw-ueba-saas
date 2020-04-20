package presidio.ade.processes.shell;

import fortscale.utils.elasticsearch.config.ElasticsearchTestConfig;
import fortscale.utils.elasticsearch.config.EmbeddedElasticsearchInitialiser;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.monitoring.spring.PresidioMonitoringConfiguration;

import java.util.Collections;
import java.util.Properties;

/**
 * @author Barak Schuster
 */
@Configuration
@Import({
        MongodbTestConfig.class,
        PresidioMonitoringConfiguration.class,
        ElasticsearchTestConfig.class
})
public class FeatureAggregationsConfigurationTest extends FeatureAggregationsConfiguration {
    @Bean
    public static TestPropertiesPlaceholderConfigurer featureAggregationsApplicationTestProperties() {
        Properties properties = new Properties();

        properties.put("fortscale.model.aggregation.bucket.conf.json.file.name", "classpath:config/asl/feature-buckets/empty-buckets/*.json");
        properties.put("fortscale.feature.aggregation.bucket.conf.json.file.name", "classpath:config/asl/feature-buckets/feature-aggregation/*.json");
        properties.put("fortscale.aggregation.feature.event.conf.json.file.name", "classpath:config/asl/aggregation-records/feature-aggregation-records/*.json");
        properties.put("presidio.modeling.base.configurations.path", "classpath:config/asl/models/feature-aggregation-records/*.json");
        properties.put("fortscale.scorer.configurations.location.path", "classpath:config/asl/scorers/feature-aggregation-records/*.json");
        properties.put("presidio.level.three.feature.bucket.conf.json.file.path", "classpath:config/asl/feature-buckets/level-three-aggregation/*.json");
        properties.put("presidio.level.three.aggregated.feature.event.conf.json.file.path", "classpath:config/asl/aggregation-records/level-three-aggregation-records/*.json");

        properties.put("fortscale.model.cache.maxDiffBetweenCachedModelAndEvent", "PT48H");
        properties.put("fortscale.model.cache.size", 100);
        properties.put("feature.aggregation.pageIterator.pageSize", 1000);
        properties.put("feature.aggregation.pageIterator.maxGroupSize", 100);
        properties.put("spring.application.name", "test-app-name");
        properties.put("presidio.default.ttl.duration", "PT48H");
        properties.put("presidio.default.cleanup.interval", "PT24H");
        properties.put("enable.metrics.export", false);
        properties.put("elasticsearch.clustername", EmbeddedElasticsearchInitialiser.EL_TEST_CLUSTER);
        properties.put("elasticsearch.host", "localhost");
        properties.put("elasticsearch.port", EmbeddedElasticsearchInitialiser.EL_TEST_PORT);
        properties.put("monitoring.fixed.rate", "60000");
        properties.put("datadog.host", "localhost");
        properties.put("datadog.port", 8125);
        properties.put("datadog.metrics", Collections.emptyList());

        return new TestPropertiesPlaceholderConfigurer(properties);
    }
}
