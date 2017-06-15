package fortscale.utils.shell.service;

import fortscale.utils.logging.Logger;
import fortscale.utils.process.processInfo.ProcessInfoServiceImpl;
import org.springframework.shell.core.ExitShellRequest;
import org.springframework.shell.core.JLineShellComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ShellServiceImpl implements ShellService {
    private static final Logger logger = Logger.getLogger(ShellServiceImpl.class);
    private JLineShellComponent shell;
    private Thread thread;
    private List<String> commandsToExecute;

    /**
     * C'tor
     *
     * @param shell
     */
    public ShellServiceImpl(JLineShellComponent shell, boolean shellThreadDisabled, List<String> commandsToExecute) {
        this.shell = shell;
        this.commandsToExecute = commandsToExecute;
//        runShell();
        if (!shellThreadDisabled) {
            logger.info("starting new shell thread");
            thread = new Thread(this::runShell);
            thread.setName(ShellServiceImpl.class.getName());
            thread.start();
        }
    }

    /**
     * getter
     *
     * @return shell
     */
    public JLineShellComponent getShell() {
        return shell;
    }

    /**
     * Runs interactive shell till exit request
     */
    @Override
    public void runShell() {

        ExitShellRequest exitShellRequest;

        logger.info("starting Shell");
        try {
            shell.start();
            shell.promptLoop();

            if (!commandsToExecute.isEmpty()) {
                commandsToExecute.stream().forEach(command ->
                {
                    logger.info("executing command={}", command);
                    shell.executeCommand(command);
                });
                shell.executeCommand("exit");
            } else {
                shell.waitForComplete();
            }

        } catch (Exception e) {
            logger.error("unexpected exception while running shell", e);
        }
        exitShellRequest = shell.getExitShellRequest();
        int exitCode = exitShellRequest.getExitCode();
        shell.stop();

        logger.info("shell got closed with exit code={}", exitCode);
        ProcessInfoServiceImpl.exit(exitCode);
    }


    public static class PresidioExecutionParams {

        private static List<String> commandsToExecute = new ArrayList<>();

        public static List<String> getExecutionCommands() {
            return commandsToExecute;
        }

        public static void setExecutionCommand(String[] args) {
//            commandsToExecute = Arrays.asList(args);


            commandsToExecute.add(Arrays.toString(args).replaceAll(",", ""));
        }
    }
}



