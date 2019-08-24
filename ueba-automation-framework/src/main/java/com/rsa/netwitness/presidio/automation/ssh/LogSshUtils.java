package com.rsa.netwitness.presidio.automation.ssh;

import com.rsa.netwitness.presidio.automation.ssh.client.SshExecutor;
import com.rsa.netwitness.presidio.automation.ssh.client.SshResponse;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class LogSshUtils {
    private static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger)
            LoggerFactory.getLogger(LogSshUtils.class.getName());

    public static void printLogIfError(String logPath) {
        printLogIfError(logPath, 100);
    }

    public static void printLogIfError(String logPath, int limitLines) {
        SshResponse response = SshExecutor.executeOnUebaHost("tail -n " + limitLines + " " + logPath);

        boolean errorFlag = Objects.requireNonNull(response.output)
                .stream()
                .anyMatch(e -> e.contains(" ERROR "));

        if (errorFlag) {
            LOGGER.warn("'ERROR' messages found in log.");
            LOGGER.warn("***********************************************");
            LOGGER.warn("\t".concat(logPath));
            LOGGER.warn("***********************************************");
            response.output.forEach(output -> LOGGER.warn(output));
        } else {
            LOGGER.debug("\t".concat(logPath));
            response.output.forEach(output -> LOGGER.debug(output));
        }
    }
}
