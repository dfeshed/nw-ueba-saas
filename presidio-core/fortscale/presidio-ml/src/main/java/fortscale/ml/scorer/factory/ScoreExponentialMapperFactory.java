package fortscale.ml.scorer.factory;

import fortscale.ml.scorer.ScoreExponentialMapper;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.config.ScoreExponentialMapperConf;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;


@SuppressWarnings("unused")
@Component
public class ScoreExponentialMapperFactory extends AbstractServiceAutowiringFactory<Scorer> {
    private static final String FACTORY_CONFIG_TYPE_ERROR_MSG = String.format(
            "factoryConfig must be an instance of %s.", ScoreExponentialMapperConf.class.getSimpleName());

    @Override
    public String getFactoryName() {
        return ScoreExponentialMapperConf.SCORER_TYPE;
    }

    @Override
    public ScoreExponentialMapper getProduct(FactoryConfig factoryConfig) {
        Assert.isInstanceOf(ScoreExponentialMapperConf.class, factoryConfig, FACTORY_CONFIG_TYPE_ERROR_MSG);
        ScoreExponentialMapperConf conf = (ScoreExponentialMapperConf) factoryConfig;
        Scorer baseScorer = factoryService.getProduct(conf.getBaseScorerConf());
        return new ScoreExponentialMapper(conf.getName(), baseScorer, conf.getScoreMappingConf());
    }
}
