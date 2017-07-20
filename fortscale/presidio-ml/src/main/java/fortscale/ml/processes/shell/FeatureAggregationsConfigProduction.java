package fortscale.ml.processes.shell;


import fortscale.common.shell.PresidioExecutionService;
import fortscale.utils.mongodb.config.MongoConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;


@Configuration
@EnableSpringConfigured
@Import({
        MongoConfig.class,
        FeatureAggregationsConfig.class
})
public class FeatureAggregationsConfigProduction {

}
