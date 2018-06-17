package fortscale.ml.scorer.factory;

import fortscale.ml.scorer.ScoreExponentialStepsMapper;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.config.ScoreExponentialStepsMapperConf;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;


@SuppressWarnings("unused")
@Component
public class ScoreExponentialStepsMapperFactory extends AbstractServiceAutowiringFactory<Scorer> {
    private static final String FACTORY_CONFIG_TYPE_ERROR_MSG = String.format(
            "factoryConfig must be an instance of %s.", ScoreExponentialStepsMapperConf.class.getSimpleName());

    @Override
    public String getFactoryName() {
        return ScoreExponentialStepsMapperConf.SCORER_TYPE;
    }

    @Override
    public ScoreExponentialStepsMapper getProduct(FactoryConfig factoryConfig) {
        Assert.isInstanceOf(ScoreExponentialStepsMapperConf.class, factoryConfig, FACTORY_CONFIG_TYPE_ERROR_MSG);
        ScoreExponentialStepsMapperConf conf = (ScoreExponentialStepsMapperConf) factoryConfig;
        Scorer baseScorer = factoryService.getProduct(conf.getBaseScorerConf());
        return new ScoreExponentialStepsMapper(conf.getName(), baseScorer, conf.getScoreMappingConf());
    }
}
