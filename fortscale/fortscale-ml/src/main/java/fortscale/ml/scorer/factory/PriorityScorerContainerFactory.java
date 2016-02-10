package fortscale.ml.scorer.factory;

import fortscale.ml.scorer.PriorityScorerContainer;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.config.IScorerConf;
import fortscale.ml.scorer.config.PriorityScorerContainerConf;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

@Component
public class PriorityScorerContainerFactory extends AbstractServiceAutowiringFactory<Scorer> {
    private static final String FACTORY_CONFIG_TYPE_ERROR_MSG = String.format(
            "factoryConfig must be an instance of %s.", PriorityScorerContainerConf.class.getSimpleName());

    @Override
    public String getFactoryName() {
        return PriorityScorerContainerConf.SCORER_TYPE;
    }

    @Override
    public PriorityScorerContainer getProduct(FactoryConfig factoryConfig) {
        Assert.isInstanceOf(PriorityScorerContainerConf.class, factoryConfig, FACTORY_CONFIG_TYPE_ERROR_MSG);

        PriorityScorerContainerConf PriorityScorerContainerConf = (PriorityScorerContainerConf)factoryConfig;
        List<IScorerConf> scorerConfList = PriorityScorerContainerConf.getScorerConfList();
        List<Scorer> scorers = new ArrayList<>(scorerConfList.size());
        for(IScorerConf scorerConf: scorerConfList) {
            Scorer scorer = factoryService.getProduct(scorerConf);
            scorers.add(scorer);
        }
        return new PriorityScorerContainer(PriorityScorerContainerConf.getName(), scorers);
    }
}
