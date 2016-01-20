package fortscale.ml.scorer.factory;

import fortscale.ml.scorer.ReductionScorer;
import fortscale.ml.scorer.config.ReductionScorerConf;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;

public class ReductionScorerFactory extends AbstractServiceAutowiringFactory<ReductionScorer> {
    @Override
    public String getFactoryName() {
        return ReductionScorerConf.SCORER_TYPE;
    }

    @Override
    public ReductionScorer getProduct(FactoryConfig factoryConfig) {
        ReductionScorerConf reductionScorerConf = (ReductionScorerConf) factoryConfig;
        return new ReductionScorer(reductionScorerConf.getName(),
                factoryService.getProduct(reductionScorerConf.getMainScorer()),
                factoryService.getProduct(reductionScorerConf.getReductionScorer()),
                reductionScorerConf.getReductionWeight(),
                reductionScorerConf.getReductionZeroScoreWeight()
        );
    }
}
