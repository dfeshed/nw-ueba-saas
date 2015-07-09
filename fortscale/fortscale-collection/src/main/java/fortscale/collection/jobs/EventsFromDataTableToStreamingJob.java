package fortscale.collection.jobs;

import fortscale.services.impl.RegexMatcher;
import fortscale.utils.ConfigurationUtils;
import fortscale.utils.ConversionUtils;
import fortscale.utils.TimestampUtils;
import fortscale.utils.hdfs.partition.PartitionStrategy;
import fortscale.utils.hdfs.partition.PartitionsUtils;
import fortscale.utils.impala.ImpalaPageRequest;
import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.impala.ImpalaQuery;
import fortscale.utils.kafka.KafkaEventsWriter;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTimeConstants;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcOperations;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fortscale.utils.impala.ImpalaCriteria.*;


@DisallowConcurrentExecution
public class EventsFromDataTableToStreamingJob extends FortscaleJob {
    private static Logger logger = Logger.getLogger(EventsFromDataTableToStreamingJob.class);

    private static int FETCH_EVENTS_STEP_IN_MINUTES_DEFAULT = 1440; // 1 day
    private static String IMPALA_TABLE_PARTITION_TYPE_DEFAULT = "daily";
    private static long LOGGER_MAX_FREQUENCY = 20 * 60;
    private static long MAX_SOURCE_DESTINATION_TIME_GAP_DEFAULT = 10 * 60 * 60; // 10 hours gap as default

    private static final String IMPALA_TABLE_NAME_JOB_PARAMETER = "impalaTableName";
    private static final String IMPALA_TABLE_FIELDS_JOB_PARAMETER = "impalaTableFields";
    private static final String LATEST_EVENT_TIME_JOB_PARAMETER = "latestEventTime";
    private static final String DELTA_TIME_IN_SEC_JOB_PARAMETER = "deltaTimeInSec";
    private static final String EPOCH_TIME_FIELD_JOB_PARAMETER = "epochtimeField";
    private static final String STREAMING_TOPIC_FIELD_JOB_PARAMETER = "streamingTopic";
    private static final String STREAMING_TOPIC_PARTITION_FIELDS_JOB_PARAMETER = "streamingTopicPartitionKey";
    private static final String WHERE_CRITERIA_FIELD_JOB_PARAMETER = "where";
    private static final String SLEEP_FIELD_JOB_PARAMETER = "sleep";
    private static final String THROTTLING_SLEEP_FIELD_JOB_PARAMETER = "throttlingSleep";
    private static final String FETCH_EVENTS_STEP_IN_MINUTES_JOB_PARAMETER = "fetchEventsStepInMinutes";
    private static final String FIELD_CLUSTER_GROUPS_REGEX_RESOURCE_JOB_PARAMETER = "fieldClusterGroupsRegexResource";
    private static final String IMPALA_TABLE_PARTITION_TYPE_JOB_PARAMETER = "impalaTablePartitionType";
    private static final String IMPALA_DESTINATION_TABLE_PARTITION_TYPE_JOB_PARAMETER = "impalaDestinationTablePartitionType";
    private static final String IMPALA_DESTINATION_TABLE_JOB_PARAMETER = "impalaDestinationTable";
    private static final String MAX_SOURCE_DESTINATION_TIME_GAP_JOB_PARAMETER = "maxSourceDestinationTimeGap";

    //define how much time to subtract from now to get the last event time to send to streaming job
    //default of 3 hours - 60 * 60 * 3
    @Value("${batch.sendTo.kafka.latest.events.time.diff.sec:10800}")
    protected long latestEventsTimeDiffFromNowInSec;

    //define how much time to subtract from the latest event time - this way to get the first event time to send
    @Value("${batch.sendTo.kafka.events.delta.time.sec:3600}")
    protected long eventsDeltaTimeInSec;

    @Autowired
    private JdbcOperations impalaJdbcTemplate;

    //parameters:
    private String impalaTableName;
    private String impalaTableFields;
    private String epochtimeField;
    private String streamingTopic;
    private String whereCriteria;
    private Long sleepField;
    private Long throttlingSleepField;
    private String streamingTopicKey;
    private long latestEventTime;
    private long deltaTimeInSec;
    private int fetchEventsStepInMinutes;
    private String impalaTablePartitionType;
    private  String impalaDestinationTablePartitionType;
    private String impalaDestinationTable;
    private Long maxSourceDestinationTimeGap;
    private long destinationTableLatestTime = 0;
    private long latestLoggerWriteTime = 0;
    private Map<String, FieldRegexMatcherConverter> fieldRegexMatcherMap = new HashMap<String, FieldRegexMatcherConverter>();

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
        whereCriteria = jobDataMapExtension.getJobDataMapStringValue(map, WHERE_CRITERIA_FIELD_JOB_PARAMETER, null);
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

        if (map.containsKey(FIELD_CLUSTER_GROUPS_REGEX_RESOURCE_JOB_PARAMETER)) {
            Resource fieldClusterGroupsRegexResource = jobDataMapExtension.getJobDataMapResourceValue(map, FIELD_CLUSTER_GROUPS_REGEX_RESOURCE_JOB_PARAMETER);
            try {
                fillRegexMatcherMap(fieldClusterGroupsRegexResource.getFile());
            } catch (Exception e) {
                logger.error("Got an exception while calling get file of resource {}", fieldClusterGroupsRegexResource.getFilename());
            }
        }
    }

    private void fillRegexMatcherMap(File f) {
        try {
            if (f.exists() && f.isFile()) {
                ArrayList<String> fieldConfList = new ArrayList<String>(FileUtils.readLines(f));
                for (String fieldConf : fieldConfList) {
                    FieldRegexMatcherConverter fieldRegexMatcherConverter = new FieldRegexMatcherConverter(fieldConf);
                    fieldRegexMatcherMap.put(fieldRegexMatcherConverter.getInputField(), fieldRegexMatcherConverter);
                }
            }
        } catch (Exception e) {
            logger.error("Got an exception while loading the regex file", e);
        }
    }

    @Override
    protected int getTotalNumOfSteps() {
        return 1;
    }

    private void addPartitionFilterToQuery(ImpalaQuery query, long earliestTime, long latestTime, String partitionType) {
        PartitionStrategy partitionStrategy = PartitionsUtils.getPartitionStrategy(partitionType);
        String earliestValue = partitionStrategy.getImpalaPartitionValue(earliestTime);
        String latestValue = partitionStrategy.getImpalaPartitionValue(latestTime);
        if (earliestValue.equals(latestValue)) {
            query.where(equalsTo(partitionStrategy.getImpalaPartitionFieldName(), earliestValue));
        } else {
            query.where(gte(partitionStrategy.getImpalaPartitionFieldName(), earliestValue));
            query.where(lte(partitionStrategy.getImpalaPartitionFieldName(), latestValue));
        }
    }

    @Override
    protected void runSteps() throws Exception {
        KafkaEventsWriter streamWriter = null;

        try {
            String[] fieldsName = ImpalaParser.getTableFieldNamesAsArray(impalaTableFields);
            streamWriter = new KafkaEventsWriter(streamingTopic);
            long timestampCursor = latestEventTime - deltaTimeInSec;
            while (timestampCursor < latestEventTime) {
                long nextTimestampCursor = Math.min(latestEventTime, timestampCursor + fetchEventsStepInMinutes * DateTimeConstants.SECONDS_PER_MINUTE);
                ImpalaQuery query = new ImpalaQuery();
                query.select("count(*)").from(impalaTableName);
                query.andWhere(gte(epochtimeField, Long.toString(timestampCursor)));
                if (StringUtils.isNotBlank(whereCriteria))
                    query.andWhere(whereCriteria);
                addPartitionFilterToQuery(query, timestampCursor, nextTimestampCursor, impalaTablePartitionType);
                if (nextTimestampCursor == latestEventTime)
                    query.andWhere(lte(epochtimeField, Long.toString(nextTimestampCursor)));
                else
                    query.andWhere(lt(epochtimeField, Long.toString(nextTimestampCursor)));
                int limit = impalaJdbcTemplate.queryForObject(query.toSQL(), Integer.class);
                query.select("*");
                query.limitAndSort(new ImpalaPageRequest(limit, new Sort(Direction.ASC, epochtimeField)));

                List<Map<String, Object>> resultsMap = impalaJdbcTemplate.query(query.toSQL(), new ColumnMapRowMapper());
                long latestEpochTimeSent = 0;

                for (Map<String, Object> result : resultsMap) {
                    JSONObject json = new JSONObject();
                    for (String fieldName : fieldsName) {
                        Object val = result.get(fieldName.toLowerCase());
                        fillJsonWithFieldValue(json, fieldName, val);
                    }
                    streamWriter.send(result.get(streamingTopicKey).toString(), json.toJSONString(JSONStyle.NO_COMPRESS));
                    long currentEpochTimeField = ConversionUtils.convertToLong(result.get(epochtimeField));
                    if (latestEpochTimeSent < currentEpochTimeField) {
                        latestEpochTimeSent = currentEpochTimeField;
                    }
                }

                monitorDataReceived(query.toSQL(), resultsMap.size(), "Events");

                if (throttlingSleepField != null && throttlingSleepField > 0 && impalaDestinationTable != null && resultsMap.size() > 0) {
                    long timeGap;
                    while ((timeGap = getGapFromDestinationTable(latestEpochTimeSent)) > maxSourceDestinationTimeGap) {
                        long currentTimeMillis = TimestampUtils.convertToSeconds(System.currentTimeMillis());
                        if (currentTimeMillis - latestLoggerWriteTime >= LOGGER_MAX_FREQUENCY) {
                            logger.info("Gap of {} between events written to topic {} and scored events in table {}. Latest epoch time sent is {}. Next timestamp cursor is {} -> Sleeping...", timeGap, streamingTopic, impalaDestinationTable, latestEpochTimeSent, nextTimestampCursor);
                            latestLoggerWriteTime = currentTimeMillis;
                        }
                        try {
                            Thread.sleep(TimestampUtils.convertToMilliSeconds(throttlingSleepField));
                        } catch (InterruptedException e) {
                        }
                    }
                }

                timestampCursor = nextTimestampCursor;

                if (sleepField != null) {
                    try {
                        Thread.sleep(TimestampUtils.convertToMilliSeconds(sleepField));
                    } catch (InterruptedException e) {
                    }
                }
            }
        } finally {
            if (streamWriter != null) {
                streamWriter.close();
            }
        }
    }

    private void fillJsonWithFieldValue(JSONObject json, String fieldName, Object val) {
        if (val instanceof String) {
            FieldRegexMatcherConverter fieldRegexMatcherConverter = fieldRegexMatcherMap.get(fieldName);
            if (fieldRegexMatcherConverter == null) {
                json.put(fieldName, val);
            } else {
                String newVal = fieldRegexMatcherConverter.replace((String) val);
                if (fieldRegexMatcherConverter.getOutputField() == null) {
                    json.put(fieldName, newVal);
                } else {
                    json.put(fieldName, val);
                    json.put(fieldRegexMatcherConverter.getOutputField(), newVal);
                }
            }
        } else {
            json.put(fieldName, val);
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
        destinationTableLatestTime = ConversionUtils.convertToLong(latestEpochTimeField);
        return timestampCursor - destinationTableLatestTime;
    }

    @Override
    protected boolean shouldReportDataReceived() {
        return true;
    }

    public static class FieldRegexMatcherConverter {
        private static final String PREFIX_PARAMETER_NAME = "field_";
        private static final String INPUT_FIELD_PARAMETER_NAME = "field_input:";
        private static final String OUTPUT_FIELD_PARAMETER_NAME = "field_output:";
        private static final String FIELD_REGEX_PARAMETER_NAME = "field_regex:";
        private static final String FIELD_CASE_PREFIX_PARAMETER_NAME = "field_case";
        private static final String FIELD_UPPER_CASE_PARAMETER_NAME = "field_case_upper";
        private static final String FIELD_LOWER_CASE_PARAMETER_NAME = "field_case_lower";

        private String inputField;
        private RegexMatcher fieldRegexMatcher;
        private String outputField = null;
        private boolean changeCase = false;
        private boolean upperCase = false;

        //The expected format is: field_input:<xxx> field_output:<yyy> field_regex:<zzz>
        public FieldRegexMatcherConverter(String confLine) {
            inputField = extractValue(confLine, INPUT_FIELD_PARAMETER_NAME);


            String clusterGroupsRegexString = extractValue(confLine, FIELD_REGEX_PARAMETER_NAME);
            String[][] configPatternsArray = ConfigurationUtils.getStringArrays(clusterGroupsRegexString);
            fieldRegexMatcher = new RegexMatcher(configPatternsArray);


            outputField = extractValue(confLine, OUTPUT_FIELD_PARAMETER_NAME);

            if (confLine.contains(FIELD_CASE_PREFIX_PARAMETER_NAME)) {
                if (confLine.contains(FIELD_UPPER_CASE_PARAMETER_NAME)) {
                    changeCase = true;
                    upperCase = true;
                } else if (confLine.contains(FIELD_LOWER_CASE_PARAMETER_NAME)) {
                    changeCase = true;
                }
            }
        }

        private String extractValue(String confLine, String paramName) {
            int fieldIndexStart = confLine.indexOf(paramName);
            if (fieldIndexStart == -1) {
                return null;
            }

            fieldIndexStart = fieldIndexStart + paramName.length();


            int fieldIndexEnd = confLine.indexOf(PREFIX_PARAMETER_NAME, fieldIndexStart);
            if (fieldIndexEnd == -1) {
                return confLine.substring(fieldIndexStart).trim();
            } else {
                return confLine.substring(fieldIndexStart, fieldIndexEnd).trim();
            }
        }

        public String replace(String val) {
            String ret = fieldRegexMatcher.replaceInPlace(val);

            if (changeCase) {
                if (upperCase) {
                    ret = ret.toUpperCase();
                } else {
                    ret = ret.toLowerCase();
                }
            }
            return ret;
        }

        public String getInputField() {
            return inputField;
        }

        public RegexMatcher getFieldRegexMatcher() {
            return fieldRegexMatcher;
        }

        public String getOutputField() {
            return outputField;
        }
    }
}
