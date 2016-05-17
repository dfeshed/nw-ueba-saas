package fortscale.services.impl;

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
			try {
				instance = new SpringService(contextPath);
			} catch (Exception e) {
				logger.error("Failed to initialize SpringService with context path {}", e);
				throw e;
			}
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


	/**
	 * Retrieve Bean by bean name and class name.
	 * This method checks that the type of the bean with the relevant name is of the expeted type,
	 * and return the expected type istead of Object (don't need to cast)
	 * @param requiredName - bean name
	 * @param className - the expected bean type
	 * @param <T>
	 * @return
	 */
	public <T> T resolve(String requiredName, Class<T> className) {
		return context.getBean(requiredName, className);
	}

    public <T> Collection<T> resolveAll(Class<T> requiredType) {
        return context.getBeansOfType(requiredType).values();
    }
}
