package fortscale.collection;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.listeners.JobListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;


/**
 * Shutdown scheduler after job is completed
 */
public class NotifyJobFinishListener extends JobListenerSupport {

	private static final Logger logger = LoggerFactory.getLogger(NotifyJobFinishListener.class); 
	
	private JobKey jobToWait;
	private FinishSignal monitor;
	
	public static FinishSignal waitOnJob(Scheduler scheduler, JobKey jobKey) throws SchedulerException {
		FinishSignal monitor = new FinishSignal();
		NotifyJobFinishListener listener = new NotifyJobFinishListener(jobKey, monitor);
		scheduler.getListenerManager().addJobListener(listener);
		return monitor;
	}
	
	public NotifyJobFinishListener(JobKey jobKey, FinishSignal monitor) {
		Assert.notNull(jobKey);
		Assert.notNull(monitor);
		
		jobToWait = jobKey;
		this.monitor = monitor;
	}
	
	
	@Override public String getName() {
		return "SchedulerShutdownJobListener";
	}
	
	@Override public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		// check if the job currently finished is the one we were waiting for 
		JobKey job = context.getJobDetail().getKey();
		if (jobToWait.equals(job)) {
			// notify finish
			logger.info("job {} was finished", job);
			monitor.doNotify();
		}		
	}


	public static class FinishSignal {
		boolean wasSignalled = false;
		public void doWait() {
			synchronized(this) {
				while (!wasSignalled) {
					try {
						this.wait();
					} catch (InterruptedException e) {}
				}
			}
			wasSignalled = false;
		}
		
		public void doNotify() {
			synchronized(this) {
				wasSignalled = true;
				this.notify();
			}
		}
	}
}
