package fortscale.collection.jobs.scoring;


import java.util.Date;

import org.apache.pig.backend.executionengine.ExecJob;
import org.apache.pig.tools.pigstats.PigStats;
import org.joda.time.DateTime;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import fortscale.collection.JobDataMapExtension;
import fortscale.collection.hadoop.ImpalaClient;
import fortscale.collection.hadoop.pig.EventScoringPigRunner;
import fortscale.monitor.JobProgressReporter;
import fortscale.utils.logging.Logger;

@DisallowConcurrentExecution
public abstract class EventScoringJob implements Job {
	private static Logger logger = Logger.getLogger(EventScoringJob.class);
	
	@Autowired 
	protected JobProgressReporter monitor;
	
	@Autowired
	private ImpalaClient impalaClient;
	
	@Autowired
	private JobDataMapExtension jobDataMapExtension;
	
	@Autowired
	private EventScoringPigRunner eventScoringPigRunner;
	
	
	private String monitorId;
	
	private String stepName;
	
	private int stepIndex = 1;
	
	private String impalaTableName;
	
	private Resource pigScriptResource;
	
	private String pigInputData;
	
	private String pigOutputDataPrefix;

	
	
	protected String getTableName(){
		return impalaTableName;
	}
	
	
	protected void getJobParameters(JobExecutionContext context) throws JobExecutionException {
		JobDataMap map = context.getMergedJobDataMap();

		// get parameters values from the job data map
		
		impalaTableName = jobDataMapExtension.getJobDataMapStringValue(map, "impalaTableName");
		pigScriptResource = jobDataMapExtension.getJobDataMapResourceValue(map, "pigScriptResouce");
		pigInputData = jobDataMapExtension.getJobDataMapStringValue(map, "pigInputData");
		pigOutputDataPrefix = jobDataMapExtension.getJobDataMapStringValue(map, "pigOutputDataPrefix");
		
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		getJobParameters(jobExecutionContext);
		
		String monitorId = startMonitoring(jobExecutionContext, 2);
		
		setMonitorId(monitorId);
		try{
			runSteps();
		} catch (Exception e) {
			logger.error(String.format("while running the step %s, got the following exception", stepName), e);
			monitor.error(monitorId, stepName, String.format("while running the step %s, got the following exception %s", stepName, e.getMessage()));
		}
		
		
		monitor.finishJob(monitorId);
	}
	
	protected int getTotalNumOfSteps(){
		return getTotalNumOfScoringSteps();
	}
	
	protected int getTotalNumOfScoringSteps(){
		return 4;
	}
	
	protected void runSteps() throws Exception{
		DateTime dateTime = new DateTime();
		Long runtime = dateTime.getMillis() / 1000;
		Long deltaTime = dateTime.minusDays(14).getMillis() / 1000;
		
		
		boolean isSucceeded = runScoringPig(runtime, deltaTime);
		if(!isSucceeded){
			return;
		}
		
		
		isSucceeded = runAddPartitionQuery(runtime);
		if(!isSucceeded){
			return;
		}
		
		isSucceeded = runRefreshTable();
		if(!isSucceeded){
			return;
		}
		
		isSucceeded = runUpdateUserWithEventScore(runtime);
		if(!isSucceeded){
			return;
		}		
	}
		
	private boolean runUpdateUserWithEventScore(Long runtime){
		startNewStep("updateUser Score");
		
		DateTime dateTime = new DateTime(runtime * 1000);
		boolean isSucceeded = runUpdateUserWithEventScore(dateTime.toDate());
		if(isSucceeded){
			finishStep();
		}
		return isSucceeded;
	}
	
	protected abstract boolean runUpdateUserWithEventScore(Date runtime);
	
	private boolean runScoringPig(Long runtime, Long deltaTime) throws Exception{
		startNewStep("pig");
				
		ExecJob execJob = runPig(runtime, deltaTime);
		if(ExecJob.JOB_STATUS.FAILED.equals(execJob.getStatus())){
			PigStats pigStats = execJob.getStatistics();
			String errMsg = String.format("while running the step %s, the pig job had failed with error code (%d) and the following message: %s.", stepName,
        			pigStats.getErrorCode(), pigStats.getErrorMessage());
        	logger.error(errMsg);
        	logger.error(pigStats.getJobGraph().toString());
        	monitor.error(monitorId, stepName, errMsg);
			return false;
        }

		monitor.finishStep(monitorId, stepName);
		
		
		return true;
	}
	
	protected ExecJob runPig(Long runtime, Long deltaTime) throws Exception{
		return eventScoringPigRunner.run(runtime, deltaTime, pigScriptResource, pigInputData, pigOutputDataPrefix);
	}
	
	private boolean runAddPartitionQuery(Long runtime) throws JobExecutionException{	
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
	
	protected boolean runCmd(String cmd, String stepName){
		Runtime run = Runtime.getRuntime();
		Process pr = null;			
		
		startNewStep(stepName);
		try {
			pr = run.exec(cmd);
			pr.waitFor();

		} catch (Exception e) {
			logger.error(String.format("while running the command %s, got the following exception", cmd), e);
			monitor.error(monitorId, stepName, String.format("while running the command %s, got the following exception %s", cmd, e.getMessage()));
			return false;
		}
		finishStep();
		
		
		return true;
	}
	

	private String startMonitoring(JobExecutionContext jobExecutionContext, int numOfSteps){
		// get the job group name to be used using monitoring 
		String sourceName = jobExecutionContext.getJobDetail().getKey().getGroup();
		String jobName = jobExecutionContext.getJobDetail().getKey().getName();
		String monitorId = monitor.startJob(sourceName, jobName, numOfSteps);
		
		return monitorId;
	}
	
	public String getMonitorId() {
		return monitorId;
	}

	public void setMonitorId(String monitorId) {
		this.monitorId = monitorId;
	}

	public String getStepName() {
		return stepName;
	}

	public void startNewStep(String stepName) {
		logger.info("Running {} ", stepName);		
		this.stepName = stepName;
		monitor.startStep(monitorId, stepName, stepIndex);
		stepIndex++;
	}
	
	public void finishStep(){
		monitor.finishStep(monitorId, stepName);
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
	
	
	
}
