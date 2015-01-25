package fortscale.streaming.scorer;

import org.apache.samza.config.Config;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import fortscale.ml.service.ModelService;

public class ContstantRegexScorerFactory implements InitializingBean, ScorerFactory{
	private static final String SCORER_NAME = "const-regex-scorer";
	
	@Autowired
	private ScorerFactoryService scorerFactoryService;

	@Override
	public void afterPropertiesSet() throws Exception {
		scorerFactoryService.register(SCORER_NAME, this);
	}

	@Override
	public Scorer getScorer(String name, Config config, ModelService modelService) {
		return new ContstantRegexScorer(name, config);
	}
	
}

