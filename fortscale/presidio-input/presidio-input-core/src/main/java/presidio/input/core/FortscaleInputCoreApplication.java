package presidio.input.core;


import fortscale.utils.logging.Logger;
import fortscale.utils.shell.commands.config.ShellCommonCommandsConfig;
import fortscale.utils.shell.service.BootShim;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.shell.support.logging.HandlerUtils;
import presidio.input.core.spring.InputCoreConfiguration;


@SpringBootApplication
@ComponentScan(
        excludeFilters = { //only scan for spring-boot beans
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "fortscale.*"),
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "presidio.*")})
//@EnableTask
public class FortscaleInputCoreApplication {


    private static final Logger logger = Logger.getLogger(FortscaleInputCoreApplication.class);

    public static void main(String[] args) {
        logger.info("Start Input Core Processing");

//        // storing application args into static class so that the ShellService will be able to retrieve them
//        // This must be done with static method since we need this before the spring context is loaded
//        ShellServiceImpl.PresidioExecutionParams.setExecutionCommand(args);
//
//        //run the spring boot application which will trigger the spring shell
//        SpringApplication.run(new Object[]{FortscaleInputCoreApplication.class, InputCoreConfiguration.class, ShellServiceConfig.class}, args);

        ConfigurableApplicationContext ctx = SpringApplication.run(new Object[]{FortscaleInputCoreApplication.class, InputCoreConfiguration.class, ShellCommonCommandsConfig.class}, args);

        try {
            BootShim bs = new BootShim(args, ctx);
            bs.run();
        } catch (RuntimeException e) {
            throw e;
        } finally {
            HandlerUtils.flushAllHandlers(java.util.logging.Logger.getLogger(""));
        }
    }

}


