package fortscale.collection;

import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.process.processInfo.ProcessInfoService;
import fortscale.utils.process.processInfo.ProcessInfoServiceImpl;
import fortscale.utils.process.processType.ProcessType;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Arrays;

/**
 * Batch application scheduler for jobs execution
 */
public class BatchScheduler {

	final String COLLECTION_PROCESS_NAME_DAEMON  = "collection-daemon";
	final String COLLECTION_PROCESS_NAME_UTIL    = "collection-util";
	final String COLLECTION_PROCESS_GROUP_NAME   = "collection";


	private static Logger logger = LoggerFactory.getLogger(BatchScheduler.class);
	
	private Scheduler scheduler;
	private ClassPathXmlApplicationContext context;

	// Process Info service
	ProcessInfoService processInfoService;

	// Process types are:  running as a daemon (forever), process  as a utility (exits when done)
	protected ProcessType processType;

	public static void main(String[] args) {
		try {
			BatchScheduler batch = new BatchScheduler();

			if (args.length==0) {
				// Mark process as daemon
				batch.processType = ProcessType.DAEMON;

				batch.loadScheduler();
				batch.startAll();
			} else if (args[0].equals("pause")) {
				// Mark process as daemon
				batch.processType = ProcessType.DAEMON;
				batch.loadScheduler();
				// do nothing
			} else if (args[0].equals("createTables")) {
				batch.loadScheduler();
				//tables were created in loadScheduler when creating the spring context.
				batch.shutdown();
			} else if (args[0].equals("cycle")) {
				batch.loadScheduler();
				batch.runFullCycle(Arrays.copyOfRange(args, 1, args.length));
				batch.shutdown();
			} else if (args.length>=2) {
				batch.loadScheduler();
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
		logger.info("initializing batch scheduler. process type is {}", processType.toString());

		// Calculate the process name depending on daemon mode
		String processName;
		if (processType.equals(ProcessType.DAEMON)) {
			processName = COLLECTION_PROCESS_NAME_DAEMON;
		}
		else {
			processName = COLLECTION_PROCESS_NAME_UTIL;
		}

		// Create process PID service and init it
		processInfoService = new ProcessInfoServiceImpl(processName, COLLECTION_PROCESS_GROUP_NAME,processType);
		processInfoService.init();

		// point quartz configuration to external file resource
		System.setProperty("org.quartz.properties", "resources/jobs/quartz.properties");
			
		// loading spring application context, we do not close this context as the application continue to 
		// run in background threads
		// Create the spring context but do not refresh it for now
		boolean isRefresh = false;
		String [] configLocations = new String[] { "classpath*:META-INF/spring/collection-context.xml" };
		context = new ClassPathXmlApplicationContext(configLocations, isRefresh);

		// Register the process at spring context (e.g. add basic process properties to Spring context)
		processInfoService.registerToSpringContext(context);

		// Refresh (activate) the spring context
		context.refresh();

		scheduler = (Scheduler) context.getBean("jobScheduler");
		scheduler.getListenerManager().addSchedulerListener(new SchedulerShutdownListener(scheduler, context));
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

		startJob("Custom", "Tagging", params);
		startJob("Computer_Fetch", "AD", params);
		startJob("Computer_ETL", "AD", params);
		startJob("Computer", "Tagging", params);
		startJob("Classify_Computers", "AD", params);
		startJob("OU_Fetch", "AD", params);
		startJob("OU_ETL", "AD", params);
		startJob("Group_Fetch", "AD", params);
		startJob("Group_ETL", "AD", params);
		startJob("User_Fetch", "AD", params);
		startJob("User_ETL", "AD", params);
		startJob("User", "Tagging", params);
		startJob("User_Thumbnail_ETL", "AD", params);
		startJob("ETL", "DHCP", params);
		startJob("ETL", "ISE", params);
        startJob("Fetch4624", "SecurityEvents", params);
        startJob("Comp4624_ETL", "SecurityEvents", params);
		startJob("Route_ETL", "SecurityEvents", params);
		startJob("Comp_ETL", "SecurityEvents", params);
		String etlParams[] = Arrays.copyOf(params, params.length + 1);
		etlParams[params.length] = "filesFilter=user_SEC_\\d+.csv$";
		startJob("ETL", "SecurityEvents", etlParams);
		startJob("ETL", "VPN", params);
		startJob("ETL", "SSH", params);

        startJob("ETL", "CRMSF", params);
        startJob("ETL", "WAME", params);
        startJob("ETL", "GWAME", params);
        startJob("ETL", "NTLM", params);
        startJob("ETL", "PRNLOG", params);
        startJob("ETL", "ORACLE", params);

	}
	
	private void startJob(String jobName, String group, String... params) throws Exception {
		
		JobKey jobKey = new JobKey(jobName, group);
		
		// register job listener to close the scheduler after job completion
		NotifyJobFinishListener.FinishSignal monitor = NotifyJobFinishListener.waitOnJob(scheduler, jobKey);

		// check if job exists
		if (scheduler.checkExists(jobKey)) {

			// build job data map if given
			JobDataMap dataMap = new JobDataMap();
			if (params != null && params.length > 0) {
				for (String param : params) {
					String[] entry = param.split("=", 2);
					dataMap.put(entry[0], entry[1]);
				}
			}


			if (!dataMap.isEmpty()) {
				scheduler.triggerJob(jobKey, dataMap);
			} else
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

		// Shutdown the process info
		if (processInfoService != null) {
			processInfoService.shutdown();
		}

	}
	
}
