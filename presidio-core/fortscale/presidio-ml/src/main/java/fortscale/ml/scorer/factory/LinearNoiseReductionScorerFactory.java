package fortscale.ml.scorer.factory;

import fortscale.ml.model.ModelConf;
import fortscale.ml.model.ModelConfService;
import fortscale.ml.model.cache.EventModelsCacheService;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.AbstractDataRetrieverConf;
import fortscale.ml.scorer.LinearNoiseReductionScorer;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.config.LinearNoiseReductionScorerConf;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import fortscale.utils.factory.FactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Set;

@Component
public class LinearNoiseReductionScorerFactory extends AbstractServiceAutowiringFactory<Scorer> {
    @Autowired
    protected FactoryService<AbstractDataRetriever> dataRetrieverFactoryService;
    @Autowired
    protected ModelConfService modelConfService;
    @Autowired
    protected EventModelsCacheService eventModelsCacheService;

    @Override
    public String getFactoryName() {
        return LinearNoiseReductionScorerConf.SCORER_TYPE;
    }

    @Override
    public Scorer getProduct(FactoryConfig factoryConfig) {
        Assert.isInstanceOf(LinearNoiseReductionScorerConf.class, factoryConfig);
        LinearNoiseReductionScorerConf scorerConf = (LinearNoiseReductionScorerConf) factoryConfig;

        //category rarity model to extract feature count
        String featureCountModelName = scorerConf.getFeatureCountModelInfo().getModelName();
        AbstractDataRetriever featureCountDataRetriever = getDataRetriever(featureCountModelName);
        Set<String> featureCountModelFeatureNames = featureCountDataRetriever.getEventFeatureNames();
        String featureCountModelFeatureName = featureCountModelFeatureNames.iterator().next();
        List<String> featureCountModelContextFieldNames = featureCountDataRetriever.getContextFieldNames();

        //occurrencesToNumOfDistinctFeatureValue model
        String occurrencesToNumOfDistinctFeatureValueModelName = scorerConf.getOccurrencesToNumOfDistinctFeatureValueModelInfo().getModelName();
        AbstractDataRetriever occurrencesToNumOfDistinctFeatureValueDataRetriever = getDataRetriever(occurrencesToNumOfDistinctFeatureValueModelName);
        List<String> occurrencesToNumOfDistinctFeatureValueContextFieldNames = occurrencesToNumOfDistinctFeatureValueDataRetriever.getContextFieldNames();

        //context model
        String contextModelName = scorerConf.getContextModelInfo().getModelName();
        AbstractDataRetriever contextModelDataRetriever = getDataRetriever(contextModelName);
        List<String> contextModelContextFieldNames = contextModelDataRetriever.getContextFieldNames();

        return new LinearNoiseReductionScorer(
                scorerConf.getName(),
                factoryService.getProduct(scorerConf.getMainScorerConf()),
                factoryService.getProduct(scorerConf.getReductionScorerConf()),
                occurrencesToNumOfDistinctFeatureValueModelName,
                occurrencesToNumOfDistinctFeatureValueContextFieldNames,
                featureCountModelName,
                featureCountModelFeatureName,
                featureCountModelContextFieldNames,
                contextModelName,
                contextModelContextFieldNames,
                scorerConf.getNoiseReductionWeight(),
                eventModelsCacheService,
                scorerConf.getMaxRareCount(),
                scorerConf.getxWithValueHalfFactor(),
                scorerConf.getEpsilonValueForMaxX());
    }

    private AbstractDataRetriever getDataRetriever(String modelName) {
        ModelConf modelConf = modelConfService.getModelConf(modelName);
        AbstractDataRetrieverConf dataRetrieverConf = modelConf.getDataRetrieverConf();
        return dataRetrieverFactoryService.getProduct(dataRetrieverConf);
    }
}
