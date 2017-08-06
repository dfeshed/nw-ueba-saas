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
        properties.put("fortscale.model.aggregation.bucket.conf.json.file.name", "classpath:config/asl/score-aggregation/feature-buckets/model/*.json");
        properties.put("fortscale.score.aggregation.bucket.conf.json.file.name", "classpath:config/asl/score-aggregation/feature-buckets/score_aggregation/*.json");
        properties.put("fortscale.aggregation.feature.event.conf.json.file.name", "classpath:config/asl/score-aggregation/aggregated-features/*.json");
        properties.put("presidio.modeling.base.configurations.path", "classpath:config/asl/score-aggregation/models/*.json");
        properties.put("fortscale.scorer.configurations.location.path", "classpath:config/asl/score-aggregation/scorers/*.json");
        //        end ASL paths configurations

        properties.put("fortscale.model.cache.futureDiffBetweenCachedModelAndEvent", "PT48H");
        properties.put("fortscale.model.cache.size", 100);

        return new TestPropertiesPlaceholderConfigurer(properties);
    }
}
