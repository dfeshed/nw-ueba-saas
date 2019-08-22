package com.rsa.netwitness.presidio.automation.utils.ade;


import com.rsa.netwitness.presidio.automation.domain.config.Consts;
import com.rsa.netwitness.presidio.automation.ssh.TerminalCommandsSshUtils;
import com.rsa.netwitness.presidio.automation.ssh.client.SshResponse;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.concurrent.Callable;

import static com.rsa.netwitness.presidio.automation.ssh.LogSshUtils.printLogFile;
import static org.assertj.core.api.Assertions.assertThat;


public class AdeDataProcessingHelper {

    static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger)
            LoggerFactory.getLogger(AdeDataProcessingHelper.class.getName());

    private static final String JAVA_CMD = "java -jar -Duser.timezone=UTC ";
    private static final String PRESIDIO_ADE_APP_SCORE_AGGR = "presidio-ade-app-score-aggr";
    private static final String PRESIDIO_ADE_APP_MODEL_FEATURE_BUCKETS = "presidio-ade-app-model-feature-buckets";
    private static final String PRESIDIO_ADE_APP_MODELING = "presidio-ade-app-modeling";
    private static final String PRESIDIO_ADE_APP_ACCUMULATE_AGGR = "presidio-ade-app-accumulate-aggr";
    private static final String PRESIDIO_ADE_APP_FEATURE_AGGR = "presidio-ade-app-feature-aggr";
    private static final String PRESIDIO_ADE_APP_SMART = "presidio-ade-app-smart";
    private static final String PRESIDIO_ADE_APP_ACCUMULATE_SMART = "presidio-ade-app-accumulate-smart";

    public ProcessScoreAggr processScoreAggr(Instant start, Instant end, String timeFrame, String schema) {
        return new ProcessScoreAggr(start, end, timeFrame, schema);
    }

    public ProcessModelFeatureBuckets processModelFeatureBuckets(Instant start, Instant end, String timeFrame, String schema) {
        return new ProcessModelFeatureBuckets(start, end, timeFrame, schema);
    }

    public ProcessSmart processSmart(Instant start, Instant end, String entity) {
        return new ProcessSmart(start, end, entity);
    }

    public ProcessAccumulateSmart processAccumulateSmart(Instant start, Instant end, String entity) {
        return new ProcessAccumulateSmart(start, end, entity);
    }

    public ProcessModeling processModeling(String group_name, String session_id, Instant end) {
        return new ProcessModeling(group_name, session_id, end);
    }

    public ProcessAccumulateAggr processAccumulateAggr(Instant start, Instant end, String schema) {
        return new ProcessAccumulateAggr(start, end, schema);
    }

    public ProcessFeatureAggr processFeatureAggr(Instant start, Instant end, String timeFrame, String schema) {
        return new ProcessFeatureAggr(start, end, timeFrame, schema);
    }


    public class ProcessScoreAggr implements Callable<Integer> {
        private final Instant start, end;
        private final String timeFrame, schema;

        private ProcessScoreAggr(Instant start, Instant end, String timeFrame, String schema) {
            this.start = start;
            this.end = end;
            this.timeFrame = timeFrame;
            this.schema = schema;
        }

        @Override
        public Integer call() {
            LOGGER.info("ProcessScoreAggr started for " + schema.toUpperCase());

            String logPath = "/tmp/" + PRESIDIO_ADE_APP_SCORE_AGGR + "_run_" + schema + "_" + start.toString() + "_" + end.toString() + ".log";

            // score raw events and builds P buckets
            SshResponse p4 = TerminalCommandsSshUtils.runCommand(JAVA_CMD + PRESIDIO_ADE_APP_SCORE_AGGR + ".jar", true, Consts.PRESIDIO_DIR, "run", "--schema " + schema.toUpperCase(),
                    "--start_date " + start.toString(), "--end_date " + end.toString(), "--fixed_duration_strategy " + getFixedDuration(timeFrame),
                    " > " + logPath);

            assertThat(p4.exitCode)
                    .withFailMessage("Error exit code.\nCheck the log: " + logPath)
                    .isEqualTo(0);

            printLogFile(logPath);
            LOGGER.info("ProcessScoreAggr[" + schema.toUpperCase() + "] completed successfully.");
            return p4.exitCode;
        }
    }

    public class ProcessModelFeatureBuckets implements Callable<Integer> {
        private final Instant start, end;
        private final String timeFrame, schema;

        private ProcessModelFeatureBuckets(Instant start, Instant end, String timeFrame, String schema) {
            this.start = start;
            this.end = end;
            this.timeFrame = timeFrame;
            this.schema = schema;
        }

        @Override
        public Integer call() {
            LOGGER.info("ProcessModelFeatureBuckets started for " + schema.toUpperCase());

            String logPath = "/tmp/" + PRESIDIO_ADE_APP_MODEL_FEATURE_BUCKETS + "_run_" + schema + "_" + start.toString() + "_" + end.toString() + ".log";
            // builds the histograms (aggr_<feature>Histogram<context+dataSource>Daily)
            //--fixed_duration_strategy should be hourly 3600
            SshResponse p4 = TerminalCommandsSshUtils.runCommand(JAVA_CMD + PRESIDIO_ADE_APP_MODEL_FEATURE_BUCKETS + ".jar", true, Consts.PRESIDIO_DIR, "run", "--schema " + schema.toUpperCase(),
                    "--start_date " + start.toString(), "--end_date " + end.toString(), "--fixed_duration_strategy " + getFixedDuration(timeFrame),
                    " > " + logPath);
            assertThat(p4.exitCode)
                    .withFailMessage("Error exit code.\nCheck the log: " + logPath)
                    .isEqualTo(0);

            printLogFile(logPath);
            LOGGER.info("ProcessModelFeatureBuckets[" + schema.toUpperCase() + "] completed successfully.");
            return p4.exitCode;
        }
    }


    public class ProcessSmart implements Callable<Integer> {
        private final Instant start, end;
        private final String entity;

        private ProcessSmart(Instant start, Instant end, String entity) {
            this.start = start;
            this.end = end;
            this.entity = entity;
        }

        @Override
        public Integer call() {
            LOGGER.info("ProcessSmart started for " + entity.toUpperCase());

            String logPath = "/tmp/" + PRESIDIO_ADE_APP_SMART + "_process_" + entity + "_" + start.toString() + "_" + end.toString() + ".log";

            SshResponse p4 = TerminalCommandsSshUtils.runCommand(JAVA_CMD + PRESIDIO_ADE_APP_SMART + ".jar", true, Consts.PRESIDIO_DIR, "process", "--smart_record_conf_name " + entity,
                    "--start_date " + start.toString(), "--end_date " + end.toString(),
                    " > " + logPath);

            assertThat(p4.exitCode)
                    .withFailMessage("Error exit code.\nCheck the log: " + logPath)
                    .isEqualTo(0);

            printLogFile(logPath);
            LOGGER.info("ProcessSmart[" + entity.toUpperCase() + "] completed successfully.");
            return p4.exitCode;
        }
    }

    public class ProcessAccumulateSmart implements Callable<Integer> {
        private final Instant start, end;
        private final String entity;

        public ProcessAccumulateSmart(Instant start, Instant end, String entity) {
            this.start = start;
            this.end = end;
            this.entity = entity;
        }

        @Override
        public Integer call() {
            LOGGER.info("ProcessAccumulateSmart started for " + entity.toUpperCase());

            // builds F features
            String logPath = "/tmp/" + PRESIDIO_ADE_APP_ACCUMULATE_SMART + "_run_" + entity + "_" + start.toString() + "_" + end.toString() + ".log";
            SshResponse p4 = TerminalCommandsSshUtils.runCommand(JAVA_CMD + PRESIDIO_ADE_APP_ACCUMULATE_SMART + ".jar", true, Consts.PRESIDIO_DIR, "run", "--smart_record_conf_name " + entity,
                    "--start_date " + start.toString(), "--end_date " + end.toString(), "--fixed_duration_strategy 86400",
                    " > " + logPath);

            assertThat(p4.exitCode)
                    .withFailMessage("Error exit code.\nCheck the log: " + logPath)
                    .isEqualTo(0);

            printLogFile(logPath);
            LOGGER.info("ProcessAccumulateSmart[" + entity.toUpperCase() + "] completed successfully.");
            return p4.exitCode;
        }
    }


    public class ProcessModeling implements Callable<Integer> {
        private final Instant end;
        private final String group_name, session_id;

        public ProcessModeling(String group_name, String session_id, Instant end) {
            this.group_name = group_name;
            this.session_id = session_id;
            this.end = end;
        }

        @Override
        public Integer call() {
            LOGGER.info("ProcessModeling started for " + group_name.toUpperCase());

            // Builds models according to the group_name:
            // group_name :  [enriched-record-models or feature-aggregation-record-models(F) or smart-record-models ]
            String logPath = "/tmp/" + PRESIDIO_ADE_APP_MODELING + "_process_" + group_name + "_" + session_id + "_" + end.toString() + ".log";

            SshResponse p3 = TerminalCommandsSshUtils.runCommand(JAVA_CMD + PRESIDIO_ADE_APP_MODELING + ".jar", true, Consts.PRESIDIO_DIR, "process",
                    "--group_name " + group_name, "--session_id " + session_id, "--end_date " + end.toString(),
                    " > " + logPath);

            assertThat(p3.exitCode)
                    .withFailMessage("Error exit code.\nCheck the log: " + logPath)
                    .isEqualTo(0);

            printLogFile(logPath);
            LOGGER.info("ProcessModeling[" + group_name.toUpperCase() + "] completed successfully.");
            return p3.exitCode;
        }
    }


    public class ProcessAccumulateAggr implements Callable<Integer> {
        private final Instant end;
        private final String schema;
        private final Instant start;

        private ProcessAccumulateAggr(Instant start, Instant end, String schema) {
            this.start = start;
            this.end = end;
            this.schema = schema;
        }

        @Override
        public Integer call() {
            LOGGER.info("ProcessAccumulateAggr started for " + schema.toUpperCase());

            String logPath = "/tmp/" + PRESIDIO_ADE_APP_ACCUMULATE_AGGR + "_run_" + schema + "_" + start.toString() + "_" + end.toString() + ".log";
            // builds F features
            SshResponse p4 = TerminalCommandsSshUtils.runCommand(JAVA_CMD + PRESIDIO_ADE_APP_ACCUMULATE_AGGR + ".jar", true, Consts.PRESIDIO_DIR,
                    "run", "--schema " + schema.toUpperCase(),
                    "--start_date " + start.toString(), "--end_date " + end.toString(), "--fixed_duration_strategy 86400  --feature_bucket_strategy 3600",
                    " > " + logPath);

            assertThat(p4.exitCode)
                    .withFailMessage("Error exit code.\nCheck the log: " + logPath)
                    .isEqualTo(0);

            printLogFile(logPath);
            LOGGER.info("ProcessAccumulateAggr[" + schema.toUpperCase() + "] completed successfully.");
            return p4.exitCode;
        }
    }


    public class ProcessFeatureAggr implements Callable<Integer> {
        private final Instant start;
        private final Instant end;
        private final String timeFrame;
        private final String schema;

        private ProcessFeatureAggr(Instant start, Instant end, String timeFrame, String schema) {

            this.start = start;
            this.end = end;
            this.timeFrame = timeFrame;
            this.schema = schema;
        }

        @Override
        public Integer call() {
            LOGGER.info("ProcessFeatureAggr started for " + schema.toUpperCase());

            String logPath = "/tmp/" + PRESIDIO_ADE_APP_FEATURE_AGGR + "_run_" + schema + "_" + start.toString() + "_" + end.toString() + ".log";

            // builds F features
            SshResponse p4 = TerminalCommandsSshUtils.runCommand(JAVA_CMD + PRESIDIO_ADE_APP_FEATURE_AGGR + ".jar", true, Consts.PRESIDIO_DIR, "run", "--schema " + schema.toUpperCase(),
                    "--start_date " + start.toString(), "--end_date " + end.toString(), "--fixed_duration_strategy " + getFixedDuration(timeFrame),
                    " > " + logPath);

            assertThat(p4.exitCode)
                    .withFailMessage("Error exit code.\nCheck the log: " + logPath)
                    .isEqualTo(0);

            printLogFile(logPath);
            LOGGER.info("ProcessFeatureAggr[" + schema.toUpperCase() + "] completed successfully.");
            return p4.exitCode;
        }
    }


    private String getFixedDuration(String timeFrame) {
        String fixed_duration;
        if (timeFrame.equals("hourly"))
            fixed_duration = "3600";
        else if (timeFrame.equals("daily"))
            fixed_duration = "86400";
        else fixed_duration = "";
        return fixed_duration;
    }

}
