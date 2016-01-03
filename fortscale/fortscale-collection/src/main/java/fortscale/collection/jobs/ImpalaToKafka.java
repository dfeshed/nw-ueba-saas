package fortscale.collection.jobs;

import fortscale.utils.kafka.IKafkaSynchronizer;
import fortscale.utils.kafka.MetricsKafkaSynchronizer;
import fortscale.services.impl.RegexMatcher;
import fortscale.utils.ConfigurationUtils;
import fortscale.utils.hdfs.partition.PartitionStrategy;
import fortscale.utils.hdfs.partition.PartitionsUtils;
import fortscale.utils.impala.ImpalaPageRequest;
import fortscale.utils.impala.ImpalaQuery;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcOperations;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fortscale.utils.impala.ImpalaCriteria.*;

public abstract class ImpalaToKafka extends FortscaleJob implements IKafkaSynchronizer {

    private static Logger logger = Logger.getLogger(ImpalaToKafka.class);

    protected static final int DEFAULT_CHECK_RETRIES = 60;
    protected static final int MILLISECONDS_TO_WAIT = 1000 * 60;

    protected static final String IMPALA_TABLE_NAME_JOB_PARAMETER = "impalaTableName";
    protected static final String IMPALA_TABLE_FIELDS_JOB_PARAMETER = "impalaTableFields";
    protected static final String EPOCH_TIME_FIELD_JOB_PARAMETER = "epochtimeField";
    protected static final String STREAMING_TOPIC_FIELD_JOB_PARAMETER = "streamingTopic";
    protected static final String STREAMING_TOPIC_PARTITION_FIELDS_JOB_PARAMETER = "streamingTopicPartitionKey";
    protected static final String IMPALA_TABLE_PARTITION_TYPE_JOB_PARAMETER = "impalaTablePartitionType";
    protected static final String WHERE_CRITERIA_FIELD_JOB_PARAMETER = "where";
    protected static final String JOB_MONITOR_PARAMETER = "jobmonitor";
    protected static final String CLASS_MONITOR_PARAMETER = "classmonitor";
    protected static final String RETRIES_PARAMETER = "retries";
    protected static final String IMPALA_TABLE_PARTITION_TYPE_DEFAULT = "daily";



    @Autowired
    protected JdbcOperations impalaJdbcTemplate;

    //parameters:
    protected Map<String, FieldRegexMatcherConverter> fieldRegexMatcherMap = new HashMap();
    protected int checkRetries;
    protected String whereCriteria;
    protected String jobToMonitor;
    protected MetricsKafkaSynchronizer metricsKafkaSynchronizer;

    protected void getGenericJobParameters(JobDataMap map)
            throws JobExecutionException {
        whereCriteria = jobDataMapExtension.getJobDataMapStringValue(map, WHERE_CRITERIA_FIELD_JOB_PARAMETER, null);
        checkRetries = jobDataMapExtension.getJobDataMapIntValue(map, RETRIES_PARAMETER, DEFAULT_CHECK_RETRIES);
        jobToMonitor = jobDataMapExtension.getJobDataMapStringValue(map, JOB_MONITOR_PARAMETER);
        if (map.containsKey(JOB_MONITOR_PARAMETER)) {
            metricsKafkaSynchronizer = new MetricsKafkaSynchronizer(
                    jobDataMapExtension.getJobDataMapStringValue(map, CLASS_MONITOR_PARAMETER),
                    jobToMonitor,
                    MILLISECONDS_TO_WAIT, checkRetries);
        }
        else {
            metricsKafkaSynchronizer = new MetricsKafkaSynchronizer();
        }
    }

    protected List<Map<String, Object>> getDataFromImpala(long nextTimestampCursor, long latestEventTime,
                                                          long timestampCursor, String impalaTableName,
                                                          String epochtimeField, String whereCriteria,
                                                          String impalaTablePartitionType) {
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
        query.limitAndSort(new ImpalaPageRequest(limit, new Sort(Sort.Direction.ASC, epochtimeField)));
        List<Map<String, Object>> resultsMap = impalaJdbcTemplate.query(query.toSQL(), new ColumnMapRowMapper());
        monitorDataReceived(query.toSQL(), resultsMap.size(), "Events");
        logger.debug("found {} records", resultsMap.size());
        return resultsMap;
    }

    @Override
    protected int getTotalNumOfSteps() {
        return 1;
    }

    @Override
    public String getStepName() {
        return "EventsFromTableToStreaming";
    }

    protected void addPartitionFilterToQuery(ImpalaQuery query, long earliestTime, long latestTime,
                                             String partitionType) {
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

    protected void fillJsonWithFieldValue(JSONObject json, String fieldName, Object val) {
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