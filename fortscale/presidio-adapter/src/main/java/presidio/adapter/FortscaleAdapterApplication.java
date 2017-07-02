package presidio.adapter;

import fortscale.utils.logging.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import presidio.adapter.spring.AdapterConfigProduction;

import java.util.Arrays;


@SpringBootApplication
@ComponentScan( //only scan for spring-boot beans
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "fortscale.*"),
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "presidio.*")})
@EnableTask
public class FortscaleAdapterApplication {
    private static final Logger logger = Logger.getLogger(FortscaleAdapterApplication.class);

    public static void main(String[] args) {
        logger.info("starting Adapter with params " + Arrays.toString(args));

        SpringApplication.run(new Object[]{FortscaleAdapterApplication.class, AdapterConfigProduction.class}, args);
    }
}
