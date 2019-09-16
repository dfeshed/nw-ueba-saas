package com.rsa.netwitness.presidio.automation.test_managers;

import com.rsa.netwitness.presidio.automation.domain.config.Consts;
import com.rsa.netwitness.presidio.automation.ssh.TerminalCommandsSshUtils;
import com.rsa.netwitness.presidio.automation.ssh.client.SshResponse;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.concurrent.Callable;

import static com.rsa.netwitness.presidio.automation.file.LogSshUtils.printLogIfError;
import static org.assertj.core.api.Assertions.assertThat;

public class OutputDataProcessingManager {
    static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger)
            LoggerFactory.getLogger(OutputDataProcessingManager.class.getName());


    public ProcessorRun processorRun(Instant startDate, Instant endDate, String smartRecordConfName) {
        return new ProcessorRun(startDate, endDate, smartRecordConfName);
    }

    public RecalculateUserScore recalculateUserScore(Instant startDate, Instant endDate, String entity) {
        return new RecalculateUserScore(startDate, endDate, entity);
    }


    public class ProcessorRun implements Callable<Integer> {
        private final Instant startDate;
        private final Instant endDate;
        private final String smart_record_conf_name;

        private ProcessorRun(Instant startDate, Instant endDate, String smartRecordConfName) {

            this.startDate = startDate;
            this.endDate = endDate;
            this.smart_record_conf_name = smartRecordConfName;
        }

        @Override
        public Integer call() {
            LOGGER.info("ProcessorRun started for " + smart_record_conf_name.toUpperCase());

            // store the data in the collections for data source
            String logPath = "/tmp/presidio-output-processor_run_" + smart_record_conf_name + "_" + startDate.toString() + "_" + endDate.toString() + ".log";

            SshResponse p = TerminalCommandsSshUtils.runCommand(
                    Consts.PRESIDIO_OUTPUT, true, Consts.PRESIDIO_DIR, "run", "--start_date " + startDate,
                    "--end_date " + endDate, "--smart_record_conf_name " + smart_record_conf_name,
                    " > " + logPath);

            printLogIfError(logPath);
            assertThat(p.exitCode)
                    .withFailMessage("Error exit code. Log: " + logPath)
                    .isEqualTo(0);

            LOGGER.info("ProcessorRun[" + smart_record_conf_name.toUpperCase() + "] completed successfully.");
            return p.exitCode;
        }
    }

    public class RecalculateUserScore implements Callable<Integer> {
        private final Instant startDate;
        private final Instant endDate;
        private final String entity;

        private RecalculateUserScore(Instant startDate, Instant endDate, String entity) {

            this.startDate = startDate;
            this.endDate = endDate;
            this.entity = entity;
        }

        @Override
        public Integer call() {
            LOGGER.info("RecalculateUserScore started for " + entity.toUpperCase());
            String logPath = "/tmp/presidio-output_recalc_user_score_" + entity + "_" + startDate.toString() + "_" + endDate.toString() + ".log";

            SshResponse p = TerminalCommandsSshUtils.runCommand(Consts.PRESIDIO_OUTPUT, true, Consts.PRESIDIO_DIR,
                    "recalculate-entity-score", "--start_date " + startDate, "--end_date " + endDate,
                    "--fixed_duration_strategy 86400.0 ", " --smart_record_conf_name " + entity + "_hourly ",
                    "--entity_type " + entity, " > " + logPath);


            printLogIfError(logPath);
            assertThat(p.exitCode)
                    .withFailMessage("Error exit code. Log: " + logPath)
                    .isEqualTo(0);

            LOGGER.info("RecalculateUserScore[" + entity.toUpperCase() + "] completed successfully.");
            return p.exitCode;
        }
    }

}
