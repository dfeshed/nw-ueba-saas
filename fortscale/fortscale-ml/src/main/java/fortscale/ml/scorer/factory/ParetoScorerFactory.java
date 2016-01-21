package fortscale.ml.scorer.factory;

import fortscale.ml.scorer.ParetoScorer;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.config.IScorerConf;
import fortscale.ml.scorer.config.ParetoScorerConf;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class ParetoScorerFactory extends AbstractServiceAutowiringScorerFactory<ParetoScorer> {

    @Override
    public String getFactoryName() {
        return ParetoScorerConf.SCORER_TYPE;
    }

    @Override
    public ParetoScorer getProduct(FactoryConfig factoryConfig) {
        ParetoScorerConf paretoScorerConf = (ParetoScorerConf)factoryConfig;
        List<IScorerConf> scorerConfList = paretoScorerConf.getScorerConfList();
        List<Scorer> scorers = new ArrayList<>(scorerConfList.size());
        for(IScorerConf scorerConf: scorerConfList) {
            Scorer scorer = scorersFactoryService.getProduct(scorerConf);
            scorers.add(scorer);
        }
        return new ParetoScorer(paretoScorerConf.getName(), scorers, paretoScorerConf.getHighestScoreWeight());
    }
}
