package fortscale.collection.jobs;

import static fortscale.utils.impala.ImpalaCriteria.gte;
import static fortscale.utils.impala.ImpalaCriteria.lt;
import static fortscale.utils.impala.ImpalaCriteria.lte;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTimeConstants;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcOperations;

import fortscale.collection.io.KafkaEventsWriter;
import fortscale.services.impl.RegexMatcher;
import fortscale.utils.ConfigurationUtils;
import fortscale.utils.TimestampUtils;
import fortscale.utils.impala.ImpalaPageRequest;
import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.impala.ImpalaQuery;
import fortscale.utils.logging.Logger;




@DisallowConcurrentExecution
public class EventsFromDataTableToStreamingJob extends FortscaleJob {
	private static Logger logger = Logger.getLogger(EventsFromDataTableToStreamingJob.class);
	
	private static int EVENTS_DELTA_TIME_IN_SEC_DEFAULT = 14*24*60*60;
	private static int FETCH_EVENTS_STEP_IN_DAYS_DEFAULT = 1;
	private static final String IMPALA_TABLE_NAME_JOB_PARAMETER = "impalaTableName";
	private static final String IMPALA_TABLE_FIELDS_JOB_PARAMETER = "impalaTableFields";
	private static final String LATEST_EVENT_TIME_JOB_PARAMETER = "latestEventTime";
	private static final String DELTA_TIME_IN_SEC_JOB_PARAMETER = "deltaTimeInSec";
	private static final String EPOCH_TIME_FIELD_JOB_PARAMETER = "epochtimeField";
	private static final String STREAMING_TOPIC_FIELD_JOB_PARAMETER = "streamingTopic";
	private static final String FETCH_EVENTS_STEP_IN_DAYS_JOB_PARAMETER = "fetchEventsStepInDays";
	private static final String FIELD_CLUSTER_GROUPS_REGEX_RESOURCE_JOB_PARAMETER = "fieldClusterGroupsRegexResource";
	
	@Autowired
	private JdbcOperations impalaJdbcTemplate;	
	
	//parameters:
	private String impalaTableName;
	private String impalaTableFields;
	private String epochtimeField;
	private String streamingTopic;	
	private long latestEventTime;
	private long deltaTimeInSec;
	private int fetchEventsStepInDays;
	private Map<String, FieldRegexMatcherConverter> fieldRegexMatcherMap = new HashMap<String, FieldRegexMatcherConverter>();
	
	
	protected String getTableName(){
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
		
		
		latestEventTime = jobDataMapExtension.getJobDataMapLongValue(map, LATEST_EVENT_TIME_JOB_PARAMETER, System.currentTimeMillis());
		latestEventTime = TimestampUtils.convertToSeconds(latestEventTime);

		deltaTimeInSec = jobDataMapExtension.getJobDataMapLongValue(map, DELTA_TIME_IN_SEC_JOB_PARAMETER, (long)EVENTS_DELTA_TIME_IN_SEC_DEFAULT);
		
		fetchEventsStepInDays = jobDataMapExtension.getJobDataMapIntValue(map, FETCH_EVENTS_STEP_IN_DAYS_JOB_PARAMETER, FETCH_EVENTS_STEP_IN_DAYS_DEFAULT);
		if(map.containsKey(FIELD_CLUSTER_GROUPS_REGEX_RESOURCE_JOB_PARAMETER)){
			Resource fieldClusterGroupsRegexResource = jobDataMapExtension.getJobDataMapResourceValue(map, FIELD_CLUSTER_GROUPS_REGEX_RESOURCE_JOB_PARAMETER);
			try{
				fillRegexMatcherMap(fieldClusterGroupsRegexResource.getFile());
			} catch(Exception e){
				logger.error("Got an exception while calling get file of resource {}", fieldClusterGroupsRegexResource.getFilename());
			}
		}
	}
	
	private void fillRegexMatcherMap(File f){
		try{
			if (f.exists() && f.isFile()) {
				ArrayList<String> fieldConfList = new ArrayList<String>(FileUtils.readLines(f));
				for (String fieldConf : fieldConfList) {
					FieldRegexMatcherConverter fieldRegexMatcherConverter = new FieldRegexMatcherConverter(fieldConf);
					fieldRegexMatcherMap.put(fieldRegexMatcherConverter.getInputField(), fieldRegexMatcherConverter);
				}
			}
		} catch(Exception e){
			logger.error("Got an exception while loading the regex file", e);
		}
	}
	
	@Override
	protected int getTotalNumOfSteps(){
		return 1;
	}
		
	@Override
	protected void runSteps() throws Exception{
		KafkaEventsWriter streamWriter = null;
		
		try{
			String[] fieldsName = ImpalaParser.getTableFieldNamesAsArray(impalaTableFields);
			streamWriter = new KafkaEventsWriter(streamingTopic);
			long timestampCursor = latestEventTime - deltaTimeInSec;
			while(timestampCursor < latestEventTime){
				long nextTimestampCursor = Math.min(latestEventTime, timestampCursor + fetchEventsStepInDays * DateTimeConstants.SECONDS_PER_DAY);
				ImpalaQuery query = new ImpalaQuery();
				query.select("count(*)").from(impalaTableName);
				query.andWhere(gte(epochtimeField, Long.toString(timestampCursor)));
				if (nextTimestampCursor==latestEventTime)
					query.andWhere(lte(epochtimeField, Long.toString(nextTimestampCursor)));
				else
					query.andWhere(lt(epochtimeField, Long.toString(nextTimestampCursor)));
				int limit = impalaJdbcTemplate.queryForObject(query.toSQL(), Integer.class);
				query.select("*");
				query.limitAndSort(new ImpalaPageRequest(limit, new Sort(Direction.ASC, epochtimeField)));
				
				List<Map<String, Object>> resultsMap = impalaJdbcTemplate.query(query.toSQL(), new ColumnMapRowMapper());
				
				for(Map<String, Object> result: resultsMap){
					JSONObject json = new JSONObject();
					for(String fieldName: fieldsName){
						Object val = result.get(fieldName.toLowerCase());
						fillJsonWithFieldValue(json, fieldName, val);
					}
					streamWriter.send(json.toJSONString(JSONStyle.NO_COMPRESS));
				}
				
				monitorDataReceived(query.toSQL(), resultsMap.size(), "Events");
				
				timestampCursor = nextTimestampCursor;
			}
		} finally{
			if(streamWriter != null){
				streamWriter.close();
			}
		}		
	}
	
	private void fillJsonWithFieldValue(JSONObject json, String fieldName, Object val){
		if(val instanceof String){
			FieldRegexMatcherConverter fieldRegexMatcherConverter = fieldRegexMatcherMap.get(fieldName);
			if(fieldRegexMatcherConverter == null){
				json.put(fieldName, val);
			} else{
				String newVal = fieldRegexMatcherConverter.replace((String) val);
				if(fieldRegexMatcherConverter.getOutputField() == null){
					json.put(fieldName, newVal);
				} else{
					json.put(fieldName, val);
					json.put(fieldRegexMatcherConverter.getOutputField(), newVal);
				}
			}
		} else{
			json.put(fieldName, val);
		}
	}
		
	@Override
	protected boolean shouldReportDataReceived() {
		return true;
	}
	
	public static class FieldRegexMatcherConverter{
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
		public FieldRegexMatcherConverter(String confLine){
			inputField = extractValue(confLine, INPUT_FIELD_PARAMETER_NAME);
			
			
			String clusterGroupsRegexString = extractValue(confLine, FIELD_REGEX_PARAMETER_NAME);
			String[][] configPatternsArray = ConfigurationUtils.getStringArrays(clusterGroupsRegexString);
			fieldRegexMatcher = new RegexMatcher(configPatternsArray);
			
			
			outputField = extractValue(confLine, OUTPUT_FIELD_PARAMETER_NAME);			
			
			if(confLine.contains(FIELD_CASE_PREFIX_PARAMETER_NAME)){
				if(confLine.contains(FIELD_UPPER_CASE_PARAMETER_NAME)){
					changeCase = true;
					upperCase = true;
				} else if(confLine.contains(FIELD_LOWER_CASE_PARAMETER_NAME)){
					changeCase = true;
				}
			}
		}
		
		private String extractValue(String confLine, String paramName){
			int fieldIndexStart = confLine.indexOf(paramName);
			if(fieldIndexStart == -1){
				return null;
			}
			
			fieldIndexStart = fieldIndexStart + paramName.length();
			
			
			int fieldIndexEnd = confLine.indexOf(PREFIX_PARAMETER_NAME, fieldIndexStart);
			if(fieldIndexEnd == -1){
				return confLine.substring(fieldIndexStart).trim();
			} else{
				return confLine.substring(fieldIndexStart, fieldIndexEnd).trim();
			}
		}
		
		public String replace(String val){
			String ret = fieldRegexMatcher.replaceInPlace(val);
			
			if(changeCase){
				if(upperCase){
					ret = ret.toUpperCase();
				} else{
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