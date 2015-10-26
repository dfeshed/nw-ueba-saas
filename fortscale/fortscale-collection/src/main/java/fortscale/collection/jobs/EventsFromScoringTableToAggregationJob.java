package fortscale.collection.jobs;

import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.kafka.KafkaEventsWriter;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.*;

import static fortscale.utils.ConversionUtils.convertToLong;

public class EventsFromScoringTableToAggregationJob extends ImpalaToKafka {

    private static Logger logger = Logger.getLogger(EventsFromScoringTableToAggregationJob.class);

    private static final long DELTA_TIME_IN_SECONDS = 3599; // 59 minutes and 59 seconds
    private static final int DEFAULT_BATCH_SIZE = 1000;
    private static final int FETCH_EVENTS_STEP_IN_MINUTES = 60; // 1 hour

    private Map<String, Map<String, String>> dataSourceToParameters;
    private int hoursToRun;
    private String dataSources;
    private DateTime startTime;
    private BatchToSend batchToSend;

    private void populateDataSourceToParametersMap(JobDataMap map, String securityDataSources)
            throws JobExecutionException {
        dataSourceToParameters = new HashMap();
        for (String dataSource: securityDataSources.split(",")) {
            Map<String, String> parameterMap = new HashMap();
            parameterMap.put(IMPALA_TABLE_NAME_JOB_PARAMETER, jobDataMapExtension.getJobDataMapStringValue(map,
                    IMPALA_TABLE_NAME_JOB_PARAMETER + "-" + dataSource));
            parameterMap.put(IMPALA_TABLE_FIELDS_JOB_PARAMETER, jobDataMapExtension.getJobDataMapStringValue(map,
                    IMPALA_TABLE_FIELDS_JOB_PARAMETER + "-" + dataSource));
            parameterMap.put(EPOCH_TIME_FIELD_JOB_PARAMETER, jobDataMapExtension.getJobDataMapStringValue(map,
                    EPOCH_TIME_FIELD_JOB_PARAMETER + "-" + dataSource));
            parameterMap.put(STREAMING_TOPIC_FIELD_JOB_PARAMETER, jobDataMapExtension.getJobDataMapStringValue(map,
                    STREAMING_TOPIC_FIELD_JOB_PARAMETER + "-" + dataSource));
            parameterMap.put(STREAMING_TOPIC_PARTITION_FIELDS_JOB_PARAMETER, jobDataMapExtension.
                    getJobDataMapStringValue(map, STREAMING_TOPIC_PARTITION_FIELDS_JOB_PARAMETER + "-" +
                            dataSource));
            parameterMap.put(IMPALA_TABLE_PARTITION_TYPE_JOB_PARAMETER, jobDataMapExtension.
                    getJobDataMapStringValue(map, IMPALA_TABLE_PARTITION_TYPE_JOB_PARAMETER + "-" + dataSource,
                            "daily"));
            dataSourceToParameters.put(dataSource, parameterMap);
        }
    }

    @Override
    protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap map = jobExecutionContext.getMergedJobDataMap();
        hoursToRun = jobDataMapExtension.getJobDataMapIntValue(map, "hoursToRun");
        dataSources = jobDataMapExtension.getJobDataMapStringValue(map, "securityDataSources");
        startTime = new DateTime(jobDataMapExtension.getJobDataMapLongValue(map, "startTime"));
        getGenericJobParameters(map);
        populateDataSourceToParametersMap(map, dataSources);
        batchToSend = new BatchToSend(jobDataMapExtension.getJobDataMapIntValue(map, "batchSize", DEFAULT_BATCH_SIZE));
    }

    @Override
    protected void runSteps() throws Exception {
        //run the forwarding job for every hour and for every data source
        for (int hour = 0; hour < hoursToRun; hour++) {
            DateTime endTime = startTime.plusHours(1).minusSeconds(1);
            long latestEventTime = endTime.getMillis() / 1000;
            for (String dataSource: dataSources.split(",")) {
                logger.info("running - {}, {}", dataSource, latestEventTime);
                runStep(dataSource, latestEventTime);
            }
            startTime = startTime.plusHours(1);
        }
        batchToSend.flushMessages();
        batchToSend.shutDown();
        finishStep();
    }

    private void runStep(String dataSource, long latestEventTime) throws Exception {
        Map<String, String> dataSourceParams = dataSourceToParameters.get(dataSource);
        try {
            String[] fieldsName = ImpalaParser.getTableFieldNamesAsArray(dataSourceParams.
                    get(IMPALA_TABLE_FIELDS_JOB_PARAMETER));
            long timestampCursor = latestEventTime - DELTA_TIME_IN_SECONDS;
            while (timestampCursor < latestEventTime) {
                long nextTimestampCursor = Math.min(latestEventTime, timestampCursor + FETCH_EVENTS_STEP_IN_MINUTES *
                        DateTimeConstants.SECONDS_PER_MINUTE);
                List<Map<String, Object>> resultsMap =  getDataFromImpala(nextTimestampCursor, latestEventTime,
                        timestampCursor, dataSourceParams.get(IMPALA_TABLE_NAME_JOB_PARAMETER),
                        dataSourceParams.get(EPOCH_TIME_FIELD_JOB_PARAMETER), whereCriteria,
                        dataSourceParams.get(IMPALA_TABLE_PARTITION_TYPE_JOB_PARAMETER));
                for (Map<String, Object> result : resultsMap) {
                    JSONObject json = new JSONObject();
                    for (String fieldName : fieldsName) {
                        Object val = result.get(fieldName.toLowerCase());
                        fillJsonWithFieldValue(json, fieldName, val);
                    }
                    batchToSend.send(dataSourceParams.get(STREAMING_TOPIC_FIELD_JOB_PARAMETER),
                            result.get(dataSourceParams.get(STREAMING_TOPIC_PARTITION_FIELDS_JOB_PARAMETER)).toString(),
                            json.toJSONString(JSONStyle.NO_COMPRESS),
                            convertToLong(result.get(dataSourceParams.get(EPOCH_TIME_FIELD_JOB_PARAMETER))));
                }
                timestampCursor = nextTimestampCursor;
            }
        } catch (Exception ex) {
            logger.error("failed to retrieve data from impala - {}", ex);
            throw new JobExecutionException();
        }
    }

    private class BatchToSend {

        private Queue<Message> messages;
        private int maxSize;
        private KafkaEventsWriter streamWriter;

        public BatchToSend(int maxSize) {
            messages = new LinkedList();
            streamWriter = new KafkaEventsWriter("");
            this.maxSize = maxSize;
        }

        public void shutDown() {
            try {
                streamWriter.close();
            } catch (Exception ex) {}
        }

        public void flushMessages() throws JobExecutionException {
            logger.info("flushing {} messages", messages.size());
            long latestEpochTimeSent = 0;
            int messagesSent = 0;
            for (Message message: messages) {
                logger.debug("sending message {} to topic {} - {} ", messagesSent, message.topic, message.messageString);
                streamWriter.setTopic(message.topic);
                try {
                    streamWriter.send(message.partitionKey, message.messageString);
                } catch (Exception ex) {
                    logger.error("failed to send message to topic {}", message.topic);
                    throw new JobExecutionException(ex);
                }
                logger.debug("{} messages sent", messagesSent++);
                latestEpochTimeSent = message.epochTime;
            }
            if (latestEpochTimeSent > 0) {
                logger.info("{} messages sent, waiting for last message time {}", messagesSent, latestEpochTimeSent);
                listenToMetrics(latestEpochTimeSent);
            }
            logger.info("finished flushing, clearing queue");
            messages.clear();
        }

        public void send(String topic, String partitionKey, String messageStr, long epochTime)
                throws JobExecutionException {
            messages.add(new Message(topic, messageStr, partitionKey, epochTime));
            if (messages.size() < maxSize) {
                return;
            }
            flushMessages();
        }

        private class Message {

            private String topic;
            private String messageString;
            private String partitionKey;
            private long epochTime;

            private Message(String topic, String messageString, String partitionKey, long epochTime) {
                this.topic = topic;
                this.messageString = messageString;
                this.partitionKey = partitionKey;
                this.epochTime = epochTime;
            }

        }

    }

}