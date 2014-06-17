package fortscale.streaming.service;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;



/**
 * Singleton spring context service wrapper for user score task
 */
public class SpringUserScoreService {

	/// Singleton section
	
	private static SpringUserScoreService instance = null;
	
	public static SpringUserScoreService getInstance(String contextPath) {
		if (instance==null) {
			instance = new SpringUserScoreService(contextPath);
		}
		return instance;
	}
	
	
	/// instance section
	
	private ApplicationContext context;
	
	private SpringUserScoreService(String contextPath) {
		context = new ClassPathXmlApplicationContext(contextPath);//("classpath*:streaming-user-score-context.xml");
	}
	
	public <T> T resolve(Class<T> requiredType) {
		return context.getBean(requiredType);
	}
}
