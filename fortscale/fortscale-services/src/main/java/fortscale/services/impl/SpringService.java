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

		// Create a Spring context and refresh it (refresh activates it)
		boolean isRefresh = true;
		initExtended(contextPath, isRefresh);

	}

	public static void initExtended(String contextPath, boolean isRefresh) {
		if (instance==null) {
			logger.info("Creating SpringService with context at {} with isRefresh={}", contextPath, isRefresh);
			instance = new SpringService(contextPath, isRefresh);
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
	
	private ClassPathXmlApplicationContext context;

	/**
	 *
	 * Creates a Spring context
	 *
	 * @param contextPath - XML context file (e.g. "classpath*:streaming-user-score-context.xml" )
	 * @param isRefresh   - Should refresh the context. Setting to False enable further context operation before refreshing it
	 */
	private SpringService(String contextPath, boolean isRefresh) {

		// Convert the context path to config location list
		String [] configLocations = new String[] { contextPath };

		// Create the context
		context = new ClassPathXmlApplicationContext(configLocations, isRefresh);
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

	// --- getters/setters ---

	public ClassPathXmlApplicationContext getContext() {
		return context;
	}

}
