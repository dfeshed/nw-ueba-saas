package fortscale.collection.scoring.jobs;

import java.util.Date;

import org.apache.pig.backend.executionengine.ExecJob;
import org.apache.pig.tools.pigstats.PigStats;
import org.joda.time.DateTime;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import fortscale.collection.hadoop.ImpalaClient;
import fortscale.monitor.JobProgressReporter;
import fortscale.services.LogEventsEnum;
import fortscale.utils.logging.Logger;

@DisallowConcurrentExecution
public abstract class EventScoringJob implements Job {
	private static Logger logger = Logger.getLogger(EventScoringJob.class);
	
	@Autowired 
	protected JobProgressReporter monitor;
	
	@Autowired
	private ImpalaClient impalaClient;
		
	protected abstract String getTableName();
	
	private String monitorId;
	
	private String stepName;
	
	private int stepIndex = 1;

	

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
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
	
	protected abstract int getTotalNumOfSteps();
	
	protected abstract void runSteps() throws Exception;
	
	protected boolean runScoringSteps(LogEventsEnum eventsEnum) throws Exception{
		DateTime dateTime = new DateTime();
		Long runtime = dateTime.getMillis() / 1000;
		Long deltaTime = dateTime.minusDays(14).getMillis() / 1000;
		
		
		boolean isSucceeded = runScoringPig(runtime, deltaTime, eventsEnum);
		if(!isSucceeded){
			return false;
		}
		
		
		isSucceeded = runAddPartitionQuery(runtime, eventsEnum);
		if(!isSucceeded){
			return false;
		}
		
		isSucceeded = runRefreshTable(eventsEnum);
		if(!isSucceeded){
			return false;
		}
		
		isSucceeded = runUpdateUserWithEventScore(runtime, eventsEnum);
		if(!isSucceeded){
			return false;
		}
		
		return true;
	}
	
	private boolean runUpdateUserWithEventScore(Long runtime, LogEventsEnum eventsEnum){
		startNewStep(String.format("updateUser %s Score", eventsEnum));
		
		DateTime dateTime = new DateTime(runtime * 1000);
		boolean isSucceeded = runUpdateUserWithEventScore(dateTime.toDate());
		if(isSucceeded){
			finishStep();
		}
		return isSucceeded;
	}
	
	protected abstract boolean runUpdateUserWithEventScore(Date runtime);
	
	private boolean runScoringPig(Long runtime, Long deltaTime, LogEventsEnum eventsEnum) throws Exception{
		startNewStep(String.format("%s pig", eventsEnum));
				
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
	
	protected abstract ExecJob runPig(Long runtime, Long deltaTime) throws Exception;
	
	private boolean runAddPartitionQuery(Long runtime, LogEventsEnum eventsEnum) throws JobExecutionException{	
		startNewStep(String.format("%s add partition", eventsEnum));
		
		impalaClient.addPartitionToTable(getTableName(), runtime);

		finishStep();
		
		
		return true;
	}
	
	private boolean runRefreshTable(LogEventsEnum eventsEnum) throws JobExecutionException{	
		startNewStep(String.format("%s refresh table", eventsEnum));
		
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
}
