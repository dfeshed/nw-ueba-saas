package fortscale.collection.scoring.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import fortscale.monitor.JobProgressReporter;

public abstract class EventScoringJob implements Job {

	@Autowired 
	protected JobProgressReporter monitor;

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		String monitorId = startMonitoring(jobExecutionContext, 2);
		
		runSteps(monitorId);
		
		monitor.finishJob(monitorId);
	}
	
	protected abstract void runSteps(String monitorId);

	private String startMonitoring(JobExecutionContext jobExecutionContext, int numOfSteps){
		// get the job group name to be used using monitoring 
		String sourceName = jobExecutionContext.getJobDetail().getKey().getGroup();
		String jobName = jobExecutionContext.getJobDetail().getKey().getName();
		String monitorId = monitor.startJob(sourceName, jobName, numOfSteps);
		
		return monitorId;
	}
}
