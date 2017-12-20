package presidio.ade.processes.shell.feature.aggregation.buckets;

import fortscale.utils.elasticsearch.config.EmbeddedElasticsearchInitialiser;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.processes.shell.feature.aggregation.buckets.config.ModelFeatureAggregationBucketsConfiguration;
import presidio.monitoring.elastic.repositories.MetricRepository;
import presidio.monitoring.spring.PresidioMonitoringConfiguration;

import java.util.Properties;

@Configuration
@Import({MongodbTestConfig.class, PresidioMonitoringConfiguration.class})
public class ModelFeatureAggregationBucketsApplicationConfigTest extends ModelFeatureAggregationBucketsConfiguration{
    @MockBean
    private MetricRepository metricRepository;


    @Bean
    public static TestPropertiesPlaceholderConfigurer modelFeatureAggregationBucketsApplicationTestProperties() {
        Properties properties = new Properties();
        //        start ASL paths configurations
        properties.put("fortscale.model.aggregation.bucket.conf.json.file.name", "classpath:config/asl/feature-buckets/models/enriched-records/*.json");

        //        end ASL paths configurations

        properties.put("fortscale.model.cache.maxDiffBetweenCachedModelAndEvent", "PT48H");
        properties.put("fortscale.model.cache.size", 100);
        properties.put("spring.application.name", "test-app-name");
        properties.put("presidio.default.ttl.duration", "PT48H");
        properties.put("presidio.default.cleanup.interval", "PT24H");
        properties.put("model-feature-aggregation.pageIterator.maxGroupSize",1000);
        properties.put("model-feature-aggregation.pageIterator.pageSize",1000);

        properties.put("enable.metrics.export", false);
        properties.put("elasticsearch.clustername", EmbeddedElasticsearchInitialiser.EL_TEST_CLUSTER);
        properties.put("elasticsearch.host", "localhost");
        properties.put("elasticsearch.port", EmbeddedElasticsearchInitialiser.EL_TEST_PORT);
        properties.put("monitoring.fixed.rate","60000");
        return new TestPropertiesPlaceholderConfigurer(properties);
    }
}
