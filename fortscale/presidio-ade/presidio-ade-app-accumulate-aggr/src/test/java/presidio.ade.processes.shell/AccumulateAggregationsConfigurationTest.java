package presidio.ade.processes.shell;

import fortscale.common.shell.command.PresidioCommands;
import fortscale.utils.shell.BootShimConfig;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.processes.shell.config.AccumulateAggregationsConfiguration;

import java.util.Properties;

/**
 * Created by barak_schuster on 7/30/17.
 */
@Configuration
@Import({MongodbTestConfig.class,
        PresidioCommands.class,
        BootShimConfig.class})
public class AccumulateAggregationsConfigurationTest extends AccumulateAggregationsConfiguration {
    @Bean
    public static TestPropertiesPlaceholderConfigurer accumulateAggregationsApplicationTestProperties() {
        Properties properties = new Properties();
        //        start ASL paths configurations
        properties.put("fortscale.feature.aggregation.bucket.conf.json.file.name", "classpath:config/asl/feature-aggregation/feature-buckets/feature_aggregation_buckets.json");
        properties.put("fortscale.aggregation.feature.event.conf.json.file.name", "classpath:config/asl/feature-aggregation/aggregated-features/*.json");
        //        end ASL paths configurations
        properties.put("streaming.event.field.type.aggr_event","aggr_event");
        properties.put("feature.aggregation.pageSize",1000);
        properties.put("feature.aggregation.maxGroupSize",100);

        return new TestPropertiesPlaceholderConfigurer(properties);
    }


}