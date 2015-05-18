package fortscale.streaming.scorer;

import org.apache.samza.config.Config;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ContinuousDataGlobalModelScorerFactory implements InitializingBean, ScorerFactory {
	public static final String SCORER_TYPE = "continuous-data-global-model";

	@Autowired
	private ScorerFactoryService scorerFactoryService;

	@Override
	public void afterPropertiesSet() throws Exception {
		scorerFactoryService.register(SCORER_TYPE, this);
	}

	@Override
	public Scorer getScorer(String name, Config config, ScorerContext context) {
		return new ContinuousDataGlobalModelScorer(name, config, context);
	}
}
