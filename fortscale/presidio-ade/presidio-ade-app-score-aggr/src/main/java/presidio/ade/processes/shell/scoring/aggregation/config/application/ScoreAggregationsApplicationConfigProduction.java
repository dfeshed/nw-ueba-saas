package presidio.ade.processes.shell.scoring.aggregation.config.application;

import fortscale.utils.mongodb.config.MongoConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by barak_schuster on 7/25/17.
 */
@Configuration
@Import({
//        common application confs

        MongoConfig.class})
public class ScoreAggregationsApplicationConfigProduction extends ScoreAggregationsApplicationConfig {
}
