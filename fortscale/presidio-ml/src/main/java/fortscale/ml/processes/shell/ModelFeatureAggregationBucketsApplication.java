package fortscale.ml.processes.shell;

import fortscale.utils.logging.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;

/**
 * Created by YaronDL on 7/2/2017.
 */
@SpringBootApplication
@EnableTask
@EnableSpringConfigured
@EnableAspectJAutoProxy
public class ModelFeatureAggregationBucketsApplication {
    private static final Logger logger = Logger.getLogger(ModelFeatureAggregationBucketsApplication.class);


    public static void main(String[] args) {
        logger.info("Start application: {}", ModelFeatureAggregationBucketsApplication.class);

        ConfigurableApplicationContext ctx = SpringApplication.run(new Object[]{ModelFeatureAggregationBucketsApplication.class,
                ModelFeatureAggregationBucketsServiceConfiguration.class}, args);
    }
}
