package fortscale.ml.scorer.factory;

import fortscale.ml.scorer.ParetoScorer;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.config.IScorerConf;
import fortscale.ml.scorer.config.ParetoScorerConf;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

@Component
public class ParetoScorerFactory extends AbstractServiceAutowiringFactory<Scorer> {
    private static final String FACTORY_CONFIG_TYPE_ERROR_MSG = String.format(
            "factoryConfig must be an instance of %s.", ParetoScorerConf.class.getSimpleName());

    @Override
    public String getFactoryName() {
        return ParetoScorerConf.SCORER_TYPE;
    }

    @Override
    public ParetoScorer getProduct(FactoryConfig factoryConfig) {
        Assert.isInstanceOf(ParetoScorerConf.class, factoryConfig, FACTORY_CONFIG_TYPE_ERROR_MSG);

        ParetoScorerConf paretoScorerConf = (ParetoScorerConf)factoryConfig;
        List<IScorerConf> scorerConfList = paretoScorerConf.getScorerConfList();
        List<Scorer> scorers = new ArrayList<>(scorerConfList.size());
        for (IScorerConf scorerConf : scorerConfList) {
            Scorer scorer = factoryService.getProduct(scorerConf);
            Assert.notNull(scorer, String.format("Factory service produced a null scorer for conf %s.", scorerConf.getName()));
            scorers.add(scorer);
        }

        return new ParetoScorer(paretoScorerConf.getName(), scorers, paretoScorerConf.getHighestScoreWeight());
    }
}
