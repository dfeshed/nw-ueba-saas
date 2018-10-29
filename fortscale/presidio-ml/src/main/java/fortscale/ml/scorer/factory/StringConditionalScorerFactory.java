package fortscale.ml.scorer.factory;

import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.StringConditionalScorer;
import fortscale.ml.scorer.config.StringConditionalScorerConf;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;


@Component
public class StringConditionalScorerFactory extends AbstractServiceAutowiringFactory<Scorer> {


    private static final String FACTORY_CONFIG_TYPE_ERROR_MSG = String.format(
            "Factory configuration must be of type %s.", StringConditionalScorerFactory.class.getSimpleName());

    @Override
    public String getFactoryName() {
        return StringConditionalScorerConf.SCORER_TYPE;
    }


    @Override
    public Scorer getProduct(FactoryConfig factoryConfig) {
        Assert.isInstanceOf(StringConditionalScorerConf.class, factoryConfig, FACTORY_CONFIG_TYPE_ERROR_MSG);
        StringConditionalScorerConf conf = (StringConditionalScorerConf)factoryConfig;
        Scorer scorer = factoryService.getProduct(conf.getScorer());
        return new StringConditionalScorer(conf.getName(), scorer, conf.getConditionalField(), conf.getConditionalValue());
    }
}
