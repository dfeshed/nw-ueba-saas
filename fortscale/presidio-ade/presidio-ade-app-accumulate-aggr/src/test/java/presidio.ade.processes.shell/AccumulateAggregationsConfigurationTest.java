package presidio.ade.processes.shell;

import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.processes.shell.config.AccumulateAggregationsConfiguration;

import java.util.Properties;

@Configuration
@Import({MongodbTestConfig.class})
public class AccumulateAggregationsConfigurationTest extends AccumulateAggregationsConfiguration {
    @Bean
    public static TestPropertiesPlaceholderConfigurer accumulateAggregationsApplicationTestProperties() {
        Properties properties = new Properties();
        //        start ASL paths configurations
        properties.put("fortscale.feature.aggregation.bucket.conf.json.file.name", "classpath:config/asl/feature-buckets/feature-aggregation/*.json");
        properties.put("fortscale.aggregation.feature.event.conf.json.file.name", "classpath:config/asl/aggregation-records/feature-aggregation-records/*.json");
        //        end ASL paths configurations
        properties.put("streaming.event.field.type.aggr_event", "aggr_event");
        properties.put("feature.aggregation.pageIterator.pageSize", 1000);
        properties.put("feature.aggregation.pageIterator.maxGroupSize", 100);
        properties.put("spring.application.name", "test-app-name");
        properties.put("presidio.default.ttl.duration", "PT48H");
        properties.put("presidio.default.cleanup.interval", "PT24H");

        return new TestPropertiesPlaceholderConfigurer(properties);
    }

}