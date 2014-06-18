package fortscale.streaming.service;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Singleton spring context service wrapper
 */
public class SpringService {

	/// Singleton section
	
	private static SpringService instance;
	
	public static SpringService getInstance() {
		if (instance==null) {
			instance = new SpringService("classpath*:META-INF/spring/streaming-context.xml");
		}
		return instance;
	}
	
	public static SpringService getInstance(String contextPath) {
		return new SpringService(contextPath);
	}
	
	
	/// instance section
	
	private ApplicationContext context;
	
	private SpringService(String contextPath) {
		context = new ClassPathXmlApplicationContext(contextPath);
	}
	
	public <T> T resolve(Class<T> requiredType) {
		return context.getBean(requiredType);
	}
	
}
