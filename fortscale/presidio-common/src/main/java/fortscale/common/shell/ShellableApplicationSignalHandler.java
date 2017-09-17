package fortscale.common.shell;

import fortscale.utils.logging.Logger;
import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.io.IOException;

/**
 * in case of kill [pid] , set exit code to bad (1) and exit
 * Created by barak_schuster on 9/17/17.
 */
public class ShellableApplicationSignalHandler implements SignalHandler {
    private static final Logger logger = Logger.getLogger(ShellableApplicationSignalHandler.class);

    private PresidioShellableApplication shellableApplication;


    public ShellableApplicationSignalHandler(PresidioShellableApplication shellableApplication) {
        this.shellableApplication = shellableApplication;
    }

    @Override
    public void handle(Signal signal) {
        int exitCode = 1;
        logger.info("received SIGTERM, setting exitcode={}",exitCode);
        PresidioShellableApplication.exitCode.compareAndSet(0,exitCode);
        try {
            shellableApplication.close();
        } catch (IOException e) {
            throw new RuntimeException("error while closing shellable application",e);
        }
    }
}
