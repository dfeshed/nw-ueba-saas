package presidio.ade.processes.shell;

import fortscale.utils.mongodb.config.MongoConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by maria_dorohin on 7/26/17.
 */
@Configuration
@Import({
        MongoConfig.class,
        AccumulateAggregationsConfig.class
})
public class AccumulateAggregationsConfigProduction {

}

