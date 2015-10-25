package fortscale.collection.jobs;

import fortscale.utils.hdfs.partition.PartitionStrategy;
import fortscale.utils.hdfs.partition.PartitionsUtils;
import fortscale.utils.impala.ImpalaPageRequest;
import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.impala.ImpalaQuery;
import fortscale.utils.kafka.KafkaEventsWriter;
import fortscale.utils.kafka.TopicReader;
import fortscale.utils.logging.Logger;
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
import java.util.*;

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
    private static final int DEFAULT_BATCH_SIZE = 1000;

    private static final int MILLISECONDS_TO_WAIT = 1000 * 60;

    //define how much time to subtract from the latest event time - this way to get the first event time to send
    @Value("${batch.sendTo.kafka.events.delta.time.sec:3599}")
    protected long eventsDeltaTimeInSec;

    @Value("${broker.list}")
    private String zookeeperConnection;

    @Autowired
    private JdbcOperations impalaJdbcTemplate;

    private String whereCriteria;
    private long deltaTimeInSec;
    private int fetchEventsStepInMinutes;
    private Map<String, FieldRegexMatcherConverter> fieldRegexMatcherMap = new HashMap();
    private int checkRetries;
    private String jobToMonitor;
    private String jobClassToMonitor;
    private Map<String, Map<String, String>> dataSourceToParameters;
    private int hoursToRun;
    private String securityDataSources;
    private DateTime startTime;
    private BatchToSend batchToSend;

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
        securityDataSources = jobDataMapExtension.getJobDataMapStringValue(map, "securityDataSources");
        startTime = new DateTime(jobDataMapExtension.getJobDataMapLongValue(map, "startTime"));
        whereCriteria = jobDataMapExtension.getJobDataMapStringValue(map, "where", null);
        deltaTimeInSec = jobDataMapExtension.getJobDataMapLongValue(map, "deltaTimeInSec", eventsDeltaTimeInSec);
        fetchEventsStepInMinutes = jobDataMapExtension.getJobDataMapIntValue(map, "fetchEventsStepInMinutes",
                FETCH_EVENTS_STEP_IN_MINUTES_DEFAULT);
        checkRetries = jobDataMapExtension.getJobDataMapIntValue(map, "retries", DEFAULT_CHECK_RETRIES);
        jobToMonitor = jobDataMapExtension.getJobDataMapStringValue(map, "jobmonitor");
        jobClassToMonitor = jobDataMapExtension.getJobDataMapStringValue(map, "classmonitor");
        populateDataSourceToParametersMap(map, securityDataSources);
        batchToSend = new BatchToSend(jobDataMapExtension.getJobDataMapIntValue(map, "batchSize", DEFAULT_BATCH_SIZE));
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
            long latestEventTime = endTime.getMillis() / 1000;
            for (String securityDataSource: securityDataSources.split(",")) {
                logger.info("running - {}, {}", securityDataSource, latestEventTime);
                runStep(securityDataSource, latestEventTime);
            }
            startTime = startTime.plusHours(1);
        }
        batchToSend.flushMessages();
        finishStep();
    }

    private void runStep(String securityDataSource, long latestEventTime) throws Exception {
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
                logger.debug("found {} records", resultsMap.size());
                for (Map<String, Object> result : resultsMap) {
                    JSONObject json = new JSONObject();
                    for (String fieldName : fieldsName) {
                        Object val = result.get(fieldName.toLowerCase());
                        fillJsonWithFieldValue(json, fieldName, val);
                    }
                    batchToSend.send(streamWriter,
                            result.get(dataSourceParams.get(STREAMING_TOPIC_PARTITION_FIELDS_JOB_PARAMETER)).toString(),
                            json.toJSONString(JSONStyle.NO_COMPRESS),
                            convertToLong(result.get(dataSourceParams.get(EPOCH_TIME_FIELD_JOB_PARAMETER))));
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

    private class BatchToSend {

        private Queue<Message> messages;
        private int maxSize;

        public BatchToSend(int maxSize) {
            messages = new LinkedList();
            this.maxSize = maxSize;
        }

        public void flushMessages() throws JobExecutionException {
            long latestEpochTimeSent = 0;
            for (Message message: messages) {
                latestEpochTimeSent = message.getEpochTime();
                message.getStreamWriter().send(message.getPartitionKey(), message.getMessageString());
            }
            if (latestEpochTimeSent > 0) {
                logger.info("throttling by last message metrics on job {}", jobToMonitor);
                boolean result = TopicReader.waitForMetrics(zookeeperConnection.split(":")[0],
                        Integer.parseInt(zookeeperConnection.split(":")[1]), jobClassToMonitor, jobToMonitor,
                        String.format("%s-last-message-epochtime", jobToMonitor), latestEpochTimeSent,
                        MILLISECONDS_TO_WAIT, checkRetries);
                if (result == true) {
                    logger.info("last message in batch processed, moving to next batch");
                } else {
                    logger.error("last message not processed - timed out!");
                    throw new JobExecutionException();
                }
                messages.clear();
            }
        }

        public void send(KafkaEventsWriter streamWriter, String partitionKey, String messageStr, long epochTime)
                throws JobExecutionException {
            if (messages.size() < maxSize) {
                messages.add(new Message(streamWriter, messageStr, partitionKey, epochTime));
                return;
            }
            flushMessages();
        }

        private class Message {

            private KafkaEventsWriter streamWriter;
            private String messageString;
            private String partitionKey;
            private long epochTime;

            private Message(KafkaEventsWriter streamWriter, String messageString, String partitionKey, long epochTime) {
                this.streamWriter = streamWriter;
                this.messageString = messageString;
                this.partitionKey = partitionKey;
                this.epochTime = epochTime;
            }

            public KafkaEventsWriter getStreamWriter() {
                return streamWriter;
            }

            public String getMessageString() {
                return messageString;
            }

            public String getPartitionKey() {
                return partitionKey;
            }

            public long getEpochTime() {
                return epochTime;
            }

        }

    }

}