package fortscale.ml.scorer.factory;

import fortscale.ml.scorer.ReductingScorer;
import fortscale.ml.scorer.config.ReductingScorerConf;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;

public class ReductingScorerFactory extends AbstractServiceAutowiringFactory<ReductingScorer> {
    @Override
    public String getFactoryName() {
        return ReductingScorerConf.SCORER_TYPE;
    }

    @Override
    public ReductingScorer getProduct(FactoryConfig factoryConfig) {
        ReductingScorerConf reductingScorerConf = (ReductingScorerConf) factoryConfig;
        return new ReductingScorer(reductingScorerConf.getName(),
                factoryService.getProduct(reductingScorerConf.getMainScorer()),
                factoryService.getProduct(reductingScorerConf.getReductingScorer()),
                reductingScorerConf.getReductingWeight(),
                reductingScorerConf.getReductingZeroScoreWeight()
        );
    }
}
