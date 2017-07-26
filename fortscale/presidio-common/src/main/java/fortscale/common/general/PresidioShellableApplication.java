package fortscale.common.general;

import fortscale.common.shell.config.ShellCommonCommandsConfig;
import fortscale.utils.logging.Logger;
import fortscale.utils.shell.BootShim;
import fortscale.utils.shell.BootShimConfig;
import fortscale.utils.shell.CommandLineArgsHolder;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Arrays;

/**
 * Abstract class for running spring boot application with spring shell operation support
 *
 * Created by efratn on 15/06/2017.
 */
public abstract class PresidioShellableApplication {
    private static final Logger logger = Logger.getLogger(PresidioShellableApplication.class);

    public static void run(String[] args, ConfigurableApplicationContext ctx) {
        try {
            BootShim bs = ctx.getBean(BootShim.class);
            bs.run();
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to run application with specified args: [%s]", Arrays.toString(args));
            logger.error(errorMessage, e);
        }
    }

    /**
     * Create a default {@link ConfigurableApplicationContext}, that contains the {@link ShellCommonCommandsConfig}
     * and the given configuration class. Run the application with the given input arguments.
     *
     * @param configurationClass where the application's context is configured
     * @param args               the input arguments
     */
    public static void run(Class<?> configurationClass, String[] args) {
        Object[] sources = {configurationClass, BootShimConfig.class,ShellCommonCommandsConfig.class};
        ConfigurableApplicationContext context = SpringApplication.run(sources, args);
        CommandLineArgsHolder.args = args;
        run(args, context);
    }
}
