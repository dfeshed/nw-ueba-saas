package presidio.input.core;



import fortscale.common.general.PresidioShellableApplication;
import fortscale.utils.logging.Logger;
import fortscale.common.shell.config.ShellCommonCommandsConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import presidio.input.core.spring.InputCoreConfiguration;


@SpringBootApplication
@ComponentScan(
        excludeFilters = { //only scan for spring-boot beans
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "fortscale.*"),
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "presidio.*")})
public class FortscaleInputCoreApplication extends PresidioShellableApplication {


    private static final Logger logger = Logger.getLogger(FortscaleInputCoreApplication.class);

    public static void main(String[] args) {
        logger.info("Start Input Core Main");

        ConfigurableApplicationContext ctx = SpringApplication.run(new Object[]{FortscaleInputCoreApplication.class, InputCoreConfiguration.class, ShellCommonCommandsConfig.class}, args);
        run(args, ctx);
    }

}


