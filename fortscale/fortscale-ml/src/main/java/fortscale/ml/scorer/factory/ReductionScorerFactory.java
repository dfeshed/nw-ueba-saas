package fortscale.ml.scorer.factory;

import fortscale.ml.scorer.ReductionScorer;
import fortscale.ml.scorer.config.ReductionScorerConf;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class ReductionScorerFactory extends AbstractServiceAutowiringScorerFactory<ReductionScorer> {
    @Override
    public String getFactoryName() {
        return ReductionScorerConf.SCORER_TYPE;
    }

    @Override
    public ReductionScorer getProduct(FactoryConfig factoryConfig) {
        Assert.notNull(factoryConfig);
        Assert.isTrue(factoryConfig instanceof ReductionScorerConf, "factoryConfig must be instance of ReductionScorerConf");

        ReductionScorerConf reductionScorerConf = (ReductionScorerConf) factoryConfig;

        return new ReductionScorer(reductionScorerConf.getName(),
                scorersFactoryService.getProduct(reductionScorerConf.getMainScorerConf()),
                scorersFactoryService.getProduct(reductionScorerConf.getReductionScorerConf()),
                reductionScorerConf.getReductionWeight(),
                reductionScorerConf.getReductionZeroScoreWeight()
        );
    }
}
