package fortscale.common.general;

import fortscale.utils.logging.Logger;
import fortscale.utils.shell.BootShim;
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
            BootShim bs = new BootShim(args, ctx);
            bs.run();
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to run application with specified args: [%s] due to %s", Arrays.toString(args), e.getMessage());
            logger.error(errorMessage, e);
        }
    }

}
