package fortscale.ml.scorer.factory;

import fortscale.ml.scorer.ReductionScorer;
import fortscale.ml.scorer.config.ReductionScorerConf;
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
                scorersFactoryService.getProduct(reductionScorerConf.getMainScorerConf()),
                scorersFactoryService.getProduct(reductionScorerConf.getReductionScorerConf()),
                reductionScorerConf.getReductionWeight(),
                reductionScorerConf.getReductionZeroScoreWeight()
        );
    }
}
