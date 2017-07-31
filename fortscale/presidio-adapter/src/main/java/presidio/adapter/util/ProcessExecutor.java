package presidio.adapter.util;

import fortscale.utils.logging.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

public class ProcessExecutor {

    private static final Logger logger = Logger.getLogger(fortscale.services.impl.ProcessExecutor.class);

    public static void executeProcess(String jobName, List<String> arguments, String workingDirectory) {
        final ProcessBuilder processBuilder = createProcessBuilder(arguments, workingDirectory);
        logger.debug("Starting process with arguments {}", arguments);
        doExecuteProcess(jobName, processBuilder);
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

    private static void doExecuteProcess(String jobName, ProcessBuilder processBuilder) {
        try {
            final Process process = processBuilder.start();
            final Charset charset = Charset.defaultCharset();
            InvokedProcessOutputReader invokedProcessOutputReader = new InvokedProcessOutputReader(new BufferedReader(new InputStreamReader(process.getInputStream(), charset)), jobName);
            invokedProcessOutputReader.setName("invokedProcessOutputReader-[" + jobName + "]");
            invokedProcessOutputReader.setDaemon(true);
            invokedProcessOutputReader.start();
        } catch (Exception e) {
            logger.error("Failed while running job: " + jobName, e);
        }
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
                    logger.warn("Stderr[{}] = '{}'", jobName, line);
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
