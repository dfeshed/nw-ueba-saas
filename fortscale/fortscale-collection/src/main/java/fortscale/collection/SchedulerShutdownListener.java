package fortscale.collection;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Scheduler listener that closes spring context when the quartz scheduler shutdown
 * @author dotanp
 *
 */
public class SchedulerShutdownListener implements SchedulerListener {

	private static final Logger logger = LoggerFactory.getLogger(SchedulerShutdownListener.class);
	
	private ClassPathXmlApplicationContext context;
	private Scheduler scheduler;
	
	public SchedulerShutdownListener(Scheduler scheduler, ClassPathXmlApplicationContext context) {
		this.scheduler = scheduler;
		this.context = context;
	}

	public void schedulerShuttingdown() {
		logger.info("Schedler is starting to shutdown, waiting on jobs to finish");
	}
	
	public void schedulerShutdown() {
		// check if all jobs finished running
		logger.info("Waiting for all jobs to finish executing");
		try {
			while (scheduler.getCurrentlyExecutingJobs().size()>0) {
				try {
					// wait for 5 seconds to give running jobs a chance to finish executing
					Thread.sleep(5000);
				} catch (InterruptedException e) {}
			}
		} catch (SchedulerException e) {
			logger.error("error getting running jobs from scheduler", e);
		}
		
		
		logger.info("Scheduler has shutdown, going to close spring context");
		if (context!=null)
			context.close();
		context = null;
		scheduler = null;
		logger.info("Spring context closed");
	}

	public void jobScheduled(Trigger trigger) {}
	public void jobUnscheduled(TriggerKey triggerKey) {}
	public void triggerFinalized(Trigger trigger) {}
	public void triggerPaused(TriggerKey triggerKey) {}
	public void triggersPaused(String triggerGroup) {}
	public void triggerResumed(TriggerKey triggerKey) {}
	public void triggersResumed(String triggerGroup) {}
	public void jobAdded(JobDetail jobDetail) {}
	public void jobDeleted(JobKey jobKey) {}
	public void jobPaused(JobKey jobKey) {}
	public void jobsPaused(String jobGroup) {}
	public void jobResumed(JobKey jobKey) {}
	public void jobsResumed(String jobGroup) {}
	public void schedulerError(String msg, SchedulerException cause) {}
	public void schedulerInStandbyMode() {}
	public void schedulerStarted() {}
	public void schedulerStarting() {}
	
	public void schedulingDataCleared() {}
}
