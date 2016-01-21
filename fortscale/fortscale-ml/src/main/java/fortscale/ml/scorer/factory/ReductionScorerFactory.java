package fortscale.ml.scorer.factory;

import fortscale.ml.scorer.ReductionScorer;
import fortscale.ml.scorer.config.ReductionScorerConf;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;

@Component
public class ReductionScorerFactory extends AbstractServiceAutowiringScorerFactory<ReductionScorer> {
    @Override
    public String getFactoryName() {
        return ReductionScorerConf.SCORER_TYPE;
    }

    @Override
    public ReductionScorer getProduct(FactoryConfig factoryConfig) {
        ReductionScorerConf reductionScorerConf = (ReductionScorerConf) factoryConfig;
        return new ReductionScorer(reductionScorerConf.getName(),
                scorersFactoryService.getProduct(reductionScorerConf.getMainScorer()),
                scorersFactoryService.getProduct(reductionScorerConf.getReductionScorer()),
                reductionScorerConf.getReductionWeight(),
                reductionScorerConf.getReductionZeroScoreWeight()
        );
    }
}
