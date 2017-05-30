package presidio.input.sdk.impl;


import fortscale.utils.mongodb.config.MongoConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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


    private static Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        log.info("shay");
        SpringApplication.run(new Object[]{Application.class, PresidioInputPersistencyServiceConfig.class, MongoConfig.class}, args);
    }


}
