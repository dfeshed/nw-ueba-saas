package fortscale.streaming.scorer;

import static fortscale.streaming.ConfigUtils.getConfigString;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.samza.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable(preConstruction=true)
public class ScorerContext {
	
	@Autowired
	private ScorerFactoryService scorerFactoryService;
	
	private Config config;
	private Map<String, Object> beanNameToObject = new HashMap<>();
	private Set<String> beansInTheMiddleOfConstructor = new HashSet<>();
	
	public ScorerContext(Config config){
		this.config = config;
	}
	
	public void setBean(String beanName, Object bean){
		beanNameToObject.put(beanName, bean);
	}
	
	public Object resolve(Class<?> beanclass, String beanName){
		if(beansInTheMiddleOfConstructor.contains(beanName)){
			throw new RuntimeException(String.format("cyclic definition: %s,%s", beansInTheMiddleOfConstructor.toString(), beanName));
		}
		Object bean = beanNameToObject.get(beanName);
		
		if(bean == null){
			beansInTheMiddleOfConstructor.add(beanName);
			bean = buildScorer(beanName);
			if(bean == null){
				throw new RuntimeException("no such bean: " + beanName);
			}
			beanNameToObject.put(beanName, bean);
			
			beansInTheMiddleOfConstructor.remove(beanName);
		}
		
		return bean;
		
//		if(bean instanceof beanclass){
//			throw new Exception(String.format("the bean actual class (%s) is not as expected (%s)", bean.getClass(), beanclass);
//		}
	}

	private Scorer buildScorer(String scorerName){
		String scorerType = getConfigString(config, String.format("fortscale.score.%s.scorer", scorerName));
		return scorerFactoryService.getScorer(scorerType, scorerName, config, this);
	}
}
