package fortscale.collection;

import java.util.Arrays;

import org.quartz.JobDataMap;
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
			} else if (args[0].equals("cycle")) {
				batch.runFullCycle(Arrays.copyOfRange(args, 1, args.length));
				batch.shutdown();
			} else if (args.length>=2) {
				// run the given job only
				String jobName = args[0];
				String group = args[1];
				
				batch.startSchedulerWithOneJob(jobName, group, Arrays.copyOfRange(args, 2, args.length));
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
	
	public void startSchedulerWithOneJob(String jobName, String group, String... params) throws Exception {
		startSchedulerAndPauseAllJobs();

		startJob(jobName, group, params);
	}
	
	private void startSchedulerAndPauseAllJobs() throws Exception{
		if (scheduler==null)
			loadScheduler();
		
		scheduler.start();
		scheduler.pauseAll();
	}
	
	public void runFullCycle(String... params) throws Exception{
		startSchedulerAndPauseAllJobs();
		
		startJob("Computer_Fetch", "AD", params);
		startJob("Computer_ETL", "AD", params);
		startJob("OU_Fetch", "AD", params);
		startJob("OU_ETL", "AD", params);
		startJob("Group_Fetch", "AD", params);
		startJob("Group_ETL", "AD", params);
		startJob("User_Fetch", "AD", params);
		startJob("User_ETL", "AD", params);
		startJob("User_Thumbnail_ETL", "AD", params);
		startJob("Group_Membership_Scoring", "AD", params);
		startJob("Fetch", "DHCP", params);
		startJob("ETL", "DHCP", params);
		startJob("Fetch", "SecurityEvents", params);
		startJob("Route_ETL", "SecurityEvents", params);
		startJob("Comp_ETL", "SecurityEvents", params);
		String etlParams[] = Arrays.copyOf(params, params.length + 2);
		etlParams[params.length] = "filesFilter";
		etlParams[params.length+1] = "user_SEC_\\d+.csv$";
		startJob("ETL", "SecurityEvents", etlParams);
		startJob("AuthScoring", "SecurityEvents", params);
		startJob("LoginScoring", "SecurityEvents", params);
		startJob("Fetch", "VPN", params);
		startJob("ETL", "VPN", params);
		startJob("Scoring", "VPN", params);
		startJob("Fetch", "SSH", params);
		startJob("ETL", "SSH", params);
		startJob("Scoring", "SSH", params);
		startJob("Scoring", "TOTAL", params);
		startJob("Export", "USER", params);
	}
	
	private void startJob(String jobName, String group, String... params) throws Exception {
		
		JobKey jobKey = new JobKey(jobName, group);
		
		// register job listener to close the scheduler after job completion
		NotifyJobFinishListener.FinishSignal monitor = NotifyJobFinishListener.waitOnJob(scheduler, jobKey);

		// check if job exists
		if (scheduler.checkExists(jobKey)) {

			// build job data map if given
			JobDataMap dataMap = new JobDataMap();
			if (params!=null && params.length>0) {
				for (String param : params) {
					String[] entry = param.split("=");
					dataMap.put(entry[0], entry[1]);
				}
			}	

			if (!dataMap.isEmpty())
				scheduler.triggerJob(jobKey, dataMap);
			else
				scheduler.triggerJob(jobKey);
			
			// wait for job completion
			monitor.doWait();		
		} else {
			System.out.println(String.format("job %s %s does not exist", jobName, group));
		}
	}
	
	public void shutdown() throws SchedulerException {
		scheduler.shutdown();
		context.close();
	}
	
}
