package fortscale.streaming.scorer;

import org.apache.samza.config.Config;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fortscale.ml.service.ModelService;

@Service
public class ModelScorerFactory implements InitializingBean, ScorerFactory{
	private static final String SCORER_NAME = "model-scorer";
	
	@Autowired
	private ScorerFactoryService scorerFactoryService;

	@Override
	public void afterPropertiesSet() throws Exception {
		scorerFactoryService.register(SCORER_NAME, this);
	}

	@Override
	public Scorer getScorer(String name, Config config, ModelService modelService) {
		return new ModelScorer(name, config, modelService);
	}
	
}
