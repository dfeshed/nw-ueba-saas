package com.rsa.netwitness.presidio.automation.test_managers;

import ch.qos.logback.classic.Logger;
import com.rsa.netwitness.presidio.automation.ssh.client.SshResponse;
import com.rsa.netwitness.presidio.automation.ssh.helper.SshHelper;
import com.rsa.netwitness.presidio.automation.utils.common.Lazy;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

public class DataProcessingManager {
    private static Logger LOGGER = (Logger) LoggerFactory.getLogger(DataProcessingManager.class);
    private static final String contextFolder = "/home/presidio/";
    private static final String airflowStartCmd = "systemctl start airflow-scheduler";
    private static final String airflowStopCmd = "systemctl stop airflow-scheduler";
    private static final String dataPreparationFinishTimeFile = "automation_data_preparation_finish_time";
    private Lazy<Optional<Instant>> dataPreparationFinishTime = new Lazy<>();

    private Function<String, String> saveCurrentTimeToFileCmd = filename -> "date --utc +%FT%T.%3NZ > ".concat(contextFolder).concat(filename);
    private Function<String, SshResponse> runCmd = CMD -> new SshHelper().uebaHostExec().withTimeout(5, TimeUnit.SECONDS).run(CMD);


    private Function<SshResponse, SshResponse> validate = result -> {
        assertThat(result.exitCode).as("exit code != 0").isEqualTo(0);
        return result;
    };

    public SshResponse stopAirflowSchedulerAndValidateSuccess() {
        return runCmd.andThen(validate).apply(airflowStopCmd);
    }

    public SshResponse stopAirflowScheduler() {
        return runCmd.apply(airflowStopCmd);
    }

    public SshResponse startAirflowSchedulerAndValidateSuccess() {
        return runCmd.andThen(validate).apply(airflowStartCmd);
    }

    public SshResponse startAirflowScheduler() {
        return runCmd.apply(airflowStartCmd);
    }

    public SshResponse saveDataPreparationFinishTime() {
        return saveCurrentTimeToFileAndValidate(dataPreparationFinishTimeFile, e -> e);
    }

    public SshResponse saveDataPreparationFinishTimeValidateSuccess() {
        return saveCurrentTimeToFileAndValidate(dataPreparationFinishTimeFile, validate);
    }

    public  Optional<Instant> geteDataPreparationFinishTime() {
        Optional<Instant> value = dataPreparationFinishTime.getOrCompute(() -> parseInstantFromFile(dataPreparationFinishTimeFile));
        return value.or(() -> parseInstantFromFile(dataPreparationFinishTimeFile));
    }



    private SshResponse saveCurrentTimeToFileAndValidate(String fileName, Function<SshResponse, SshResponse> validate) {
        return runCmd.andThen(validate).compose(saveCurrentTimeToFileCmd).apply(fileName);
    }

    private Optional<Instant> parseInstantFromFile(String fileName) {
        String CMD = "cat ".concat(contextFolder).concat(fileName);
        SshResponse response = runCmd.apply(CMD);

        String ts = response.output.get(0).trim();
        try {
            return Optional.of(Instant.parse(ts));
        } catch (Exception e) {
            LOGGER.error("Instant parsing error from String: [" + ts + "]");
        }

        return Optional.empty();
    }
}
