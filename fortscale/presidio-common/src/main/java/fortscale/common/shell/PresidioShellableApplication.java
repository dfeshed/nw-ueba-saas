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
import sun.misc.Signal;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Used for running spring boot application with spring shell operation support
 *
 * Created by efratn on 15/06/2017.
 */
public class PresidioShellableApplication implements Closeable {

    private static final Logger logger = Logger.getLogger(PresidioShellableApplication.class);
    static AtomicInteger exitCode;
    private ConfigurableApplicationContext context;

    public PresidioShellableApplication() {
        exitCode = new AtomicInteger(0);
        this.context = null;
        initSigtermHandler(this);
    }

    /**
     * @see ShellableApplicationSignalHandler
     */
    private void initSigtermHandler(PresidioShellableApplication presidioShellableApplication) {
        Signal.handle(new Signal("TERM"), new ShellableApplicationSignalHandler(presidioShellableApplication));
        Signal.handle(new Signal("INT"), new ShellableApplicationSignalHandler(presidioShellableApplication));
    }

    /**
     * Create a default {@link ConfigurableApplicationContext}, that contains the {@link BootShimConfig}
     * and the given configuration class. Run the application with the given input arguments.
     *
     * @param configurationClass where the application's context is configured
     * @param args               the input arguments
     */
    public void run(List<Class> configurationClass, String[] args) {
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
        ExitShellRequest exitShellRequest = null;
        try {
            context = SpringApplication.run(configurationClass.toArray(), args);
            context.registerShutdownHook();
            exitShellRequest = run();
            exitCode.set(exitShellRequest.getExitCode());
        } catch (Throwable e) {
            String errorMessage = String.format("Failed to run application with specified args: [%s]", Arrays.toString(args));
            logger.error(errorMessage, e);
            if (exitShellRequest == null) {
                exitCode.compareAndSet(0,1);
            } else {
                exitCode.set(exitShellRequest.getExitCode());
                exitCode.compareAndSet(0,1);
            }
        }
        finally {
            try {
                close();
            } catch (IOException e) {
                throw new RuntimeException("error while closing system",e);
            }
        }
    }

    private ExitShellRequest run() {
        BootShim bs = context.getBean(BootShim.class);
        return bs.run();
    }

    @Override
    public void close() throws IOException {
        if (context!=null) {
            context.close();
        }

        int exitCodeNumber = exitCode.get();
        logger.info("system finished with exit code={}", exitCodeNumber);
        Thread.currentThread().interrupt();
        System.exit(exitCodeNumber);
    }
}
