package presidio.ade.processes.shell.feature.aggregation.buckets;

import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.processes.shell.feature.aggregation.buckets.config.ModelFeatureAggregationBucketsConfiguration;

import java.util.Properties;

@Configuration
@Import(MongodbTestConfig.class)
public class ModelFeatureAggregationBucketsApplicationConfigTest extends ModelFeatureAggregationBucketsConfiguration{
    @Bean
    public static TestPropertiesPlaceholderConfigurer modelFeatureAggregationBucketsApplicationTestProperties() {
        Properties properties = new Properties();
        //        start ASL paths configurations
        properties.put("fortscale.model.aggregation.bucket.conf.json.file.name", "classpath:config/asl/model-aggregation/feature-buckets/*.json");

        //        end ASL paths configurations

        properties.put("fortscale.model.cache.futureDiffBetweenCachedModelAndEvent", "PT48H");
        properties.put("fortscale.model.cache.size", 100);

        return new TestPropertiesPlaceholderConfigurer(properties);
    }
}
