package presidio.ade.processes.shell;


import fortscale.utils.mongodb.config.MongoConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration
@Import({
        MongoConfig.class,
})
public class FeatureAggregationsConfigurationProduction extends FeatureAggregationsConfiguration {

}
