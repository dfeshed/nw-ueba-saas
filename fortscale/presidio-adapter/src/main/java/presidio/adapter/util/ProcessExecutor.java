package presidio.adapter.util;

import fortscale.utils.logging.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.StringJoiner;


/**
 * This class is a util class the allows its user to execute an external process,
 * and clear the external process' output buffer to avoid a process 'hang'.
 * see - https://stackoverflow.com/questions/16983372/why-does-process-hang-if-the-parent-does-not-consume-stdout-stderr-in-java
 * <p>
 * * not static so it could be mocked
 */
public class ProcessExecutor {

    private static final Logger logger = Logger.getLogger(fortscale.services.impl.ProcessExecutor.class);

    /**
     * @param jobName          the name of the new job
     * @param arguments        the arguments for new external process
     * @param workingDirectory the working directory for the new process
     * @return the newly created {@link Process}
     */
    public int executeProcess(String jobName, List<String> arguments, String workingDirectory) {
        final ProcessBuilder processBuilder = createProcessBuilder(arguments, workingDirectory);
        logger.debug("Starting process with arguments {}", arguments);
        return doExecuteProcess(jobName, processBuilder);
    }

    private ProcessBuilder createProcessBuilder(List<String> arguments) {
        final ProcessBuilder processBuilder = new ProcessBuilder(arguments).redirectErrorStream(true);
        processBuilder.redirectErrorStream(true);
        return processBuilder;
    }

    private ProcessBuilder createProcessBuilder(List<String> arguments, String workingDirectory) {
        final ProcessBuilder processBuilder = createProcessBuilder(arguments);
        processBuilder.directory(new File(workingDirectory));
        return processBuilder;
    }

    private int doExecuteProcess(String jobName, ProcessBuilder processBuilder) {
        final Process process;
        try {
            String command = getCommand(processBuilder);
            logger.info("Running command {}", command);
            process = processBuilder.start();
            final Charset charset = Charset.defaultCharset();
            InvokedProcessOutputReader invokedProcessOutputReader = new InvokedProcessOutputReader(new BufferedReader(new InputStreamReader(process.getInputStream(), charset)), "flume: "+jobName);
            invokedProcessOutputReader.setName("invokedProcessOutputReader-[" + jobName + "]");
            invokedProcessOutputReader.setDaemon(true);
            invokedProcessOutputReader.start();
        } catch (Exception e) {
            logger.error("Failed while running job: " + jobName, e);
            return 1;
        }
        int status = 0;
        try {
            status = process.waitFor();
        } catch (InterruptedException e) {
            logger.error("Execution of job {} has failed. Job has been interrupted", jobName, e);
            if (process.isAlive()) {
                logger.error("Killing the process forcibly!");
                process.destroyForcibly();
            }
            return status;
        }

        return status;
    }


    private String getCommand(ProcessBuilder processBuilder) {
        final List<String> args = processBuilder.command();
        StringJoiner  stringJoiner = new StringJoiner(" ");
        for (String arg : args) {
            stringJoiner.add(arg);
        }
        return stringJoiner.toString();
    }


    /**
     * Class used to clear the invoked process out buffer
     * InvokedProcessOutputReader dies as soon as the input stream is invalid
     */
    private static class InvokedProcessOutputReader extends Thread {
        private BufferedReader input;
        private final String jobName;

        private InvokedProcessOutputReader(BufferedReader input, String jobName) {
            this.input = input;
            this.jobName = jobName;
        }

        @Override
        public void run() {
            try {
                String line;
                while ((line = input.readLine()) != null) {
                    logger.warn("log[{}] = '{}'", jobName, line);
                }
            } catch (IOException e) {
                logger.info("InvokedProcessOutputReader for job {} is exiting...", jobName, e);
            } finally {
                try {
                    if (input != null) {
                        input.close();
                    }
                } catch (IOException ex) {
                    logger.error("Failed to close InvokedProcessOutputReader for job {}", jobName, ex);
                }
            }
        }
    }
}
