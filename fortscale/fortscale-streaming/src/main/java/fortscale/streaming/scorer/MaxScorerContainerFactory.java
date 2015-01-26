package fortscale.streaming.scorer;

import org.apache.samza.config.Config;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MaxScorerContainerFactory  implements InitializingBean, ScorerFactory{
	private static final String SCORER_NAME = "max-scorer";
	
	@Autowired
	private ScorerFactoryService scorerFactoryService;

	@Override
	public void afterPropertiesSet() throws Exception {
		scorerFactoryService.register(SCORER_NAME, this);
	}

	@Override
	public Scorer getScorer(String name, Config config, ScorerContext context) {
		return new MaxScorerContainer(name, config, context);
	}

}
