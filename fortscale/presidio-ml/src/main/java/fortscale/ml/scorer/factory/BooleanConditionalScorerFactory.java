package fortscale.ml.scorer.factory;

import fortscale.ml.scorer.BooleanConditionalScorer;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.config.BooleanConditionalScorerConf;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class BooleanConditionalScorerFactory extends AbstractServiceAutowiringFactory<Scorer> {


    private static final String FACTORY_CONFIG_TYPE_ERROR_MSG = String.format(
            "Factory configuration must be of type %s.", BooleanConditionalScorerConf.class.getSimpleName());

    @Override
    public String getFactoryName() {
        return BooleanConditionalScorerConf.SCORER_TYPE;
    }


    @Override
    public Scorer getProduct(FactoryConfig factoryConfig) {
        Assert.isInstanceOf(BooleanConditionalScorerConf.class, factoryConfig, FACTORY_CONFIG_TYPE_ERROR_MSG);
        BooleanConditionalScorerConf conf = (BooleanConditionalScorerConf)factoryConfig;
        Scorer scorer = factoryService.getProduct(conf.getScorer());
        return new BooleanConditionalScorer(conf.getName(), scorer, conf.getConditionalField(), conf.getConditionalValue());
    }
}
