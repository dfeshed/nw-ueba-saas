package fortscale.collection.jobs;

import static fortscale.utils.impala.ImpalaCriteria.gte;
import static fortscale.utils.impala.ImpalaCriteria.lt;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;

import org.joda.time.DateTimeConstants;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcOperations;

import fortscale.collection.io.KafkaEventsWriter;
import fortscale.utils.TimestampUtils;
import fortscale.utils.impala.ImpalaQuery;




@DisallowConcurrentExecution
public class EventsFromDataTableToStreamingJob extends FortscaleJob {
	
	private static int EVENTS_DELTA_TIME_IN_SEC_DEFAULT = 14*24*60*60;
	private static int FETCH_EVENTS_STEP_IN_DAYS_DEFAULT = 1;
	private static final String IMPALA_TABLE_NAME_JOB_PARAMETER = "impalaTableName";
	private static final String LATEST_EVENT_TIME_JOB_PARAMETER = "latestEventTime";
	private static final String DELTA_TIME_IN_SEC_JOB_PARAMETER = "deltaTimeInSec";
	private static final String EPOCH_TIME_FIELD_JOB_PARAMETER = "epochtimeField";
	private static final String STREAMING_TOPIC_FIELD_JOB_PARAMETER = "streamingTopic";
	private static final String FETCH_EVENTS_STEP_IN_DAYS_JOB_PARAMETER = "fetchEventsStepInDays";
	
	@Autowired
	private JdbcOperations impalaJdbcTemplate;	
	
	//parameters:
	private String impalaTableName;
	private String epochtimeField;
	private String streamingTopic;	
	private long latestEventTime;
	private long deltaTimeInSec;
	private int fetchEventsStepInDays;
	
	
	protected String getTableName(){
		return impalaTableName;
	}
			
	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobDataMap map = jobExecutionContext.getMergedJobDataMap();

		// get parameters values from the job data map
		
		impalaTableName = jobDataMapExtension.getJobDataMapStringValue(map, IMPALA_TABLE_NAME_JOB_PARAMETER);
		epochtimeField = jobDataMapExtension.getJobDataMapStringValue(map, EPOCH_TIME_FIELD_JOB_PARAMETER);
		
		streamingTopic = jobDataMapExtension.getJobDataMapStringValue(map, STREAMING_TOPIC_FIELD_JOB_PARAMETER);
		
		
		latestEventTime = jobDataMapExtension.getJobDataMapLongValue(map, LATEST_EVENT_TIME_JOB_PARAMETER, System.currentTimeMillis());
		latestEventTime = TimestampUtils.convertToSeconds(latestEventTime);

		deltaTimeInSec = jobDataMapExtension.getJobDataMapLongValue(map, DELTA_TIME_IN_SEC_JOB_PARAMETER, (long)EVENTS_DELTA_TIME_IN_SEC_DEFAULT);
		
		fetchEventsStepInDays = jobDataMapExtension.getJobDataMapIntValue(map, FETCH_EVENTS_STEP_IN_DAYS_JOB_PARAMETER, FETCH_EVENTS_STEP_IN_DAYS_DEFAULT);
	}
	
	@Override
	protected int getTotalNumOfSteps(){
		return 1;
	}
		
	@Override
	protected void runSteps() throws Exception{
		KafkaEventsWriter streamWriter = null;
		
		try{
			streamWriter = new KafkaEventsWriter(streamingTopic);
			long timestampCursor = latestEventTime - deltaTimeInSec;
			while(timestampCursor <= latestEventTime){
				long nextTimestampCursor = timestampCursor + fetchEventsStepInDays * DateTimeConstants.SECONDS_PER_DAY;
				ImpalaQuery query = new ImpalaQuery();
				query.select("*").from(impalaTableName);
				query.andWhere(gte(epochtimeField, Long.toString(timestampCursor)));
				query.andWhere(lt(epochtimeField, Long.toString(nextTimestampCursor)));
				
				List<Map<String, Object>> resultsMap = impalaJdbcTemplate.query(query.toSQL(), new ColumnMapRowMapper());
				
				for(Map<String, Object> result: resultsMap){
					JSONObject json = new JSONObject();
					Iterator<Entry<String, Object>> resultIter = result.entrySet().iterator();
					while(resultIter.hasNext()){
						Entry<String, Object> entry = resultIter.next();
						json.put(entry.getKey(), entry.getValue());
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
		
	@Override
	protected boolean shouldReportDataReceived() {
		return true;
	}
	
}