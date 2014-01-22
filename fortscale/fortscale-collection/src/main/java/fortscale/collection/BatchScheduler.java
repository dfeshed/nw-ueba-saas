package fortscale.collection;

import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Batch application scheduler for jobs execution
 */
public class BatchScheduler {

	private static Logger logger = LoggerFactory.getLogger(BatchScheduler.class);
	
	private Scheduler scheduler;
	private ClassPathXmlApplicationContext context;
	
	public static void main(String[] args) {
		try {
			BatchScheduler batch = new BatchScheduler();
			batch.loadScheduler();

			if (args.length==0) {
				batch.startAll();
			} else if (args[0].equals("pause")) {
				// do nothing
			} else if (args.length==2) {
				// run the given job only
				String jobName = args[0];
				String group = args[1];
				
				batch.startJob(jobName, group);
				batch.shutdown();
			} else {
				System.out.println("Usage:");
				System.out.println(" pause - load the scheduler without starting it");
				System.out.println(" <jobName> <group> - start only the given job");
				System.out.println(" <no args> - start the scheduler and all jobs");
			}
									
		} catch (Exception e) {
			logger.error("error in scheduling collection jobs", e);
		}
	}

	
	public void loadScheduler() throws Exception {
		// Grab schedule instance from the factory
		// use the quartz.conf instance for jobs and triggers configuration
		logger.info("initializing batch scheduler");
			
		// point quartz configuration to external file resource
		System.setProperty("org.quartz.properties", "resources/jobs/quartz.properties");
			
		// loading spring application context, we do not close this context as the application continue to 
		// run in background threads
		context = new ClassPathXmlApplicationContext("classpath*:META-INF/spring/collection-context.xml");
			
		scheduler = (Scheduler) context.getBean("jobScheduler");
	}
	
	
	public void startAll() throws Exception {
		if (scheduler==null)
			loadScheduler();
		
		// build job chaining listener
		JobChainingListener listener = new JobChainingListener("resources/jobs/job_chains.xml");
		scheduler.getListenerManager().addJobListener(listener);
		
		// start of the scheduler, the application will not terminate
		// until a call to scheduler.shutdown() is made, because there are
		// active threads
		logger.info("starting batch scheduler execution");
		scheduler.start();
	}
	
	public void startJob(String jobName, String group) throws Exception {
		if (scheduler==null)
			loadScheduler();
		
		JobKey jobKey = new JobKey(jobName, group);
		
		// register job listener to close the scheduler after job completion
		NotifyJobFinishListener.FinishSignal monitor = NotifyJobFinishListener.waitOnJob(scheduler, jobKey);

		// pause all triggers and schedule only the required job
		// we need to start the scheduler before triggering and pausing all jobs as the
		// jobs configuration is loaded only when the scheduler is started
		scheduler.start();
		scheduler.pauseAll();
		scheduler.triggerJob(jobKey);
		
		
		// wait for job completion
		monitor.doWait();		
	}
	
	public void shutdown() throws SchedulerException {
		scheduler.shutdown();
		context.close();
	}
	
}
