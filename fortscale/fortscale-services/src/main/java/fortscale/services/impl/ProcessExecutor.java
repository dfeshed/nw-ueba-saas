package fortscale.services.impl;

import fortscale.utils.logging.Logger;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class ProcessExecutor {

    private static final Logger logger = Logger.getLogger(ProcessExecutor.class);

    public static boolean executeProcess(String jobName, List<String> arguments, String workingDirectory) {
        final ProcessBuilder processBuilder = createProcessBuilder(arguments, workingDirectory);
        return doExecuteProcess(jobName, arguments, processBuilder);
    }

    public static boolean executeProcess(String jobName, List<String> arguments) {
        final ProcessBuilder processBuilder = createProcessBuilder(arguments);
        return doExecuteProcess(jobName, arguments, processBuilder);
    }

    private static ProcessBuilder createProcessBuilder(List<String> arguments) {
        final ProcessBuilder processBuilder = new ProcessBuilder(arguments).redirectErrorStream(true);
        processBuilder.redirectErrorStream(true);
        return processBuilder;
    }

    private static ProcessBuilder createProcessBuilder(List<String> arguments, String workingDirectory) {
        final ProcessBuilder processBuilder = createProcessBuilder(arguments);
        processBuilder.directory(new File(workingDirectory));
        return processBuilder;
    }

    private static boolean doExecuteProcess(String jobName, List<String> arguments, ProcessBuilder processBuilder) {
        Process process;
        try {
            logger.debug("Starting process with arguments {}", arguments);
            process = processBuilder.start();
        } catch (IOException e) {
            logger.error("Execution of task {} has failed.", jobName, e);
            return false;
        }
        int status;
        try {
            status = process.waitFor();
        } catch (InterruptedException e) {
            if (process.isAlive()) {
                logger.error("Killing the process forcibly");
                process.destroyForcibly();
            }
            logger.error("Execution of task {} has failed. Task has been interrupted", jobName, e);
            return false;
        }

        if (status != 0) {
            try {
                String processOutput = IOUtils.toString(process.getInputStream());
                final int length = processOutput.length();
                if (length > 1000) {
                    processOutput = processOutput.substring(length - 1000, length); // getting last 1000 chars to not overload the log file
                }
                logger.error("Error stream for job {} = \n{}", jobName, processOutput);
            } catch (IOException e) {
                logger.warn("Failed to get error stream from process for job {}", jobName);
            }
            logger.error("Execution of task {} has finished with status {}. Execution failed", jobName, status);
            return false;
        }

        logger.debug("Execution of task {} has finished with status {}", jobName, status);
        return true;
    }
}
