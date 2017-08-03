package fortscale.common.shell;

import fortscale.common.shell.config.ShellCommonCommandsConfig;
import fortscale.common.shell.config.ShellableApplicationConfig;
import fortscale.utils.logging.Logger;
import fortscale.utils.shell.BootShim;
import fortscale.utils.shell.CommandLineArgsHolder;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.shell.core.ExitShellRequest;

import java.util.Arrays;
import java.util.List;

/**
 * Used for running spring boot application with spring shell operation support
 *
 * Created by efratn on 15/06/2017.
 */
public class PresidioShellableApplication {

    private static final Logger logger = Logger.getLogger(PresidioShellableApplication.class);

    /**
     * Create a default {@link ConfigurableApplicationContext}, that contains the {@link ShellCommonCommandsConfig}
     * and the given configuration class. Run the application with the given input arguments.
     *
     * @param configurationClass where the application's context is configured
     * @param args               the input arguments
     */
    public static void run(List<Class> configurationClass, String[] args) {
        logger.debug("Starting {} component ",configurationClass.getClass().getName());

        CommandLineArgsHolder.args = args;

        configurationClass.add(ShellableApplicationConfig.class);
        ConfigurableApplicationContext context = SpringApplication.run(configurationClass.toArray(), args);
        int exitCode=0;
        try {
            context.registerShutdownHook();
            ExitShellRequest exitShellRequest = run(context);
            exitCode = exitShellRequest.getExitCode();
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to run application with specified args: [%s]", Arrays.toString(args));
            logger.error(errorMessage, e);
            exitCode=1;
        }
        finally {
            context.close();
            Thread.currentThread().interrupt();
            System.exit(exitCode);
        }
    }

    private static ExitShellRequest run(ConfigurableApplicationContext ctx) {
        BootShim bs = ctx.getBean(BootShim.class);
        return bs.run();
    }
}
