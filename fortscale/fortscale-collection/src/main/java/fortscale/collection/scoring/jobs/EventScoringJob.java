package fortscale.collection.scoring.jobs;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import fortscale.monitor.JobProgressReporter;
import fortscale.utils.logging.Logger;

@DisallowConcurrentExecution
public abstract class EventScoringJob implements Job {
	private static Logger logger = Logger.getLogger(EventScoringJob.class);
	
	@Autowired 
	protected JobProgressReporter monitor;

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		String monitorId = startMonitoring(jobExecutionContext, 2);
		
		runSteps(monitorId);
		
		monitor.finishJob(monitorId);
	}
	
	protected abstract void runSteps(String monitorId);
	
	protected boolean runCmd(String monitorId, String cmd, String stepName){
		logger.info("Running {} with the following shell command: {}", stepName, cmd);
		
		
		Runtime run = Runtime.getRuntime();
		Process pr = null;			
		
		monitor.startStep(monitorId, stepName, 1);
		try {
			pr = run.exec(cmd);
			pr.waitFor();

		} catch (Exception e) {
			logger.error(String.format("while running the command %s, got the following exception", cmd), e);
			monitor.error(monitorId, stepName, String.format("while running the command %s, got the following exception %s", cmd, e.getMessage()));
			return false;
		}
		monitor.finishStep(monitorId, stepName);
		
		
		return true;
	}

	private String startMonitoring(JobExecutionContext jobExecutionContext, int numOfSteps){
		// get the job group name to be used using monitoring 
		String sourceName = jobExecutionContext.getJobDetail().getKey().getGroup();
		String jobName = jobExecutionContext.getJobDetail().getKey().getName();
		String monitorId = monitor.startJob(sourceName, jobName, numOfSteps);
		
		return monitorId;
	}
}
