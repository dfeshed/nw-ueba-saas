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

    private static void run(ConfigurableApplicationContext ctx) {
        BootShim bs = ctx.getBean(BootShim.class);
        bs.run();
    }

    /**
     * Create a default {@link ConfigurableApplicationContext}, that contains the {@link ShellCommonCommandsConfig}
     * and the given configuration class. Run the application with the given input arguments.
     *
     * @param configurationClass where the application's context is configured
     * @param args               the input arguments
     */
    public static void run(Object[] configurationClass, String[] args) {
        logger.info("Starting {} component ",configurationClass.getClass().getName());
        CommandLineArgsHolder.args = args;
        Object[] sources= new Object[configurationClass.length+2];
        for(int i=0;i<configurationClass.length;i++){
            sources[i]=configurationClass[i];
        }
        sources[configurationClass.length]=ShellCommonCommandsConfig.class;
        sources[configurationClass.length+1]=BootShimConfig.class;
        ConfigurableApplicationContext context = SpringApplication.run(sources, args);
        int exitCode=0;
        try {
            context.registerShutdownHook();
            run(context);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to run application with specified args: [%s]", Arrays.toString(args));
            logger.error(errorMessage, e);
            exitCode=1;
        }
        finally {
            if (exitCode!=1) {
                BootShim bs = context.getBean(BootShim.class);
                exitCode = bs.getShell().getExitShellRequest().getExitCode();
            }
            Thread.currentThread().interrupt();
            context.close();
            System.exit(exitCode);
        }
    }
}
