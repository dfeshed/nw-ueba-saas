package fortscale.collection;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Batch application scheduler for jobs execution
 */
public class BatchScheduler {

	private static Logger logger = LoggerFactory.getLogger(BatchScheduler.class);
	
	public static void main(String[] args) {
		try {
			// Grab schedule instance from the factory
			// use the quartz.conf instance for jobs and triggers configuration
			logger.info("initializing batch scheduler");
			Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
			
			// start of the scheduler, the application will not terminate
			// until a call to scheduler.shutdown() is made, because there are
			// active threads
			logger.info("starting batch scheduler execution");
			scheduler.start();
			
		} catch (SchedulerException e) {
			logger.error("error in scheduling collection jobs", e);
		}
	}

}
