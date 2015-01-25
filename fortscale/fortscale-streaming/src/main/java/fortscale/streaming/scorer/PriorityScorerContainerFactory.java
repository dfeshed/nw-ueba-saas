package fortscale.streaming.scorer;

import org.apache.samza.config.Config;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fortscale.ml.service.ModelService;

@Component
public class PriorityScorerContainerFactory implements InitializingBean, ScorerFactory{
	private static final String SCORER_NAME = "priority-scorer";
	
	@Autowired
	private ScorerFactoryService scorerFactoryService;

	@Override
	public void afterPropertiesSet() throws Exception {
		scorerFactoryService.register(SCORER_NAME, this);
	}

	@Override
	public Scorer getScorer(String name, Config config, ModelService modelService) {
		return new PriorityScorerContainer(name, config);
	}
	
}
