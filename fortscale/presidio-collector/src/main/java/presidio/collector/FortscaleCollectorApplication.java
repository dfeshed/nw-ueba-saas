package presidio.collector;

import fortscale.common.general.PresidioShellableApplication;
import fortscale.common.shell.config.ShellCommonCommandsConfig;
import fortscale.utils.logging.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import presidio.collector.spring.CollectorConfig;

import java.util.Arrays;


@SpringBootApplication
@ComponentScan( //only scan for spring-boot beans
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "fortscale.*"),
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "presidio.*")})
@EnableTask
public class FortscaleCollectorApplication extends PresidioShellableApplication {

    private static final Logger logger = Logger.getLogger(FortscaleCollectorApplication.class);

    public static void main(String[] args) {
        logger.info("starting Collector with params " + Arrays.toString(args));

        ConfigurableApplicationContext ctx = SpringApplication.run(new Object[]{FortscaleCollectorApplication.class, CollectorConfig.class, ShellCommonCommandsConfig.class}, args);
        run(args, ctx);
    }
}
