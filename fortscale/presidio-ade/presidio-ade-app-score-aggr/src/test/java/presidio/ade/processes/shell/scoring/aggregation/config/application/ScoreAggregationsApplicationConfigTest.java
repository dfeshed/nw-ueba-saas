package presidio.ade.processes.shell.scoring.aggregation.config.application;

import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Properties;

/**
 * Created by barak_schuster on 7/25/17.
 */
@Configuration
@Import(MongodbTestConfig.class)
public class ScoreAggregationsApplicationConfigTest extends ScoreAggregationsApplicationConfig {
    @Bean
    public static TestPropertiesPlaceholderConfigurer scoreAggregationsApplicationTestProperties() {
        Properties properties = new Properties();
        //        start ASL paths configurations
        properties.put("fortscale.model.aggregation.bucket.conf.json.file.name", "classpath:config/asl/feature-buckets/models/enriched-records/*.json");
        properties.put("fortscale.score.aggregation.bucket.conf.json.file.name", "classpath:config/asl/feature-buckets/score-aggregation/*.json");
        properties.put("fortscale.aggregation.feature.event.conf.json.file.name", "classpath:config/asl/aggregation-records/score-aggregation-records/*.json");
        properties.put("presidio.modeling.base.configurations.path", "classpath:config/asl/models/enriched-records/*.json");
        properties.put("fortscale.scorer.configurations.location.path", "classpath:config/asl/scorers/enriched-records/*.json");
        //        end ASL paths configurations

        properties.put("fortscale.model.cache.futureDiffBetweenCachedModelAndEvent", "PT48H");
        properties.put("fortscale.model.cache.size", 100);
        properties.put("spring.application.name", "test-app-name");
        properties.put("presidio.default.ttl.duration", "PT48H");
        properties.put("presidio.default.cleanup.interval", "PT24H");
        return new TestPropertiesPlaceholderConfigurer(properties);
    }
}
