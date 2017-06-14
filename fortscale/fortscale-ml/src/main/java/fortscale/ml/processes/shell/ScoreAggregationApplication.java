package fortscale.ml.processes.shell;

import fortscale.utils.logging.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * Created by barak_schuster on 6/14/17.
 */
@SpringBootApplication
@ComponentScan(
        excludeFilters = { //only scan for spring-boot beans
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "fortscale.*"),
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "presidio.*")})
@EnableTask
public class ScoreAggregationApplication {
    private static final Logger logger = Logger.getLogger(ScoreAggregationApplication.class);

    public static void main(String[] args) {
        logger.info("Start application: {}",ScoreAggregationApplication.class);
        ConfigurableApplicationContext ctx = SpringApplication.run(new Object[]{ScoreAggregationApplication.class,
                ScoreAggregationServiceConfiguration.class}, args);
    }
}

