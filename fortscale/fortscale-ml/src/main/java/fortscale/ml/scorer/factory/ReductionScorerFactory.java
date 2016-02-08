package fortscale.ml.scorer.factory;

import fortscale.ml.scorer.ReductionScorer;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.config.ReductionScorerConf;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class ReductionScorerFactory extends AbstractServiceAutowiringFactory<Scorer> {
    private static final String FACTORY_CONFIG_TYPE_ERROR_MSG = String.format(
            "factoryConfig must be an instance of %s.", ReductionScorerConf.class.getSimpleName());

    @Override
    public String getFactoryName() {
        return ReductionScorerConf.SCORER_TYPE;
    }

    @Override
    public ReductionScorer getProduct(FactoryConfig factoryConfig) {
        Assert.isInstanceOf(ReductionScorerConf.class, factoryConfig, FACTORY_CONFIG_TYPE_ERROR_MSG);
        ReductionScorerConf reductionScorerConf = (ReductionScorerConf) factoryConfig;

        return new ReductionScorer(reductionScorerConf.getName(),
                factoryService.getProduct(reductionScorerConf.getMainScorerConf()),
                factoryService.getProduct(reductionScorerConf.getReductionScorerConf()),
                reductionScorerConf.getReductionWeight(),
                reductionScorerConf.getReductionZeroScoreWeight()
        );
    }
}
