package fortscale.ml.scorer.factory;

import fortscale.ml.scorer.ScoreAndCertaintyMultiplierScorer;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.config.ScoreAndCertaintyMultiplierScorerConf;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class ScoreAndCertaintyMultiplierScorerFactory extends AbstractServiceAutowiringFactory<Scorer> {
    private static final String FACTORY_CONFIG_TYPE_ERROR_MSG = String.format(
            "factoryConfig must be an instance of %s.", ScoreAndCertaintyMultiplierScorerConf.class.getSimpleName());

    @Override
    public String getFactoryName() {
        return ScoreAndCertaintyMultiplierScorerConf.SCORER_TYPE;
    }

    @Override
    public ScoreAndCertaintyMultiplierScorer getProduct(FactoryConfig factoryConfig) {
        Assert.isInstanceOf(ScoreAndCertaintyMultiplierScorerConf.class, factoryConfig, FACTORY_CONFIG_TYPE_ERROR_MSG);
        ScoreAndCertaintyMultiplierScorerConf scorerConf = (ScoreAndCertaintyMultiplierScorerConf) factoryConfig;

        return new ScoreAndCertaintyMultiplierScorer(scorerConf.getName(),
                factoryService.getProduct(scorerConf.getBaseScorerConf()));
    }
}
