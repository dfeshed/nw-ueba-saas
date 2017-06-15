package fortscale.utils.shell.service;

import fortscale.utils.logging.Logger;
import org.springframework.shell.core.CommandResult;
import org.springframework.shell.core.JLineShellComponent;

/**
 * extension to spring's JLineShellComponent
 * purpose: shell class with bulletproof logging
 * Created by barak_schuster on 8/1/16.
 */
public class FortscaleJLineShellComponent extends JLineShellComponent {
    private static final Logger logger = Logger.getLogger(FortscaleJLineShellComponent.class);

    /**
     * executes command line
     *
     * @param line - line to execute
     * @return command result containing execution successFlag, exception and result data
     */
    @Override
    public CommandResult executeCommand(String line) {
        // log executed command
        logger.info("executing command = {}", line);

        // execute command
        CommandResult commandResult = super.executeCommand(line);

        // get command result
        Object result = commandResult.getResult();

        String outputStr = "";

        if (result != null) {
            // log output as string
            if (result instanceof String) {
                outputStr = (String) result;
            }
            // log output as json
            else {
                try {
//                    outputStr = FortscaleCommandMarker.objectToDateFormattedJsonString(result);
                    outputStr = (String) result;
                } catch (Exception e) {

                    logger.error("command={} output parsing error", line, e);
                }
            }
        }

        boolean isCadExecutedSuccessfully = commandResult.isSuccess();
        String msg = String.format("command=%s isSuccess=%s result=%s", line, isCadExecutedSuccessfully, outputStr);
        Throwable cmdException = commandResult.getException();

        // exception happened during command execution
        if (cmdException == null) {
            logger.debug(msg);
        } else {
            logger.error(msg, cmdException);
        }

        return commandResult;
    }


}
