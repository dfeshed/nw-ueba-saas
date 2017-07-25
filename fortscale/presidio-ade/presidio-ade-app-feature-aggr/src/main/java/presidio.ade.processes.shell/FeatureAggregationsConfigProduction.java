package presidio.ade.processes.shell;


import fortscale.utils.mongodb.config.MongoConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;


@Configuration
//@EnableSpringConfigured
@Import({
        MongoConfig.class,
        FeatureAggregationsConfig.class
})
public class FeatureAggregationsConfigProduction {

}
