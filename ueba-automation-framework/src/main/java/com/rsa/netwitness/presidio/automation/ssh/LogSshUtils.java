package com.rsa.netwitness.presidio.automation.ssh;

import com.rsa.netwitness.presidio.automation.ssh.client.SshExecutor;
import com.rsa.netwitness.presidio.automation.ssh.client.SshResponse;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class LogSshUtils {
    private static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger)
            LoggerFactory.getLogger(LogSshUtils.class.getName());

    public static void printLogFile(String logPath) {
        SshResponse response = SshExecutor.executeOnUebaHost("tail -n 50 " + logPath);

        boolean errorFlag = Objects.requireNonNull(response.output)
                .stream()
                .anyMatch(e -> e.contains(" ERROR "));

        if (errorFlag) {
            LOGGER.warn("'ERROR' messages found in log.");
            LOGGER.info("***********************************************");
            LOGGER.info("\t".concat(logPath));
            LOGGER.info("***********************************************");
            response.output.forEach(output -> LOGGER.error(output));
        } else {
            LOGGER.debug("\t".concat(logPath));
            response.output.forEach(output -> LOGGER.debug(output));
        }
    }
}
