package fortscale.ml.scorer.factory;

import fortscale.ml.scorer.LinearScoreReducer;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.config.LinearScoreReducerConf;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class LinearScoreReducerFactory extends AbstractServiceAutowiringFactory<Scorer> {
	private static final String FACTORY_CONFIG_TYPE_ERROR_MSG = String.format(
			"Factory configuration must be of type %s.", LinearScoreReducerConf.class.getSimpleName());

	@Override
	public String getFactoryName() {
		return LinearScoreReducerConf.SCORER_TYPE;
	}

	@Override
	public Scorer getProduct(FactoryConfig factoryConfig) {
		Assert.isInstanceOf(LinearScoreReducerConf.class, factoryConfig, FACTORY_CONFIG_TYPE_ERROR_MSG);
		LinearScoreReducerConf conf = (LinearScoreReducerConf)factoryConfig;
		Scorer reducedScorer = factoryService.getProduct(conf.getReducedScorer());
		return new LinearScoreReducer(conf.getName(), reducedScorer, conf.getReducingWeight());
	}
}
