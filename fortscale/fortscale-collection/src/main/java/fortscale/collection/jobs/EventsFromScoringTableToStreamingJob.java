package fortscale.collection.jobs;

import fortscale.utils.hdfs.partition.PartitionStrategy;
import fortscale.utils.hdfs.partition.PartitionsUtils;
import fortscale.utils.impala.ImpalaPageRequest;
import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.impala.ImpalaQuery;
import fortscale.utils.kafka.KafkaEventsWriter;
import fortscale.utils.kafka.TopicConsumer;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimestampUtils;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcOperations;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fortscale.collection.jobs.EventsFromDataTableToStreamingJob.FieldRegexMatcherConverter;
import static fortscale.utils.ConversionUtils.convertToLong;
import static fortscale.utils.impala.ImpalaCriteria.*;

public class EventsFromScoringTableToStreamingJob extends FortscaleJob {

    private static Logger logger = Logger.getLogger(EventsFromScoringTableToStreamingJob.class);

    private static final String IMPALA_TABLE_NAME_JOB_PARAMETER = "impalaTableName";
    private static final String IMPALA_TABLE_FIELDS_JOB_PARAMETER = "impalaTableFields";
    private static final String EPOCH_TIME_FIELD_JOB_PARAMETER = "epochtimeField";
    private static final String STREAMING_TOPIC_FIELD_JOB_PARAMETER = "streamingTopic";
    private static final String STREAMING_TOPIC_PARTITION_FIELDS_JOB_PARAMETER = "streamingTopicPartitionKey";
    private static final String IMPALA_TABLE_PARTITION_TYPE_JOB_PARAMETER = "impalaTablePartitionType";

    private static final int FETCH_EVENTS_STEP_IN_MINUTES_DEFAULT = 60; // 1 hour
    private static final int DEFAULT_CHECK_RETRIES = 60;
    private static final int MILLISECONDS_TO_WAIT = 1000 * 60;

    //define how much time to subtract from now to get the last event time to send to streaming job
    //default of 3 hours - 60 * 60 * 3
    @Value("${batch.sendTo.kafka.latest.events.time.diff.sec:10800}")
    protected long latestEventsTimeDiffFromNowInSec;

    //define how much time to subtract from the latest event time - this way to get the first event time to send
    @Value("${batch.sendTo.kafka.events.delta.time.sec:3600}")
    protected long eventsDeltaTimeInSec;

    @Value("${zookeeper.connection}")
    private String zookeeperConnection;
    @Value("${zookeeper.timeout}")
    private int zookeeperTimeout;
    @Value("${zookeeper.group}")
    private String zookeeperGroup;

    @Autowired
    private JdbcOperations impalaJdbcTemplate;

    private String whereCriteria;
    private long latestEventTime;
    private long deltaTimeInSec;
    private int fetchEventsStepInMinutes;
    private Map<String, FieldRegexMatcherConverter> fieldRegexMatcherMap = new HashMap();
    private int checkRetries;
    private String jobToMonitor;
    private String jobClassToMonitor;
    private Map<String, Map<String, String>> dataSourceToParameters;

    private int hoursToRun;
    private String batchSizeInMinutes;
    private String securityDataSources;
    private DateTime startTime;

    private void populateDataSourceToParametersMap(JobDataMap map, String securityDataSources)
            throws JobExecutionException {
        dataSourceToParameters = new HashMap();
        for (String securityDataSource: securityDataSources.split(",")) {
            Map<String, String> parameterMap = new HashMap();
            parameterMap.put(IMPALA_TABLE_NAME_JOB_PARAMETER, jobDataMapExtension.getJobDataMapStringValue(map,
                    IMPALA_TABLE_NAME_JOB_PARAMETER + "-" + securityDataSource));
            parameterMap.put(IMPALA_TABLE_FIELDS_JOB_PARAMETER, jobDataMapExtension.getJobDataMapStringValue(map,
                    IMPALA_TABLE_FIELDS_JOB_PARAMETER + "-" + securityDataSource));
            parameterMap.put(EPOCH_TIME_FIELD_JOB_PARAMETER, jobDataMapExtension.getJobDataMapStringValue(map,
                    EPOCH_TIME_FIELD_JOB_PARAMETER + "-" + securityDataSource));
            parameterMap.put(STREAMING_TOPIC_FIELD_JOB_PARAMETER, jobDataMapExtension.getJobDataMapStringValue(map,
                    STREAMING_TOPIC_FIELD_JOB_PARAMETER + "-" + securityDataSource));
            parameterMap.put(STREAMING_TOPIC_PARTITION_FIELDS_JOB_PARAMETER, jobDataMapExtension.
                    getJobDataMapStringValue(map, STREAMING_TOPIC_PARTITION_FIELDS_JOB_PARAMETER + "-" +
                            securityDataSource));
            parameterMap.put(IMPALA_TABLE_PARTITION_TYPE_JOB_PARAMETER, jobDataMapExtension.
                    getJobDataMapStringValue(map, IMPALA_TABLE_PARTITION_TYPE_JOB_PARAMETER + "-" + securityDataSource,
                            "daily"));
            dataSourceToParameters.put(securityDataSource, parameterMap);
        }
    }

    @Override
    protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap map = jobExecutionContext.getMergedJobDataMap();

        hoursToRun = jobDataMapExtension.getJobDataMapIntValue(map, "hoursToRun");
        batchSizeInMinutes = jobDataMapExtension.getJobDataMapStringValue(map, "batchSizeInMinutes");
        securityDataSources = jobDataMapExtension.getJobDataMapStringValue(map, "securityDataSources");
        startTime = new DateTime(jobDataMapExtension.getJobDataMapLongValue(map, "startTime"));

        whereCriteria = jobDataMapExtension.getJobDataMapStringValue(map, "where", null);
        latestEventTime = jobDataMapExtension.getJobDataMapLongValue(map, "latestEventTime",
                (TimestampUtils.convertToSeconds(System.currentTimeMillis()) - latestEventsTimeDiffFromNowInSec));
        deltaTimeInSec = jobDataMapExtension.getJobDataMapLongValue(map, "deltaTimeInSec", eventsDeltaTimeInSec);
        fetchEventsStepInMinutes = jobDataMapExtension.getJobDataMapIntValue(map, "fetchEventsStepInMinutes",
                FETCH_EVENTS_STEP_IN_MINUTES_DEFAULT);
        checkRetries = jobDataMapExtension.getJobDataMapIntValue(map, "retries", DEFAULT_CHECK_RETRIES);
        jobToMonitor = jobDataMapExtension.getJobDataMapStringValue(map, "jobmonitor");
        jobClassToMonitor = jobDataMapExtension.getJobDataMapStringValue(map, "classmonitor");

        populateDataSourceToParametersMap(map, securityDataSources);

    }

    @Override
    protected int getTotalNumOfSteps() {
        return 1;
    }

    @Override
    protected void runSteps() throws Exception {
        //run the forwarding job for every hour and for every data source
        for (int hour = 0; hour < hoursToRun; hour++) {
            DateTime endTime = startTime.plusHours(1).minusSeconds(1);
            String latestEventTime = "latestEventTime=" + (endTime.getMillis() / 1000);
            String deltaInSec = "deltaTimeInSec=3599";
            List<String> args = new ArrayList();
            args.add(latestEventTime);
            args.add(deltaInSec);
            args.add("fetchEventsStepInMinutes=" + batchSizeInMinutes);
            for (String securityDataSource: securityDataSources.split(",")) {
                logger.info("running - {}, {}", securityDataSource, args);
                runStep(securityDataSource);
            }
            startTime = startTime.plusHours(1);
        }
        finishStep();
    }

    private void runStep(String securityDataSource) throws Exception {
        KafkaEventsWriter streamWriter = null;
        Map<String, String> dataSourceParams = dataSourceToParameters.get(securityDataSource);
        try {
            String[] fieldsName = ImpalaParser.getTableFieldNamesAsArray(dataSourceParams.
                    get(IMPALA_TABLE_FIELDS_JOB_PARAMETER));
            streamWriter = new KafkaEventsWriter(dataSourceParams.get(STREAMING_TOPIC_FIELD_JOB_PARAMETER));
            long timestampCursor = latestEventTime - deltaTimeInSec;
            while (timestampCursor < latestEventTime) {
                long nextTimestampCursor = Math.min(latestEventTime, timestampCursor + fetchEventsStepInMinutes *
                        DateTimeConstants.SECONDS_PER_MINUTE);
                ImpalaQuery query = new ImpalaQuery();
                query.select("count(*)").from(dataSourceParams.get(IMPALA_TABLE_NAME_JOB_PARAMETER));
                query.andWhere(gte(dataSourceParams.get(EPOCH_TIME_FIELD_JOB_PARAMETER),
                        Long.toString(timestampCursor)));
                if (StringUtils.isNotBlank(whereCriteria))
                    query.andWhere(whereCriteria);
                addPartitionFilterToQuery(query, timestampCursor, nextTimestampCursor,
                        dataSourceParams.get(IMPALA_TABLE_PARTITION_TYPE_JOB_PARAMETER));
                if (nextTimestampCursor == latestEventTime)
                    query.andWhere(lte(dataSourceParams.get(EPOCH_TIME_FIELD_JOB_PARAMETER),
                            Long.toString(nextTimestampCursor)));
                else
                    query.andWhere(lt(dataSourceParams.get(EPOCH_TIME_FIELD_JOB_PARAMETER),
                            Long.toString(nextTimestampCursor)));
                logger.debug("query is {}", query.toSQL());
                int limit = impalaJdbcTemplate.queryForObject(query.toSQL(), Integer.class);
                query.select("*");
                query.limitAndSort(new ImpalaPageRequest(limit, new Sort(Sort.Direction.ASC,
                        dataSourceParams.get(EPOCH_TIME_FIELD_JOB_PARAMETER))));
                List<Map<String, Object>> resultsMap = impalaJdbcTemplate.query(query.toSQL(),
                        new ColumnMapRowMapper());
                long latestEpochTimeSent = 0;
                logger.debug("found {} records", resultsMap.size());
                for (Map<String, Object> result : resultsMap) {
                    JSONObject json = new JSONObject();
                    for (String fieldName : fieldsName) {
                        Object val = result.get(fieldName.toLowerCase());
                        fillJsonWithFieldValue(json, fieldName, val);
                    }
                    streamWriter.send(result.get(dataSourceParams.get(STREAMING_TOPIC_PARTITION_FIELDS_JOB_PARAMETER)).
                            toString(), json.toJSONString(JSONStyle.NO_COMPRESS));
                    long currentEpochTimeField = convertToLong(result.get(dataSourceParams.
                            get(EPOCH_TIME_FIELD_JOB_PARAMETER)));
                    if (latestEpochTimeSent < currentEpochTimeField) {
                        latestEpochTimeSent = currentEpochTimeField;
                    }
                }
                if (resultsMap.size() > 0) {
                    monitorDataReceived(query.toSQL(), resultsMap.size(), "Events");
                    logger.debug("throttling by last message metrics");
                    int currentTry = 0;
                    while (currentTry < checkRetries) {
                        logger.debug("try number {}, checking task {}", currentTry, jobToMonitor);
                        TopicConsumer topicConsumer = new TopicConsumer(zookeeperConnection, zookeeperGroup, "metrics");
                        Long time = convertToLong(topicConsumer.readSamzaMetric(jobToMonitor, jobClassToMonitor,
                                String.format("%s-last-message-epochtime", jobToMonitor)));
                        if (time != null && time == latestEpochTimeSent) {
                            logger.debug("last message in batch processed, moving to next batch");
                            break;
                        }
                        logger.debug("last message not yet processed, waiting {} milliseconds...");
                        Thread.sleep(MILLISECONDS_TO_WAIT);
                    }
                    if (currentTry >= checkRetries) {
                        logger.error("did not receive last message time {} in task {} - breaking", latestEpochTimeSent,
                                jobToMonitor);
                        break;
                    }
                }
                timestampCursor = nextTimestampCursor;
            }
        } finally {
            if (streamWriter != null) {
                streamWriter.close();
            }
        }
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
        } else if(val instanceof Timestamp){
            json.put(fieldName, val.toString());
        } else{
            json.put(fieldName, val);
        }
    }

    @Override
    protected boolean shouldReportDataReceived() {
        return true;
    }

}