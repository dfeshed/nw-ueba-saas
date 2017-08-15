package fortscale.ml.scorer.factory;

import fortscale.ml.scorer.ListConditionalScorer;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.config.ListConditionalScorerConf;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class ListConditionalScorerFactory extends AbstractServiceAutowiringFactory<Scorer> {


    private static final String FACTORY_CONFIG_TYPE_ERROR_MSG = String.format(
            "Factory configuration must be of type %s.", ListConditionalScorerConf.class.getSimpleName());

    @Override
    public String getFactoryName() {
        return ListConditionalScorerConf.SCORER_TYPE;
    }


    @Override
    public Scorer getProduct(FactoryConfig factoryConfig) {
        Assert.isInstanceOf(ListConditionalScorerConf.class, factoryConfig, FACTORY_CONFIG_TYPE_ERROR_MSG);
        ListConditionalScorerConf conf = (ListConditionalScorerConf)factoryConfig;
        Scorer scorer = factoryService.getProduct(conf.getScorer());
        return new ListConditionalScorer(conf.getName(), scorer, conf.getConditionalField(), conf.getConditionalValue());
    }
}
