package com.rsa.netwitness.presidio.automation.data.processing;

import ch.qos.logback.classic.Logger;
import com.rsa.netwitness.presidio.automation.ssh.client.SshExecHelper;
import com.rsa.netwitness.presidio.automation.ssh.client.SshResponse;
import com.rsa.netwitness.presidio.automation.ssh.helper.SshHelper;
import com.rsa.netwitness.presidio.automation.utils.common.Lazy;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Optional;
import java.util.function.Function;

import static java.util.concurrent.TimeUnit.SECONDS;

public enum DataProcessingHelper {
    INSTANCE;

    private static Logger LOGGER = (Logger) LoggerFactory.getLogger(DataProcessingHelper.class);
    private final String contextFolder = "/home/presidio/";
    private Function<String, String> saveCurrentTimeToFileCmd = filename -> "date --utc +%FT%T.%3NZ > ".concat(contextFolder).concat(filename);
    private final String dataPreparationFinishTimeFile = "automation_data_preparation_finish_time";
    private final int TIMEOUT_SEC = 10;
    private SshExecHelper runCmdPresidio = new SshHelper().uebaHostExec();
    private Lazy<Optional<Instant>> dataPreparationFinishTime = new Lazy<>();

    public synchronized SshResponse saveDataPreparationFinishTime() {
        return runCmdPresidio.withTimeout(TIMEOUT_SEC, SECONDS).run(saveCurrentTimeToFileCmd.apply(dataPreparationFinishTimeFile));
    }

    public synchronized SshResponse saveDataPreparationFinishTimeValidateSuccess() {
        return runCmdPresidio.withTimeout(TIMEOUT_SEC, SECONDS).withExitCodeValidation().run(saveCurrentTimeToFileCmd.apply(dataPreparationFinishTimeFile));
    }

    public synchronized Optional<Instant> getDataPreparationFinishTime() {
        Optional<Instant> value = dataPreparationFinishTime.getOrCompute(() -> parseInstantFromFile(dataPreparationFinishTimeFile));
        return value.or(() -> parseInstantFromFile(dataPreparationFinishTimeFile));
    }



    private Optional<Instant> parseInstantFromFile(String fileName) {
        String CMD = "cat ".concat(contextFolder).concat(fileName);
        SshResponse response = runCmdPresidio.withTimeout(TIMEOUT_SEC, SECONDS).run(CMD);

        String ts = response.output.get(0).trim();
        try {
            return Optional.of(Instant.parse(ts));
        } catch (Exception e) {
            LOGGER.error("Instant parsing error from String: [" + ts + "]");
        }

        return Optional.empty();
    }
}
