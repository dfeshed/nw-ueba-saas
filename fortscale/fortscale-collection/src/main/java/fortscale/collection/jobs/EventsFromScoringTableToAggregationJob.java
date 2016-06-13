package fortscale.collection.jobs;

import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.kafka.KafkaSender;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fortscale.utils.ConversionUtils.convertToLong;

public class EventsFromScoringTableToAggregationJob extends ImpalaToKafka {

    private static Logger logger = Logger.getLogger(EventsFromScoringTableToAggregationJob.class);

    private static final long DELTA_TIME_IN_SECONDS = 3599; // 59 minutes and 59 seconds
    private static final int DEFAULT_BATCH_SIZE = 1000;
    private static final int FETCH_EVENTS_STEP_IN_MINUTES = 60; // 1 hour
    private static final String DATA_SOURCE_FIELD = "data_source";

    private Map<String, Map<String, String>> dataSourceToParameters;
    private int hoursToRun;
    private String dataSources;
    private DateTime startTime;
    private KafkaSender kafkaSender;

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
            parameterMap.put(IMPALA_TABLE_PARTITION_TYPE_JOB_PARAMETER, jobDataMapExtension.
                    getJobDataMapStringValue(map, IMPALA_TABLE_PARTITION_TYPE_JOB_PARAMETER + "-" + dataSource,
                            IMPALA_TABLE_PARTITION_TYPE_DEFAULT));
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
        kafkaSender = new KafkaSender(metricsKafkaSynchronizer,
                jobDataMapExtension.getJobDataMapIntValue(map, "batchSize", DEFAULT_BATCH_SIZE),
                jobDataMapExtension.getJobDataMapStringValue(map, STREAMING_TOPIC_FIELD_JOB_PARAMETER),
                jobDataMapExtension.getJobDataMapStringValue(map, STREAMING_TOPIC_PARTITION_FIELDS_JOB_PARAMETER));
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
        kafkaSender.shutDown();
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
                    json.put(DATA_SOURCE_FIELD, dataSource);
                    kafkaSender.send(json.toJSONString(JSONStyle.NO_COMPRESS),
                            convertToLong(result.get(dataSourceParams.get(EPOCH_TIME_FIELD_JOB_PARAMETER))));
                }
                timestampCursor = nextTimestampCursor;
            }
        } catch (Exception ex) {
            logger.error("failed to retrieve data from impala - {}", ex);
            throw new JobExecutionException();
        }
    }

    @Override public boolean synchronize(long latestEpochTimeSent) {
        return metricsKafkaSynchronizer.synchronize(latestEpochTimeSent);
    }
}

