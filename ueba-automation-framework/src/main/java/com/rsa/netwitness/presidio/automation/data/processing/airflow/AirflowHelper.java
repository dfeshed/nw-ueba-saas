package com.rsa.netwitness.presidio.automation.data.processing.airflow;

import ch.qos.logback.classic.Logger;
import com.rsa.netwitness.presidio.automation.ssh.client.SshExecHelper;
import com.rsa.netwitness.presidio.automation.ssh.client.SshResponse;
import com.rsa.netwitness.presidio.automation.ssh.helper.SshHelper;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

import static com.rsa.netwitness.presidio.automation.config.AutomationConf.TARGET_DIR;
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

    public synchronized void copyLogs(String dag_id, String task_id, Instant execution_date) {
        Path sourceDir = Paths.get("/var/log/netwitness/presidio/3p/airflow/logs/", dag_id, task_id, execution_date.toString());
        Path dstDir = Paths.get(TARGET_DIR.toString(), "airflow_failures");

        String CMD = "cp -rf ".concat(sourceDir.toString()).concat(" ").concat(dstDir.toString());
        runCmdRoot.withTimeout(15, SECONDS).run(CMD);

        CMD = "chown -R presidio:presidio ".concat(dstDir.toString());
        runCmdRoot.withTimeout(15, SECONDS).run(CMD);
    }
}
