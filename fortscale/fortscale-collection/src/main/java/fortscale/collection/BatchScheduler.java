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
			logger.info("loading the collection application context");
			
			// loading spring application context
			@SuppressWarnings("unused")
			ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:META-INF/spring/collection-context.xml");
			
	}

}
