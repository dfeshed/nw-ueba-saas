package com.rsa.netwitness.presidio.automation.data.processing.mongo_core;


import com.rsa.netwitness.presidio.automation.common.helpers.DateTimeHelperUtils;
import com.rsa.netwitness.presidio.automation.ssh.client.SshResponse;
import com.rsa.netwitness.presidio.automation.ssh.helper.SshHelper;
import com.rsa.netwitness.presidio.automation.utils.ade.inserter.AdeInserter;
import com.rsa.netwitness.presidio.automation.utils.ade.inserter.AdeInserterFactory;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import presidio.ade.domain.record.aggregated.ScoredFeatureAggregationRecord;
import presidio.ade.domain.record.aggregated.SmartRecord;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;
import presidio.ade.sdk.common.AdeManagerSdk;
import presidio.ade.sdk.common.RunId;
import presidio.ade.sdk.common.RunStatus;
import presidio.data.domain.event.Event;
import presidio.data.generators.common.GeneratorException;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static com.rsa.netwitness.presidio.automation.domain.config.Consts.PRESIDIO_DIR;
import static com.rsa.netwitness.presidio.automation.file.LogSshUtils.printLogIfError;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * ADETestManager - stores, processes and monitors ADE component using ADE SDK
 */
public class ADETestManager {
    @Autowired
    private MongoTemplate mongoTemplate;


    static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger)
            LoggerFactory.getLogger(ADETestManager.class.getName());

    public static final String JAVA_CMD = "java -jar -Duser.timezone=UTC ";

    public static final String PRESIDIO_ADE_APP_SCORE_AGGR = "presidio-ade-app-score-aggr";
    public static final String PRESIDIO_ADE_APP_MODEL_FEATURE_BUCKETS = "presidio-ade-app-model-feature-buckets";
    public static final String PRESIDIO_ADE_APP_MODELING = "presidio-ade-app-modeling";
    public static final String PRESIDIO_ADE_APP_ACCUMULATE_AGGR = "presidio-ade-app-accumulate-aggr";
    public static final String PRESIDIO_ADE_APP_FEATURE_AGGR = "presidio-ade-app-feature-aggr";
    public static final String PRESIDIO_ADE_APP_SMART = "presidio-ade-app-smart";
    public static final String PRESIDIO_ADE_APP_ACCUMULATE_SMART = "presidio-ade-app-accumulate-smart";

    private AdeManagerSdk adeManagerSDK;
    private AdeInserterFactory adeInserterFactory;
    private String dataSource;
    private SshHelper sshHelper = new SshHelper();

    public ADETestManager(AdeManagerSdk adeManagerSDK, AdeInserterFactory adeInserterFactory) {
        this.adeManagerSDK = adeManagerSDK;
        this.adeInserterFactory = adeInserterFactory;
    }

    /***
     * Clear data from ADE collections
     */
    public void clearAdeCollections(String schemaName) {
        String schemaNameInCollectionName = (schemaName.equalsIgnoreCase("active_directory") ? "activedirectory" : schemaName.toLowerCase());
        mongoTemplate.getCollectionNames().forEach(collectionName -> {
            if ((collectionName.startsWith("aggr_") ||
                    collectionName.startsWith("scored_") ||
                    collectionName.startsWith("model_") ||
                    collectionName.startsWith("accm_") ||
                    collectionName.startsWith("enriched_")
            ) &&
                    // workaround for ADE collections naming convention: in some cases it is in Camel case, in other - with underscore
                    (collectionName.toLowerCase().contains(schemaNameInCollectionName) ||
                            collectionName.toLowerCase().contains(schemaName.toLowerCase()))) {
                mongoTemplate.dropCollection(collectionName);
            }
        });
    }

    public void clearAdeSmartCollections() {
        mongoTemplate.getCollectionNames().forEach(collectionName -> {
            if (collectionName.startsWith("smart_")) {
                mongoTemplate.dropCollection(collectionName);
            }
        });
    }

    public void clearAllCollections() {
        mongoTemplate.getCollectionNames().forEach(collectionName -> {
            if (!collectionName.startsWith("management_ttl")) {
                mongoTemplate.dropCollection(collectionName);
            }
        });
    }

    /**
     * Calls store method to insert generated events into mongodb collection
     * Limitation: the list of events should contain events of the same data source.
     *
     * @param evList - list of generated events
     */
    public void insert(List<? extends Event> evList) {
        if (evList == null || evList.isEmpty()) {
            return;
        }
        //currently implemented insert that expect all the events to be from the same type. later we may change this.
        AdeInserter inserter = adeInserterFactory.getAdeInserter(evList.get(0).getClass());
        inserter.insert(evList);
        dataSource = inserter.getDataSource();
    }

    public void insertAllEvents(List<List<? extends Event>> allEvents) {
        for (List<? extends Event> evList : allEvents) {
            insert(evList);
        }
    }

    public void processModeling(String group_name, String session_id, Instant end) {
        // Builds models according to the group_name:
        // group_name :  [enriched-record-models or feature-aggregation-record-models(F) or smart-record-models ]
        String logPath = "/tmp/" + PRESIDIO_ADE_APP_MODELING + "_process_" + group_name + "_" + session_id + "_" + end.toString() + ".log";

        SshResponse p3 = sshHelper.uebaHostExec().setUserDir(PRESIDIO_DIR).run(JAVA_CMD + PRESIDIO_ADE_APP_MODELING + ".jar",
                "process", "--group_name " + group_name, "--session_id " + session_id, "--end_date " + end.toString()
                        + " > " + logPath);

        printLogIfError(logPath);
        assertThat(p3.exitCode)
                .as("Error exit code. Log: " + logPath)
                .isEqualTo(0);
    }

    public void processScoreAggr(Instant start, Instant end, String timeFrame, String schema) {
        String logPath = "/tmp/" + PRESIDIO_ADE_APP_SCORE_AGGR + "_run_" + schema + "_" + start.toString() + "_" + end.toString() + ".log";

        // score raw events and builds P buckets
        SshResponse p4 = sshHelper.uebaHostExec().setUserDir(PRESIDIO_DIR).run(JAVA_CMD + PRESIDIO_ADE_APP_SCORE_AGGR + ".jar",
                "run", "--schema " + schema.toUpperCase(), "--start_date " + start.toString(), "--end_date " + end.toString(),
                "--fixed_duration_strategy " + getFixedDuration(timeFrame)
                        + " > " + logPath);

        printLogIfError(logPath);
        assertThat(p4.exitCode)
                .as("Error exit code. Log: " + logPath)
                .isEqualTo(0);
    }


    public void processModelFeatureBuckets(Instant start, Instant end, String timeFrame, String schema) {
        String logPath = "/tmp/" + PRESIDIO_ADE_APP_MODEL_FEATURE_BUCKETS + "_run_" + schema + "_" + start.toString() + "_" + end.toString() + ".log";
        // builds the histograms (aggr_<feature>Histogram<context+dataSource>Daily)
        //--fixed_duration_strategy should be hourly 3600
        SshResponse p4 = sshHelper.uebaHostExec().setUserDir(PRESIDIO_DIR).run(JAVA_CMD + PRESIDIO_ADE_APP_MODEL_FEATURE_BUCKETS + ".jar",
                "run", "--schema " + schema.toUpperCase(),
                "--start_date " + start.toString(), "--end_date " + end.toString(), "--fixed_duration_strategy " + getFixedDuration(timeFrame)
                        + " > " + logPath);


        printLogIfError(logPath);
        assertThat(p4.exitCode)
                .as("Error exit code. Log: " + logPath)
                .isEqualTo(0);
    }

    public void processFeatureAggr(Instant start, Instant end, String timeFrame, String schema) {
        String logPath = "/tmp/" + PRESIDIO_ADE_APP_FEATURE_AGGR + "_run_" + schema + "_" + start.toString() + "_" + end.toString() + ".log";

        // builds F features
        SshResponse p4 = sshHelper.uebaHostExec().setUserDir(PRESIDIO_DIR).run(JAVA_CMD + PRESIDIO_ADE_APP_FEATURE_AGGR + ".jar",
                "run", "--schema " + schema.toUpperCase(),
                "--start_date " + start.toString(), "--end_date " + end.toString(), "--fixed_duration_strategy " + getFixedDuration(timeFrame)
                        + " > " + logPath);

        printLogIfError(logPath);
        assertThat(p4.exitCode)
                .as("Error exit code. Log: " + logPath)
                .isEqualTo(0);
    }

    public void processAccumulateAggr(Instant start, Instant end, String schema) {
        String logPath= "/tmp/" + PRESIDIO_ADE_APP_ACCUMULATE_AGGR + "_run_" + schema + "_" + start.toString() + "_" + end.toString() + ".log";
        // builds F features
        SshResponse p4 = sshHelper.uebaHostExec().setUserDir(PRESIDIO_DIR).run(JAVA_CMD + PRESIDIO_ADE_APP_ACCUMULATE_AGGR + ".jar",
                "run", "--schema " + schema.toUpperCase(),
                "--start_date " + start.toString(), "--end_date " + end.toString(), "--fixed_duration_strategy 86400  --feature_bucket_strategy 3600"
                        + " > " + logPath);

        printLogIfError(logPath);
        assertThat(p4.exitCode)
                .as("Error exit code. Log: " + logPath)
                .isEqualTo(0);
    }


    public void processSmart(Instant start, Instant end) {
        processSmart(start, end, "userId_hourly");
    }

    public void processAccumulateSmart(Instant start, Instant end) {
        processAccumulateSmart(start, end, "userId_hourly");
    }


    public void processSmart(Instant start, Instant end, String entity) {
        // builds F features
        String logPath = "/tmp/" + PRESIDIO_ADE_APP_SMART + "_process_" + entity + "_" + start.toString() + "_" + end.toString() + ".log";

        SshResponse p4 = sshHelper.uebaHostExec().setUserDir(PRESIDIO_DIR).run(JAVA_CMD + PRESIDIO_ADE_APP_SMART + ".jar",
                "process", "--smart_record_conf_name " + entity,
                "--start_date " + start.toString(), "--end_date " + end.toString()
                        + " > " + logPath);

        printLogIfError(logPath);
        assertThat(p4.exitCode)
                .as("Error exit code. Log: " + logPath)
                .isEqualTo(0);
    }

    public void processAccumulateSmart(Instant start, Instant end, String entity) {
        // builds F features
        String logPath = "/tmp/" + PRESIDIO_ADE_APP_ACCUMULATE_SMART + "_run_" + entity + "_" + start.toString() + "_" + end.toString() + ".log";

        SshResponse p4 = sshHelper.uebaHostExec().setUserDir(PRESIDIO_DIR).run(JAVA_CMD + PRESIDIO_ADE_APP_ACCUMULATE_SMART + ".jar",
                "run", "--smart_record_conf_name " + entity,
                "--start_date " + start.toString(), "--end_date " + end.toString(), "--fixed_duration_strategy 86400"
                        + " > " + logPath);

        printLogIfError(logPath);
        assertThat(p4.exitCode)
                .as("Error exit code. Log: " + logPath)
                .isEqualTo(0);
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


    private boolean processHistoricalData(RunId historicalRunId, long timeout) throws TimeoutException {
        // process next historical time range
        adeManagerSDK.processNextHistoricalTimeRange(historicalRunId);

        // check the run status each second until returns success or reach timeout
        RunStatus status = null;
        Instant timeout_time = Instant.now().plusSeconds(timeout);
        while (Instant.now().isBefore(timeout_time)) {
            status = adeManagerSDK.getHistoricalRunStatus(historicalRunId);

            if (status == null) return true;

            //if error
            //handle error, return false

            // sleep
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                LOGGER.error("Exception in sleep:" + e.getMessage());
                return false;
            }
        }
        // timeout reached
        throw new TimeoutException(String.format("Timeout reached when processing data. \nTimeout: %d sec. \nRunId: %s. \nLatest status: %s" +
                timeout, historicalRunId.toString(), status.toString()));
    }

    /***
     * Runs ADE applications to process historical events until 2 days back: model feature buckets and modelling.
     * Runs ADE applications to score last 2 days events
     *
     * @param firstHistoricalEventTime - time of the first event
     *
     */
    public void processEnriched2Scored(Instant firstHistoricalEventTime, String schema) {
        processEnriched2Scored(firstHistoricalEventTime, 2, schema);
    }

    public void processEnriched2Scored(Instant firstHistoricalEventTime, int anomalyDayBack, String schema) {
        this.processFeatureAggr(firstHistoricalEventTime, DateTimeHelperUtils.truncateAndMinusDays(anomalyDayBack), "hourly", schema);
        this.processModelFeatureBuckets(firstHistoricalEventTime, DateTimeHelperUtils.truncateAndMinusDays(anomalyDayBack), "hourly", schema);
        this.processModeling("enriched-record-models", "test-run", DateTimeHelperUtils.truncateAndMinusDays(anomalyDayBack));
        this.processScoreAggr(DateTimeHelperUtils.truncateAndMinusDays(anomalyDayBack), DateTimeHelperUtils.truncateAndMinusDays(anomalyDayBack - 1), "hourly", schema);
    }

    public void processEnriched2F(Instant firstHistoricalEventTime, String schema) {
        processEnriched2F(firstHistoricalEventTime, 2, schema);
    }

    public void processEnriched2F(Instant firstHistoricalEventTime, int anomalyDayBack, String schema) {
        this.processAccumulateAggr(firstHistoricalEventTime, DateTimeHelperUtils.truncateAndMinusDays(anomalyDayBack), schema);
        this.processModeling("feature-aggregation-record-models", "test-run", DateTimeHelperUtils.truncateAndMinusDays(anomalyDayBack));
        this.processFeatureAggr(DateTimeHelperUtils.truncateAndMinusDays(anomalyDayBack), DateTimeHelperUtils.truncateAndMinusDays(anomalyDayBack - 1), "hourly", schema);
    }

    public void processSmart(Instant firstHistoricalEventTime, int anomalyDay) {
//            this.processSmart(firstHistoricalEventTime, DateTimeHelperUtils.truncateAndMinusDays(anomalyDay));
        this.processAccumulateSmart(firstHistoricalEventTime, DateTimeHelperUtils.truncateAndMinusDays(anomalyDay));
        this.processModeling("smart-record-models", "test-run", DateTimeHelperUtils.truncateAndMinusDays(anomalyDay));
        this.processSmart(DateTimeHelperUtils.truncateAndMinusDays(anomalyDay), DateTimeHelperUtils.truncateAndMinusDays(anomalyDay - 1));
    }

    public long getNumberOfScoredEnrichedDocuments(String username, int lowestScores, int highestScores, String collectionName) {
        Query query = new Query()
                .addCriteria(Criteria.where(AdeScoredEnrichedRecord.SCORE_FIELD_NAME).gte(lowestScores).andOperator(Criteria.where(AdeScoredEnrichedRecord.SCORE_FIELD_NAME).lte(highestScores)))
                .addCriteria(Criteria.where(AdeScoredEnrichedRecord.CONTEXT_FIELD_NAME + ".userId").is(username));
        long count = mongoTemplate.count(query, AdeScoredEnrichedRecord.class, collectionName);
        return count;
    }

    public long getNumberOfSmartDocuments(String username, int lowestScores, int highestScores, String collectionName) {
        Query query = new Query()
                .addCriteria(Criteria.where(SmartRecord.SMART_SCORE_FIELD).gte(lowestScores).andOperator(Criteria.where(SmartRecord.SMART_SCORE_FIELD).lte(highestScores)))
                .addCriteria(Criteria.where(SmartRecord.CONTEXT_ID_FIELD).is("userId#" + username));
        long count = mongoTemplate.count(query, SmartRecord.class, collectionName);
        return count;
    }

    public long getNumberOfFDocuments(String username, int lowestScores, int highestScores, String collectionName) {

        Query query = new Query()
                .addCriteria(Criteria.where(ScoredFeatureAggregationRecord.SCORE_FIELD_NAME).gte(lowestScores).andOperator(Criteria.where(AdeScoredEnrichedRecord.SCORE_FIELD_NAME).lte(highestScores)))
                .addCriteria(Criteria.where(AdeScoredEnrichedRecord.CONTEXT_FIELD_NAME + ".userId").is(username));
        long count = mongoTemplate.count(query, ScoredFeatureAggregationRecord.class, collectionName);
        return count;

    }

    public void processSmartsFromFs(int normalStartDayBack, int abnormalStartDayBack, int abnormalEndDaysBack) throws GeneratorException {
        /** Process events by running ADE applications*/
        Instant normalPeriodStart = DateTimeHelperUtils.truncateAndMinusDays(normalStartDayBack);
        Instant abnormalPeriodStart = DateTimeHelperUtils.truncateAndMinusDays(abnormalStartDayBack);
        Instant abnormalPeriodEnd = DateTimeHelperUtils.truncateAndMinusDays(abnormalEndDaysBack);

        //Fs for normal
        processAccumulateAggr(normalPeriodStart, abnormalPeriodStart, "file");
        processAccumulateAggr(normalPeriodStart, abnormalPeriodStart, "active_directory");
        processAccumulateAggr(normalPeriodStart, abnormalPeriodStart, "authentication");
        processModeling("feature-aggregation-record-models", "test-run", abnormalPeriodStart);
        processFeatureAggr(normalPeriodStart, abnormalPeriodStart, "hourly", "file");
        processFeatureAggr(normalPeriodStart, abnormalPeriodStart, "hourly", "active_directory");
        processFeatureAggr(normalPeriodStart, abnormalPeriodStart, "hourly", "authentication");

        //SMART
        processSmart(normalPeriodStart, abnormalPeriodStart);
        processAccumulateSmart(normalPeriodStart, abnormalPeriodStart);
        processModeling("smart-record-models", "test-run", abnormalPeriodStart);

        // new Fs again, for SMART anomaly
        processFeatureAggr(abnormalPeriodStart, abnormalPeriodEnd, "hourly", "file");
        processFeatureAggr(abnormalPeriodStart, abnormalPeriodEnd, "hourly", "active_directory");
        processFeatureAggr(abnormalPeriodStart, abnormalPeriodEnd, "hourly", "authentication");

        // New Smarts
        processSmart(abnormalPeriodStart, abnormalPeriodEnd);
    }

    public void processSmartsFromPsAndFs(int normalStartDayBack, int abnormalStartDayBack, int abnormalEndDaysBack) throws GeneratorException {
        /** Process events by running ADE applications*/
        Instant normalPeriodStart = DateTimeHelperUtils.truncateAndMinusDays(normalStartDayBack);
        Instant abnormalPeriodStart = DateTimeHelperUtils.truncateAndMinusDays(abnormalStartDayBack);
        Instant abnormalPeriodEnd = DateTimeHelperUtils.truncateAndMinusDays(abnormalEndDaysBack);

        //Ps for normal
        processModelFeatureBuckets(normalPeriodStart, abnormalPeriodStart, "hourly", "file");
        processModelFeatureBuckets(normalPeriodStart, abnormalPeriodStart, "hourly", "active_directory");
        processModelFeatureBuckets(normalPeriodStart, abnormalPeriodStart, "hourly", "authentication");
        processModeling("enriched-record-models", "test-run", abnormalPeriodStart);
        processScoreAggr(normalPeriodStart, abnormalPeriodStart, "hourly", "file");
        processScoreAggr(normalPeriodStart, abnormalPeriodStart, "hourly", "active_directory");
        processScoreAggr(normalPeriodStart, abnormalPeriodStart, "hourly", "authentication");

        //Fs for normal
        processAccumulateAggr(normalPeriodStart, abnormalPeriodStart, "file");
        processAccumulateAggr(normalPeriodStart, abnormalPeriodStart, "active_directory");
        processAccumulateAggr(normalPeriodStart, abnormalPeriodStart, "authentication");
        processModeling("feature-aggregation-record-models", "test-run", abnormalPeriodStart);
        processFeatureAggr(normalPeriodStart, abnormalPeriodStart, "hourly", "file");
        processFeatureAggr(normalPeriodStart, abnormalPeriodStart, "hourly", "active_directory");
        processFeatureAggr(normalPeriodStart, abnormalPeriodStart, "hourly", "authentication");

        //SMART
        processSmart(normalPeriodStart, abnormalPeriodStart);
        processAccumulateSmart(normalPeriodStart, abnormalPeriodStart);
        processModeling("smart-record-models", "test-run", abnormalPeriodStart);

        // new Ps again, for SMART anomaly
        processScoreAggr(abnormalPeriodStart, abnormalPeriodEnd, "hourly", "file");
        processScoreAggr(abnormalPeriodStart, abnormalPeriodEnd, "hourly", "active_directory");
        processScoreAggr(abnormalPeriodStart, abnormalPeriodEnd, "hourly", "authentication");

        // new Fs again, for SMART anomaly
        processFeatureAggr(abnormalPeriodStart, abnormalPeriodEnd, "hourly", "file");
        processFeatureAggr(abnormalPeriodStart, abnormalPeriodEnd, "hourly", "active_directory");
        processFeatureAggr(abnormalPeriodStart, abnormalPeriodEnd, "hourly", "authentication");

        // New Smarts
        processSmart(abnormalPeriodStart, abnormalPeriodEnd);
    }

    public void processSmartsTemp() throws GeneratorException {
        /** Process events by running ADE applications*/
        Instant normalPeriodStart = DateTimeHelperUtils.truncateAndMinusDays(20);
        Instant abnormalPeriod1 = DateTimeHelperUtils.truncateAndMinusDays(5);
        Instant abnormalPeriod2 = DateTimeHelperUtils.truncateAndMinusDays(3);
        Instant abnormalPeriodEnd = DateTimeHelperUtils.truncateAndMinusDays(1);

        //Ps for normal
        processModelFeatureBuckets(normalPeriodStart, abnormalPeriod1, "hourly", "file");
        processModelFeatureBuckets(normalPeriodStart, abnormalPeriod1, "hourly", "active_directory");
        processModelFeatureBuckets(normalPeriodStart, abnormalPeriod1, "hourly", "authentication");
        processModeling("enriched-record-models", "test-run", abnormalPeriod1);
        processScoreAggr(normalPeriodStart, abnormalPeriod1, "hourly", "file");
        processScoreAggr(normalPeriodStart, abnormalPeriod1, "hourly", "active_directory");
        processScoreAggr(normalPeriodStart, abnormalPeriod1, "hourly", "authentication");

        //Fs for normal
        processAccumulateAggr(normalPeriodStart, abnormalPeriod1, "file");
        processAccumulateAggr(normalPeriodStart, abnormalPeriod1, "active_directory");
        processAccumulateAggr(normalPeriodStart, abnormalPeriod1, "authentication");
        processModeling("feature-aggregation-record-models", "test-run", abnormalPeriod1);
        processFeatureAggr(normalPeriodStart, abnormalPeriod1, "hourly", "file");
        processFeatureAggr(normalPeriodStart, abnormalPeriod1, "hourly", "active_directory");
        processFeatureAggr(normalPeriodStart, abnormalPeriod1, "hourly", "authentication");

        //SMART
        processSmart(normalPeriodStart, abnormalPeriod1);
        processAccumulateSmart(normalPeriodStart, abnormalPeriod1);
        processModeling("smart-record-models", "test-run", abnormalPeriod1);

        // new Ps again, for SMART anomaly
        processScoreAggr(abnormalPeriod1, abnormalPeriod2, "hourly", "file");
        processScoreAggr(abnormalPeriod1, abnormalPeriod2, "hourly", "active_directory");
        processScoreAggr(abnormalPeriod1, abnormalPeriod2, "hourly", "authentication");

        // new Fs again, for SMART anomaly
        processFeatureAggr(abnormalPeriod1, abnormalPeriod2, "hourly", "file");
        processFeatureAggr(abnormalPeriod1, abnormalPeriod2, "hourly", "active_directory");
        processFeatureAggr(abnormalPeriod1, abnormalPeriod2, "hourly", "authentication");

        // New Smarts 1
        processSmart(abnormalPeriod1, abnormalPeriod2);

        // new Ps again, for SMART anomaly
        processScoreAggr(abnormalPeriod2, abnormalPeriodEnd, "hourly", "file");
        processScoreAggr(abnormalPeriod2, abnormalPeriodEnd, "hourly", "active_directory");
        processScoreAggr(abnormalPeriod2, abnormalPeriodEnd, "hourly", "authentication");

        // new Fs again, for SMART anomaly
        processFeatureAggr(abnormalPeriod2, abnormalPeriodEnd, "hourly", "file");
        processFeatureAggr(abnormalPeriod2, abnormalPeriodEnd, "hourly", "active_directory");
        processFeatureAggr(abnormalPeriod2, abnormalPeriodEnd, "hourly", "authentication");

        // New Smarts 2
        processSmart(abnormalPeriod2, abnormalPeriodEnd);
    }
}
