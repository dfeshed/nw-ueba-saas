package fortscale.ml.scorer.factory;

import fortscale.ml.scorer.ConditionalScorer;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.config.ConditionalScorerConf;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class ConditionalScorerFactory extends AbstractServiceAutowiringFactory<Scorer> {
    private static final String FACTORY_CONFIG_ERROR_MESSAGE = String.format(
            "factoryConfig must be an instance of %s.", ConditionalScorerConf.class.getSimpleName());

    @Override
    public String getFactoryName() {
        return ConditionalScorerConf.SCORER_TYPE;
    }

    @Override
    public Scorer getProduct(FactoryConfig factoryConfig) {
        Assert.isInstanceOf(ConditionalScorerConf.class, factoryConfig, FACTORY_CONFIG_ERROR_MESSAGE);
        ConditionalScorerConf conditionalScorerConf = (ConditionalScorerConf)factoryConfig;
        Scorer scorer = factoryService.getProduct(conditionalScorerConf.getScorerConf());
        return new ConditionalScorer(conditionalScorerConf.getName(), conditionalScorerConf.getPredicates(), scorer);
    }
}
