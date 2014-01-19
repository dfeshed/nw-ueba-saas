package fortscale.collection;

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
	
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = null;
		try {
			// Grab schedule instance from the factory
			// use the quartz.conf instance for jobs and triggers configuration
			logger.info("initializing batch scheduler");
			
			// loading spring application context
			context = new ClassPathXmlApplicationContext("classpath*:META-INF/spring/collection-context.xml");

			// do not start scheduler in case of pause argument, just initialize it
			// this is to be used mainly in debug scenarios 
			if (args.length > 0 && args[0].equals("pause"))
				return;
				
			// start of the scheduler, the application will not terminate
			// until a call to scheduler.shutdown() is made, because there are
			// active threads
			logger.info("starting batch scheduler execution");
			Scheduler scheduler = (Scheduler) context.getBean("jobScheduler");
			scheduler.start();
			
		} catch (SchedulerException e) {
			logger.error("error in scheduling collection jobs", e);
		}
	}

}
