package fortscale.collection;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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
			
			// loading spring application context
			ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
					"classpath*:META-INF/spring/collection-context.xml",
					"classpath*:META-INF/spring/monitor-context.xml", 
					"classpath*:META-INF/spring/mongo-context.xml");
						
			// set spring bean supporting job factory for the scheduler
			AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
			jobFactory.setApplicationContext(context);
			scheduler.setJobFactory(jobFactory);
			
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
