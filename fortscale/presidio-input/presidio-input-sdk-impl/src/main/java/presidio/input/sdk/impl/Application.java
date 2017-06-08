package presidio.input.sdk.impl;


import fortscale.utils.logging.Logger;
import fortscale.utils.mongodb.config.MongoConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import presidio.input.sdk.impl.spring.PresidioInputPersistencyServiceConfig;


@SpringBootApplication
@ComponentScan(
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "fortscale.*"),
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "presidio.*")})
@EnableTask
public class Application {


    private static final Logger logger = Logger.getLogger(Application.class);

    public static void main(String[] args) {
        logger.info("Starting input servcie");
        SpringApplication.run(new Object[]{Application.class, PresidioInputPersistencyServiceConfig.class, MongoConfig.class}, args);
    }


}
