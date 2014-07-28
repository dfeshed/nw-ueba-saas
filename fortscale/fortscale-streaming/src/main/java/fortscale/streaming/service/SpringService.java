package fortscale.streaming.service;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Collection;

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
		if (instance==null) {
			instance = new SpringService(contextPath);
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

    public <T> Collection<T> resolveAll(Class<T> requiredType) {
        return context.getBeansOfType(requiredType).values();
    }
}
