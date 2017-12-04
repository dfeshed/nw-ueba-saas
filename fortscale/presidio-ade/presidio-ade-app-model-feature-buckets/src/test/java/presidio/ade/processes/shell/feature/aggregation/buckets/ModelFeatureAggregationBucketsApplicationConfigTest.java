package presidio.ade.processes.shell.feature.aggregation.buckets;

import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.processes.shell.feature.aggregation.buckets.config.ModelFeatureAggregationBucketsConfiguration;
import presidio.monitoring.spring.test.PresidioMonitoringTestConfig;

import java.util.Properties;

@Configuration
@Import({MongodbTestConfig.class,PresidioMonitoringTestConfig.class})
public class ModelFeatureAggregationBucketsApplicationConfigTest extends ModelFeatureAggregationBucketsConfiguration{
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
        return new TestPropertiesPlaceholderConfigurer(properties);
    }
}
