package presidio.input.core;




import fortscale.common.general.PresidioShellableApplication;
import fortscale.common.shell.config.ShellCommonCommandsConfig;
import fortscale.utils.logging.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import presidio.input.core.spring.InputProductionConfiguration;

@SpringBootApplication
/*@ComponentScan(
        excludeFilters = { //only scan for spring-boot beans
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "fortscale.*"),
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "presidio.*")})*/
public class FortscaleInputCoreApplication extends PresidioShellableApplication {


    private static final Logger logger = Logger.getLogger(FortscaleInputCoreApplication.class);

    public static void main(String[] args) {
        logger.info("Start Input Core Main");

        ConfigurableApplicationContext ctx = SpringApplication.run(new Object[]{FortscaleInputCoreApplication.class, InputProductionConfiguration.class, ShellCommonCommandsConfig.class}, args);
        //todo: all the lines under the configurations should be in the parent class and the logger, else they are duplicated
        ctx.registerShutdownHook();
        run(args, ctx);
        Thread.currentThread().interrupt();
        ctx.close();

    }

}


