package fortscale.ml.scorer.factory;

import fortscale.ml.scorer.ScoreMapper;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.config.ScoreMapperConf;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@SuppressWarnings("unused")
@Component
public class ScoreMapperFactory extends AbstractServiceAutowiringFactory<Scorer> {
	private static final String FACTORY_CONFIG_TYPE_ERROR_MSG = String.format(
			"factoryConfig must be an instance of %s.", ScoreMapperConf.class.getSimpleName());

	@Override
	public String getFactoryName() {
		return ScoreMapperConf.SCORER_TYPE;
	}

	@Override
	public ScoreMapper getProduct(FactoryConfig factoryConfig) {
		Assert.isInstanceOf(ScoreMapperConf.class, factoryConfig, FACTORY_CONFIG_TYPE_ERROR_MSG);
		ScoreMapperConf conf = (ScoreMapperConf) factoryConfig;
		Scorer baseScorer = factoryService.getProduct(conf.getBaseScorerConf());
		return new ScoreMapper(conf.getName(), baseScorer, conf.getScoreMappingConf());
	}
}
