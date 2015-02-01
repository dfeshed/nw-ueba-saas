package fortscale.streaming.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Collection;

/**
 * Singleton spring context service wrapper
 */
public class SpringService {

	/// Singleton section
	private static Logger logger = LoggerFactory.getLogger(SpringService.class);
	
	private static SpringService instance;
	
	public static void init(String contextPath) {
		if (instance==null) {
			logger.info("Creating SpringService with context at {}", contextPath);
			instance = new SpringService(contextPath);
		}
	}
	
	public static SpringService getInstance() {
		if (instance==null) {
			// report error if instance was not create
			StackTraceElement[] trace = Thread.currentThread().getStackTrace(); 
			logger.error("SpringService.getInstance was called from {}.{} without being initialized first", 
					trace[trace.length-1].getClassName(), trace[trace.length-1].getMethodName());
		}
		return instance;
	}
	
	public static void shutdown() {
		if (instance!=null) {
			if (instance.context!=null)
				((ClassPathXmlApplicationContext)instance.context).close();
			instance = null;
		}
	}
	
	/// instance section
	
	private ApplicationContext context;
	
	private SpringService(String contextPath) {
		context = new ClassPathXmlApplicationContext(contextPath);//("classpath*:streaming-user-score-context.xml");
	}
		
	public <T> T resolve(Class<T> requiredType) {
		return context.getBean(requiredType);
	}

	public Object resolve(String requiredName) {
		return context.getBean(requiredName);
	}

    public <T> Collection<T> resolveAll(Class<T> requiredType) {
        return context.getBeansOfType(requiredType).values();
    }
}
