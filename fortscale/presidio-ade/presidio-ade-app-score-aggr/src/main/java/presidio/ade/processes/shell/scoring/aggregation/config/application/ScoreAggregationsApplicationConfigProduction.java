package presidio.ade.processes.shell.scoring.aggregation.config.application;

import fortscale.utils.elasticsearch.config.ElasticsearchConfig;
import fortscale.utils.mongodb.config.MongoConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.monitoring.spring.PresidioMonitoringConfiguration;

/**
 * Created by barak_schuster on 7/25/17.
 */
@Configuration
@Import({
//        common application confs
        PresidioMonitoringConfiguration.class,
        ElasticsearchConfig.class,
        MongoConfig.class})
public class ScoreAggregationsApplicationConfigProduction extends ScoreAggregationsApplicationConfig {
}
