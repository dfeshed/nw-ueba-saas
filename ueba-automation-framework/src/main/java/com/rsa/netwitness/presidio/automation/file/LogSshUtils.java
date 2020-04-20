package com.rsa.netwitness.presidio.automation.file;

import com.rsa.netwitness.presidio.automation.ssh.client.SshResponse;
import com.rsa.netwitness.presidio.automation.ssh.helper.SshHelper;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static com.rsa.netwitness.presidio.automation.config.AutomationConf.IS_JENKINS_RUN;
import static com.rsa.netwitness.presidio.automation.config.AutomationConf.TARGET_DIR;

public class LogSshUtils {
    private static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger)
            LoggerFactory.getLogger(LogSshUtils.class.getName());

    public static void printLogIfError(String logPath) {
        printLogIfError(logPath, 100);
    }

    public static void printLogIfError(String logPath, int limitLines) {
        SshResponse response = new SshHelper().uebaHostExec().run("tail -n " + limitLines + " " + logPath);

        boolean errorFlag = Objects.requireNonNull(response.output)
                .stream()
                .anyMatch(e -> e.contains(" ERROR ") || e.contains("Exception:"));

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

        copyToJenkinsTarget(logPath);
    }

    public static void copyToJenkinsTarget(String path) {
        if (IS_JENKINS_RUN) {
            Path targetPath = Paths.get(TARGET_DIR.toString(), "processing");
            String mkdirCmd = "mkdir -p ".concat(targetPath.toString());
            String copyCmd = "cp -f ".concat(path).concat(" ").concat(targetPath.toString());
            SshHelper ssh = new SshHelper();
            ssh.uebaHostExec().run(mkdirCmd + " ; " + copyCmd);
        }
    }
}
