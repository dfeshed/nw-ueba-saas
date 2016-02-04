package fortscale.ml.scorer.factory;

import fortscale.ml.scorer.ConstantRegexScorer;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.config.ConstantRegexScorerConf;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@SuppressWarnings("unused")
@Component
public class ConstantRegexScorerFactory extends AbstractServiceAutowiringFactory<Scorer> {
	private static final String FACTORY_CONFIG_TYPE_ERROR_MSG = String.format(
			"factoryConfig must be an instance of %s.", ConstantRegexScorerConf.class.getSimpleName());

	@Override
	public String getFactoryName() {
		return ConstantRegexScorerConf.SCORER_TYPE;
	}

	@Override
	public Scorer getProduct(FactoryConfig factoryConfig) {
		Assert.isInstanceOf(ConstantRegexScorerConf.class, factoryConfig, FACTORY_CONFIG_TYPE_ERROR_MSG);
		ConstantRegexScorerConf conf = (ConstantRegexScorerConf)factoryConfig;
		return new ConstantRegexScorer(conf.getName(), conf.getRegexFieldName(), conf.getRegexPattern(), conf.getConstantScore());
	}
}
