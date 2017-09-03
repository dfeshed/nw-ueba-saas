package presidio.ade.processes.shell;

import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Properties;

/**
 * Created by barak_schuster on 7/30/17.
 */
@Configuration
@Import({MongodbTestConfig.class})
public class FeatureAggregationsConfigurationTest extends FeatureAggregationsConfiguration {
    @Bean
    public static TestPropertiesPlaceholderConfigurer featureAggregationsApplicationTestProperties() {
        Properties properties = new Properties();
        //        start ASL paths configurations
        properties.put("fortscale.model.aggregation.bucket.conf.json.file.name", "classpath:config/asl/feature-buckets/empty-buckets/*.json");
        properties.put("fortscale.feature.aggregation.bucket.conf.json.file.name", "classpath:config/asl/feature-buckets/feature-aggregation/*.json");
        properties.put("fortscale.aggregation.feature.event.conf.json.file.name", "classpath:config/asl/aggregation-records/feature-aggregation-records/*.json");
        properties.put("presidio.modeling.base.configurations.path", "classpath:config/asl/models/feature-aggregation-records/*.json");
        properties.put("fortscale.scorer.configurations.location.path", "classpath:config/asl/scorers/feature-aggregation-records/*.json");
        //        end ASL paths configurations
        properties.put("fortscale.model.cache.futureDiffBetweenCachedModelAndEvent", "PT48H");
        properties.put("fortscale.model.cache.size", 100);
        properties.put("streaming.event.field.type.aggr_event","aggr_event");
        properties.put("feature.aggregation.pageIterator.pageSize",1000);
        properties.put("feature.aggregation.pageIterator.maxGroupSize",100);
        properties.put("presidio.application.name", "test-app-name");
        properties.put("presidio.default.ttl.duration", "PT48H");
        properties.put("presidio.default.cleanup.interval", "PT24H");
        return new TestPropertiesPlaceholderConfigurer(properties);
    }


}