package com.rsa.netwitness.presidio.automation.data.processing.airflow;

import ch.qos.logback.classic.Logger;
import com.rsa.netwitness.presidio.automation.ssh.client.SshExecHelper;
import com.rsa.netwitness.presidio.automation.ssh.client.SshResponse;
import com.rsa.netwitness.presidio.automation.ssh.helper.SshHelper;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

import static com.rsa.netwitness.presidio.automation.config.AutomationConf.IS_JENKINS_RUN;
import static com.rsa.netwitness.presidio.automation.config.AutomationConf.TARGET_DIR;
import static java.time.ZoneOffset.UTC;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

public enum AirflowHelper {
    INSTANCE;

    private static Logger LOGGER = (Logger) LoggerFactory.getLogger(AirflowHelper.class);
    private final String airflowStartCmd = "systemctl start airflow-scheduler";
    private final String airflowStopCmd = "systemctl stop airflow-scheduler";
    private final int TIMEOUT_MIN = 5;
    private SshExecHelper runCmdRoot = new SshHelper().uebaHostRootExec();

    public synchronized SshResponse stopAirflowSchedulerAndValidateSuccess() {
        return runCmdRoot.withTimeout(TIMEOUT_MIN, MINUTES).withExitCodeValidation().run(airflowStopCmd);
    }

    public synchronized SshResponse stopAirflowScheduler() {
        return runCmdRoot.withTimeout(TIMEOUT_MIN, MINUTES).run(airflowStopCmd);
    }

    public synchronized SshResponse startAirflowSchedulerAndValidateSuccess() {
        return runCmdRoot.withTimeout(TIMEOUT_MIN, MINUTES).withExitCodeValidation().run(airflowStartCmd);
    }

    public synchronized SshResponse startAirflowScheduler() {
        return runCmdRoot.withTimeout(TIMEOUT_MIN, MINUTES).run(airflowStartCmd);
    }

    public synchronized void publishLogs(String dagId, String taskId, Instant executionDate) {
        if ( !IS_JENKINS_RUN ) return;

        Path sourceDir = Paths.get("/var/log/netwitness/presidio/3p/airflow/logs/", dagId, taskId, toLogPathExecutionDate(executionDate));
        Path dstDir = Paths.get(TARGET_DIR.toString(), "airflow_failures");

        if (sourceDir.toFile().exists()) {
            LOGGER.info("Going to publish: " + sourceDir.toString());
            String CMD = "cp -rf ".concat(sourceDir.toString()).concat(" ").concat(dstDir.toString());
            runCmdRoot.withTimeout(15, SECONDS).run(CMD);
            CMD = "chown -R presidio:presidio ".concat(dstDir.toString());
            runCmdRoot.withTimeout(15, SECONDS).run(CMD);
        } else {
            LOGGER.error("Folder not found: " + sourceDir.toString());
        }
    }

    // expected: 2019-11-24T21:00:00+00:00
    private String toLogPathExecutionDate(Instant executionDate) {
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(UTC).format(executionDate);
    }
}
