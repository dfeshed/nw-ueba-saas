package com.rsa.netwitness.presidio.automation.utils.output;

import com.rsa.netwitness.presidio.automation.domain.config.Consts;
import com.rsa.netwitness.presidio.automation.ssh.SSHManager;
import com.rsa.netwitness.presidio.automation.ssh.TerminalCommands;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.time.Instant;
import java.util.concurrent.Callable;

import static com.rsa.netwitness.presidio.automation.ssh.RunCmdUtils.printLogFile;

public class OutputDataProcessingHelper {
    static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger)
            LoggerFactory.getLogger(OutputDataProcessingHelper.class.getName());



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
            String logFile = "/tmp/presidio-output-processor_run_" + smart_record_conf_name + "_" + startDate.toString() + "_" + endDate.toString() + ".log";

            SSHManager.Response p = TerminalCommands.runCommand(
                    Consts.PRESIDIO_OUTPUT, true, Consts.PRESIDIO_DIR, "run" , "--start_date " + startDate,
                    "--end_date " + endDate , "--smart_record_conf_name " + smart_record_conf_name + " " + " > " + logFile);

            printLogFile(logFile);
            Assert.assertEquals(0, p.exitCode, "Shell command failed. exit value: " + p.exitCode + "\nLog: " + logFile);
            LOGGER.info("ProcessorRun["+ smart_record_conf_name.toUpperCase()+"] completed successfully.");
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
            String logFile = "/tmp/presidio-output_recalc_user_score_" + entity + "_" + startDate.toString() + "_" + endDate.toString() + ".log";

            SSHManager.Response p = TerminalCommands.runCommand(Consts.PRESIDIO_OUTPUT, true, Consts.PRESIDIO_DIR,
                    "recalculate-entity-score", "--start_date " + startDate, "--end_date " + endDate ,
                    " --fixed_duration_strategy 86400.0 " , " --smart_record_conf_name " + entity + "_hourly ",
                    " --entity_type " + entity + " > " + logFile);

            printLogFile(logFile);
            Assert.assertEquals(0, p.exitCode, "Shell command failed. exit value: " + p.exitCode + "\nLog: " + logFile);
            LOGGER.info("RecalculateUserScore["+ entity.toUpperCase()+"] completed successfully.");
            return p.exitCode;
        }
    }

}
