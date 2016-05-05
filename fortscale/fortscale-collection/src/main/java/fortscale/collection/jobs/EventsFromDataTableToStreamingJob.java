package fortscale.collection.jobs;

import fortscale.utils.impala.ImpalaPageRequest;
import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.impala.ImpalaQuery;
import fortscale.utils.kafka.KafkaEventsWriter;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimestampUtils;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTimeConstants;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.jdbc.core.ColumnMapRowMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static fortscale.utils.ConversionUtils.convertToLong;

@DisallowConcurrentExecution
public class EventsFromDataTableToStreamingJob extends ImpalaToKafka {
    private static Logger logger = Logger.getLogger(EventsFromDataTableToStreamingJob.class);

    private static final int FETCH_EVENTS_STEP_IN_MINUTES_DEFAULT = 1440; // 1 day
    private static final long LOGGER_MAX_FREQUENCY = 20 * 60;
    private static final long MAX_SOURCE_DESTINATION_TIME_GAP_DEFAULT = 10 * 60 * 60; // 10 hours gap as default

    private static final String LATEST_EVENT_TIME_JOB_PARAMETER = "latestEventTime";
    private static final String DELTA_TIME_IN_SEC_JOB_PARAMETER = "deltaTimeInSec";
    private static final String SLEEP_FIELD_JOB_PARAMETER = "sleep";
    private static final String THROTTLING_SLEEP_FIELD_JOB_PARAMETER = "throttlingSleep";
    private static final String FETCH_EVENTS_STEP_IN_MINUTES_JOB_PARAMETER = "fetchEventsStepInMinutes";
    private static final String FIELD_CLUSTER_GROUPS_REGEX_RESOURCE_JOB_PARAMETER = "fieldClusterGroupsRegexResource";
    private static final String IMPALA_DESTINATION_TABLE_PARTITION_TYPE_JOB_PARAMETER = "impalaDestinationTablePartitionType";
    private static final String IMPALA_DESTINATION_TABLE_JOB_PARAMETER = "impalaDestinationTable";
    private static final String MAX_SOURCE_DESTINATION_TIME_GAP_JOB_PARAMETER = "maxSourceDestinationTimeGap";
    private static final String DATA_SOURCE_PARAMETER = "dataSource";
    private static final String LAST_STATE_PARAMETER = "lastState";

    //define how much time to subtract from now to get the last event time to send to streaming job
    //default of 3 hours - 60 * 60 * 3
    @Value("${batch.sendTo.kafka.latest.events.time.diff.sec:10800}")
    protected long latestEventsTimeDiffFromNowInSec;

    //define how much time to subtract from the latest event time - this way to get the first event time to send
    @Value("${batch.sendTo.kafka.events.delta.time.sec:3600}")
    protected long eventsDeltaTimeInSec;

    // Non-inherited parameters
    private String impalaTableName;
    private String impalaTableFields;
    private String epochtimeField;
    private String streamingTopic;
    private Long sleepField;
    private Long throttlingSleepField;
    private String streamingTopicKey;
    private int fetchEventsStepInMinutes;
    private String impalaTablePartitionType;
    private String impalaDestinationTablePartitionType;
    private String impalaDestinationTable;
    private long destinationTableLatestTime = 0;
    private long latestLoggerWriteTime = 0;
    private int sleepingCounter = 0;
    private String lastState;

    // Parameters inherited by extending classes
    protected long latestEventTime;
    protected long deltaTimeInSec;
    protected String dataSource;
    protected Long maxSourceDestinationTimeGap;

    protected String getTableName() {
        return impalaTableName;
    }

    @Override
    protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap map = jobExecutionContext.getMergedJobDataMap();

        // get parameters values from the job data map
        impalaTableName = jobDataMapExtension.getJobDataMapStringValue(map, IMPALA_TABLE_NAME_JOB_PARAMETER);
        impalaTableFields = jobDataMapExtension.getJobDataMapStringValue(map, IMPALA_TABLE_FIELDS_JOB_PARAMETER);
        epochtimeField = jobDataMapExtension.getJobDataMapStringValue(map, EPOCH_TIME_FIELD_JOB_PARAMETER);
        streamingTopic = jobDataMapExtension.getJobDataMapStringValue(map, STREAMING_TOPIC_FIELD_JOB_PARAMETER);
        sleepField = jobDataMapExtension.getJobDataMapLongValue(map, SLEEP_FIELD_JOB_PARAMETER, null);
        throttlingSleepField = jobDataMapExtension.getJobDataMapLongValue(map, THROTTLING_SLEEP_FIELD_JOB_PARAMETER, null);
        streamingTopicKey = jobDataMapExtension.getJobDataMapStringValue(map, STREAMING_TOPIC_PARTITION_FIELDS_JOB_PARAMETER);
        latestEventTime = jobDataMapExtension.getJobDataMapLongValue(map, LATEST_EVENT_TIME_JOB_PARAMETER, (TimestampUtils.convertToSeconds(System.currentTimeMillis()) - latestEventsTimeDiffFromNowInSec));
        deltaTimeInSec = jobDataMapExtension.getJobDataMapLongValue(map, DELTA_TIME_IN_SEC_JOB_PARAMETER, eventsDeltaTimeInSec);
        fetchEventsStepInMinutes = jobDataMapExtension.getJobDataMapIntValue(map, FETCH_EVENTS_STEP_IN_MINUTES_JOB_PARAMETER, FETCH_EVENTS_STEP_IN_MINUTES_DEFAULT);
        impalaTablePartitionType = jobDataMapExtension.getJobDataMapStringValue(map, IMPALA_TABLE_PARTITION_TYPE_JOB_PARAMETER, IMPALA_TABLE_PARTITION_TYPE_DEFAULT);
        impalaDestinationTablePartitionType = jobDataMapExtension.getJobDataMapStringValue(map, IMPALA_DESTINATION_TABLE_PARTITION_TYPE_JOB_PARAMETER, IMPALA_TABLE_PARTITION_TYPE_DEFAULT);
        impalaDestinationTable = jobDataMapExtension.getJobDataMapStringValue(map, IMPALA_DESTINATION_TABLE_JOB_PARAMETER, null);
        maxSourceDestinationTimeGap = jobDataMapExtension.getJobDataMapLongValue(map, MAX_SOURCE_DESTINATION_TIME_GAP_JOB_PARAMETER, MAX_SOURCE_DESTINATION_TIME_GAP_DEFAULT);
        dataSource = jobDataMapExtension.getJobDataMapStringValue(map, DATA_SOURCE_PARAMETER);
        lastState = jobDataMapExtension.getJobDataMapStringValue(map, LAST_STATE_PARAMETER);

        if (map.containsKey(FIELD_CLUSTER_GROUPS_REGEX_RESOURCE_JOB_PARAMETER)) {
            Resource fieldClusterGroupsRegexResource = jobDataMapExtension.getJobDataMapResourceValue(map, FIELD_CLUSTER_GROUPS_REGEX_RESOURCE_JOB_PARAMETER);
            try {
                fillRegexMatcherMap(fieldClusterGroupsRegexResource.getFile());
            } catch (Exception e) {
                logger.error("Got an exception while calling get file of resource {}", fieldClusterGroupsRegexResource.getFilename());
            }
        }
    }

    @Override
    protected void runSteps() throws Exception {
        KafkaEventsWriter streamWriter = null;

        int i = 0;
        long startTime = System.currentTimeMillis();
        try {
            String[] fieldsName = ImpalaParser.getTableFieldNamesAsArray(impalaTableFields);
            streamWriter = new KafkaEventsWriter(streamingTopic);
            long timestampCursor = latestEventTime - deltaTimeInSec;
            destinationTableLatestTime = timestampCursor;
            while (timestampCursor < latestEventTime) {
                long nextTimestampCursor = Math.min(latestEventTime, timestampCursor + fetchEventsStepInMinutes *
                        DateTimeConstants.SECONDS_PER_MINUTE);
                List<Map<String, Object>> resultsMap = getDataFromImpala(nextTimestampCursor, latestEventTime,
                        timestampCursor, impalaTableName, epochtimeField, whereCriteria, impalaTablePartitionType);
                long latestEpochTimeSent = 0;

                for (Map<String, Object> result : resultsMap) {
                    JSONObject json = new JSONObject();
                    for (String fieldName : fieldsName) {
                        Object val = result.get(fieldName.toLowerCase());
                        fillJsonWithFieldValue(json, fieldName, val);
                    }

                    // Add the data source sign to the message
                    fillJsonWithFieldValue(json, "data_source", dataSource);
                    // Add the last step  sign to the message
                    fillJsonWithFieldValue(json, "last_state", lastState);

                    streamWriter.send(result.get(streamingTopicKey).toString(), json.toJSONString(JSONStyle.NO_COMPRESS));
                    long currentEpochTimeField = convertToLong(result.get(epochtimeField));
                    if (latestEpochTimeSent < currentEpochTimeField) {
                        latestEpochTimeSent = currentEpochTimeField;
                    }
                }

                throttle(resultsMap.size(), latestEpochTimeSent, nextTimestampCursor);
                timestampCursor = nextTimestampCursor;

                if (sleepField != null) {
                    i++;
                    long nextRoundTime = startTime + i * sleepField * 1000;
                    long sleepTime = nextRoundTime - System.currentTimeMillis();
                    if (sleepTime > 0) {
                        try {
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException e) {
                            logger.error(e.getMessage());
                        }
                    }
                }
            }
        } finally {
            if (streamWriter != null) {
                streamWriter.close();
            }
        }
    }

    private long getGapFromDestinationTable(long timestampCursor) {
        ImpalaQuery query = new ImpalaQuery();
        query.select("*").from(impalaDestinationTable);
        addPartitionFilterToQuery(query, destinationTableLatestTime, timestampCursor, impalaDestinationTablePartitionType);
        query.limitAndSort(new ImpalaPageRequest(1, new Sort(Direction.DESC, epochtimeField)));
        List<Map<String, Object>> resultsMap = impalaJdbcTemplate.query(query.toSQL(), new ColumnMapRowMapper());
        if (resultsMap == null || resultsMap.size() == 0) {
            return timestampCursor;
        }
        Map<String, Object> result = resultsMap.get(0);
        Object latestEpochTimeField = result.get(epochtimeField);
        if (latestEpochTimeField == null) {
            return timestampCursor;
        }
        destinationTableLatestTime = convertToLong(latestEpochTimeField);
        return timestampCursor - destinationTableLatestTime;
    }

    private void fillRegexMatcherMap(File f) {
        try {
            if (f.exists() && f.isFile()) {
                ArrayList<String> fieldConfList = new ArrayList<>(FileUtils.readLines(f));
                for (String fieldConf : fieldConfList) {
                    FieldRegexMatcherConverter fieldRegexMatcherConverter = new FieldRegexMatcherConverter(fieldConf);
                    fieldRegexMatcherMap.put(fieldRegexMatcherConverter.getInputField(), fieldRegexMatcherConverter);
                }
            }
        } catch (Exception e) {
            logger.error("Got an exception while loading the regex file", e);
        }
    }

    @Override public boolean synchronize(long latestEpochTimeSent) {
        return metricsKafkaSynchronizer.synchronize(latestEpochTimeSent);
    }

    protected void throttle(int numOfResults, long latestEpochTimeSent, long nextTimestampCursor) throws Exception {
        if (throttlingSleepField != null && throttlingSleepField > 0 &&
                impalaDestinationTable != null && numOfResults > 0) {

            long timeGap;
            while ((timeGap = getGapFromDestinationTable(latestEpochTimeSent)) > maxSourceDestinationTimeGap) {
                long currentTimeSeconds = TimestampUtils.convertToSeconds(System.currentTimeMillis());
                if (currentTimeSeconds - latestLoggerWriteTime >= LOGGER_MAX_FREQUENCY) {
                    logger.info("Total number of sleeps so far: {}. Total sleeping time so far: {} seconds.",
                            sleepingCounter, sleepingCounter * throttlingSleepField);
                    logger.info("Gap of {} seconds between events written to topic {} and events in table {}.",
                            timeGap, streamingTopic, impalaDestinationTable);
                    logger.info("Latest epoch time sent is {}. Next timestamp cursor is {}. Sleeping...",
                            latestEpochTimeSent, nextTimestampCursor);
                    latestLoggerWriteTime = currentTimeSeconds;
                }

                try {
                    Thread.sleep(throttlingSleepField * 1000);
                    sleepingCounter++;
                } catch (InterruptedException e) {
                    logger.error("Exception during throttling sleep: {}.", e.getMessage());
                }
            }
        } else if (jobToMonitor != null && latestEpochTimeSent > 0) {
            // Metric based throttling
            synchronize(latestEpochTimeSent);
        }
    }
}