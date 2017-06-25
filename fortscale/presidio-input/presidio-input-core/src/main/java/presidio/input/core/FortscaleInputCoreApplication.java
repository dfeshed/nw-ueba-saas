package presidio.input.core;


import fortscale.utils.logging.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;


@SpringBootApplication
@ComponentScan(
        excludeFilters = { //only scan for spring-boot beans
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "fortscale.*"),
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "presidio.*")})
@EnableTask
public class FortscaleInputCoreApplication {
    private static final Logger logger = Logger.getLogger(FortscaleInputCoreApplication.class);

    public static void main(String[] args) {
        logger.info("Start Input Core Processing");
        SpringApplication.run(new Object[]{FortscaleInputCoreApplication.class, InputCommandLineRunnerConfiguration.class},
                args);
    }
}
