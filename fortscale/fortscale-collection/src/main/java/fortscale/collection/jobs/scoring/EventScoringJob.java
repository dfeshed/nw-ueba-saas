package fortscale.collection.jobs.scoring;


import java.util.Date;

import org.apache.pig.backend.executionengine.ExecJob;
import org.apache.pig.tools.pigstats.PigStats;
import org.joda.time.DateTime;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import fortscale.collection.hadoop.ImpalaClient;
import fortscale.collection.hadoop.pig.EventScoringPigRunner;
import fortscale.collection.hadoop.pig.NoPartitionExistException;
import fortscale.collection.jobs.FortscaleJob;
import fortscale.utils.logging.Logger;

@DisallowConcurrentExecution
public abstract class EventScoringJob extends FortscaleJob {
	private static Logger logger = Logger.getLogger(EventScoringJob.class);
	
	public static int EVENTS_DELTA_TIME_IN_SEC_DEFAULT = 14*24*60*60;
	public static final String IMPALA_TABLE_NAME_JOB_PARAMETER = "impalaTableName";
	public static final String PIG_SCRIPT_RESOURCE_JOB_PARAMETER = "pigScriptResouce";
	public static final String PIG_INPUT_DATA_JOB_PARAMETER = "pigInputData";
	public static final String PIG_OUTPUT_DATA_PREFIX_JOB_PARAMETER = "pigOutputDataPrefix";
	public static final String LATEST_EVENT_TIME_JOB_PARAMETER = "latestEventTime";
	public static final String DELTA_TIME_IN_SEC_JOB_PARAMETER = "deltaTimeInSec";
	
	@Autowired
	private ImpalaClient impalaClient;
		
	@Autowired
	private EventScoringPigRunner eventScoringPigRunner;
	
	protected Long runtime;
	protected Long earliestEventTime;
	
	
	//parameters:
	private String impalaTableName;
	
	private Resource pigScriptResource;
	
	private String pigInputData;
	
	private String pigOutputDataPrefix;
		
	private Long latestEventTime = null;
	private Long deltaTimeInSec = null;

	
	
	protected String getTableName(){
		return impalaTableName;
	}
	
	@Override
	protected void init(JobExecutionContext jobExecutionContext) throws JobExecutionException{
		super.init(jobExecutionContext);
		
		runtime = latestEventTime;
		if(runtime == null){
			DateTime dateTime = new DateTime();
			runtime = dateTime.getMillis() / 1000;
		}
		if(deltaTimeInSec != null){
			earliestEventTime = runtime - deltaTimeInSec;
		} else{
			earliestEventTime = runtime - EVENTS_DELTA_TIME_IN_SEC_DEFAULT;
		}
	}
	
	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobDataMap map = jobExecutionContext.getMergedJobDataMap();

		// get parameters values from the job data map
		
		impalaTableName = jobDataMapExtension.getJobDataMapStringValue(map, IMPALA_TABLE_NAME_JOB_PARAMETER);
		pigScriptResource = jobDataMapExtension.getJobDataMapResourceValue(map, PIG_SCRIPT_RESOURCE_JOB_PARAMETER);
		pigInputData = jobDataMapExtension.getJobDataMapStringValue(map, PIG_INPUT_DATA_JOB_PARAMETER);
		pigOutputDataPrefix = jobDataMapExtension.getJobDataMapStringValue(map, PIG_OUTPUT_DATA_PREFIX_JOB_PARAMETER);
		if (map.containsKey(LATEST_EVENT_TIME_JOB_PARAMETER)) {
			latestEventTime = jobDataMapExtension.getJobDataMapLongValue(map, LATEST_EVENT_TIME_JOB_PARAMETER);
			latestEventTime = normalizeTimeToEpochSec(latestEventTime);
		}
		if (map.containsKey(DELTA_TIME_IN_SEC_JOB_PARAMETER)) {
			deltaTimeInSec = jobDataMapExtension.getJobDataMapLongValue(map, DELTA_TIME_IN_SEC_JOB_PARAMETER);
		}
	}
	
	private long normalizeTimeToEpochSec(long ts) {
		if (ts < 100000000000L)
			return ts;
		else
			return ts /1000;
	}

	@Override
	protected int getTotalNumOfSteps(){
		return 4;
	}
		
	@Override
	protected void runSteps() throws Exception{
		
		boolean isSucceeded = runScoringPig();
		if(!isSucceeded){
			return;
		}
		
		
		isSucceeded = runAddPartitionQuery();
		if(!isSucceeded){
			return;
		}
		
		isSucceeded = runRefreshTable();
		if(!isSucceeded){
			return;
		}
		
		isSucceeded = runUpdateUserWithEventScore();
		if(!isSucceeded){
			return;
		}		
	}
		
	private boolean runUpdateUserWithEventScore(){
		startNewStep("updateUser Score");
		
		DateTime dateTime = new DateTime(runtime * 1000);
		boolean isSucceeded = runUpdateUserWithEventScore(dateTime.toDate());
		if(isSucceeded){
			finishStep();
		}
		return isSucceeded;
	}
	
	protected abstract boolean runUpdateUserWithEventScore(Date runtime);
	
	private boolean runScoringPig() throws Exception{
		boolean ret = true;
		startNewStep("pig");
		try{
			ExecJob execJob = runPig();
			if(ExecJob.JOB_STATUS.FAILED.equals(execJob.getStatus())){
				PigStats pigStats = execJob.getStatistics();
				String errMsg = String.format("while running the step %s, the pig job had failed with error code (%d) and the following message: %s.", getStepName(),
	        			pigStats.getErrorCode(), pigStats.getErrorMessage());
	        	logger.error(errMsg);
	        	logger.error(pigStats.getJobGraph().toString());
	        	monitor.error(getMonitorId(), getStepName(), errMsg);
				return false;
	        }
		} catch(NoPartitionExistException e){
			ret = false;
		}

		monitor.finishStep(getMonitorId(), getStepName());
		
		
		return ret;
	}
	
	protected ExecJob runPig() throws Exception{
		return eventScoringPigRunner.run(runtime, earliestEventTime, pigScriptResource, pigInputData, pigOutputDataPrefix);
	}
	
	private boolean runAddPartitionQuery() throws JobExecutionException{	
		startNewStep(String.format("%s add partition ", getTableName()));
		
		impalaClient.addPartitionToTable(getTableName(), runtime);

		finishStep();
		
		
		return true;
	}
	
	private boolean runRefreshTable() throws JobExecutionException{	
		startNewStep(String.format("%s refresh table", getTableName()));
		
		impalaClient.refreshTable(getTableName());

		finishStep();
		
		
		return true;
	}
	

	public Resource getPigScriptResource() {
		return pigScriptResource;
	}


	public void setPigScriptResource(Resource pigScriptResource) {
		this.pigScriptResource = pigScriptResource;
	}


	public String getPigInputData() {
		return pigInputData;
	}


	public void setPigInputData(String pigInputData) {
		this.pigInputData = pigInputData;
	}


	public String getPigOutputDataPrefix() {
		return pigOutputDataPrefix;
	}


	public void setPigOutputDataPrefix(String pigOutputDataPrefix) {
		this.pigOutputDataPrefix = pigOutputDataPrefix;
	}

	public Long getLatestEventTime() {
		return latestEventTime;
	}

	public Long getRuntime() {
		return runtime;
	}

	public void setRuntime(Long runtime) {
		this.runtime = runtime;
	}

	public Long getEarliestEventTime() {
		return earliestEventTime;
	}

	public void setEarliestEventTime(Long earliestEventTime) {
		this.earliestEventTime = earliestEventTime;
	}
	
	
}
