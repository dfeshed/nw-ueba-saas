package fortscale.ml.scorer.factory;

import fortscale.ml.scorer.ConditionalScorer;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.config.ConditionalScorerConf;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Created by YaronDL on 8/6/2017.
 */
@Component
public class ConditionalScorerFactory extends AbstractServiceAutowiringFactory<Scorer> {


    private static final String FACTORY_CONFIG_TYPE_ERROR_MSG = String.format(
            "Factory configuration must be of type %s.", ConditionalScorerConf.class.getSimpleName());

    @Override
    public String getFactoryName() {
        return ConditionalScorerConf.SCORER_TYPE;
    }


    @Override
    public Scorer getProduct(FactoryConfig factoryConfig) {
        Assert.isInstanceOf(ConditionalScorerConf.class, factoryConfig, FACTORY_CONFIG_TYPE_ERROR_MSG);
        ConditionalScorerConf conf = (ConditionalScorerConf)factoryConfig;
        Scorer scorer = factoryService.getProduct(conf.getScorer());
        return new ConditionalScorer(conf.getName(), scorer, conf.getConditionalField(), conf.getConditionalValue());
    }
}
