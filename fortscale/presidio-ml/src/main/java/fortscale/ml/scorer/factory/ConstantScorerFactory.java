package fortscale.ml.scorer.factory;

import fortscale.ml.scorer.ConstantScorer;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.config.ConstantScorerConf;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;


@SuppressWarnings("unused")
@Component
public class ConstantScorerFactory extends AbstractServiceAutowiringFactory<Scorer> {
    private static final String FACTORY_CONFIG_TYPE_ERROR_MSG = String.format(
            "factoryConfig must be an instance of %s.", ConstantScorerFactory.class.getSimpleName());

    @Override
    public String getFactoryName() {
        return ConstantScorerConf.SCORER_TYPE;
    }

    @Override
    public Scorer getProduct(FactoryConfig factoryConfig) {
        Assert.isInstanceOf(ConstantScorerConf.class, factoryConfig, FACTORY_CONFIG_TYPE_ERROR_MSG);
        ConstantScorerConf conf = (ConstantScorerConf)factoryConfig;
        return new ConstantScorer(conf.getName(), conf.getConstantScore());
    }
}
