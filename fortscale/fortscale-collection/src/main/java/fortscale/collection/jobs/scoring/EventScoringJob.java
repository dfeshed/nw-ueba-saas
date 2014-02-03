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
	
	private static int EVENTS_DELTA_TIME_IN_SEC = 14*24*60*60;
	
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
		earliestEventTime = runtime - EVENTS_DELTA_TIME_IN_SEC;
	}
	
	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobDataMap map = jobExecutionContext.getMergedJobDataMap();

		// get parameters values from the job data map
		
		impalaTableName = jobDataMapExtension.getJobDataMapStringValue(map, "impalaTableName");
		pigScriptResource = jobDataMapExtension.getJobDataMapResourceValue(map, "pigScriptResouce");
		pigInputData = jobDataMapExtension.getJobDataMapStringValue(map, "pigInputData");
		pigOutputDataPrefix = jobDataMapExtension.getJobDataMapStringValue(map, "pigOutputDataPrefix");
		if (map.containsKey("latestEventTime")) {
			latestEventTime = jobDataMapExtension.getJobDataMapLongValue(map, "latestEventTime");
			latestEventTime = normalizeTimeToEpochSec(latestEventTime);
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
