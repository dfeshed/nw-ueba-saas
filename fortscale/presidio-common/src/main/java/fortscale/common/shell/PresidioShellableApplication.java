package fortscale.common.shell;

import fortscale.common.shell.command.FSBannerProvider;
import fortscale.common.shell.command.FSHistoryFileNameProvider;
import fortscale.common.shell.command.FSPromptProvider;
import fortscale.utils.logging.Logger;
import fortscale.utils.shell.BootShim;
import fortscale.utils.shell.BootShimConfig;
import fortscale.utils.shell.CommandLineArgsHolder;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.shell.core.ExitShellRequest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Used for running spring boot application with spring shell operation support
 *
 * Created by efratn on 15/06/2017.
 */
public class PresidioShellableApplication {

    private static final Logger logger = Logger.getLogger(PresidioShellableApplication.class);

    /**
     * Create a default {@link ConfigurableApplicationContext}, that contains the {@link BootShimConfig}
     * and the given configuration class. Run the application with the given input arguments.
     *
     * @param configurationClass where the application's context is configured
     * @param args               the input arguments
     */
    public static void run(List<Class> configurationClass, String[] args) {
        if(logger.isDebugEnabled()) {
            String configurationClasses = configurationClass.stream().map(Class::getSimpleName).collect(Collectors.joining(","));
            logger.debug("Starting {} component ", configurationClasses);
        }
        CommandLineArgsHolder.args = args;

        //Required beans for the shell
        configurationClass.add(FSBannerProvider.class);
        configurationClass.add(FSHistoryFileNameProvider.class);
        configurationClass.add(FSPromptProvider.class);
        configurationClass.add(BootShimConfig.class);
        ConfigurableApplicationContext context = null;
        int exitCode=0;
        try {
            context = SpringApplication.run(configurationClass.toArray(), args);
            context.registerShutdownHook();
            ExitShellRequest exitShellRequest = run(context);
            exitCode = exitShellRequest.getExitCode();
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to run application with specified args: [%s]", Arrays.toString(args));
            logger.error(errorMessage, e);
            exitCode=1;
        }
        finally {
            if (context!=null) {
                context.close();
            }
            Thread.currentThread().interrupt();
            System.exit(exitCode);
        }
    }

    private static ExitShellRequest run(ConfigurableApplicationContext ctx) {
        BootShim bs = ctx.getBean(BootShim.class);
        return bs.run();
    }
}
