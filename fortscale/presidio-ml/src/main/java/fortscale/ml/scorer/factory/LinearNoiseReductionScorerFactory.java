package fortscale.ml.scorer.factory;

import fortscale.ml.model.ModelConf;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.AbstractDataRetrieverConf;
import fortscale.ml.scorer.LinearNoiseReductionScorer;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.config.LinearNoiseReductionScorerConf;
import fortscale.ml.scorer.config.ModelInfo;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class LinearNoiseReductionScorerFactory extends AbstractModelScorerFactory {
    @Override
    public String getFactoryName() {
        return LinearNoiseReductionScorerConf.SCORER_TYPE;
    }

    @Override
    public Scorer getProduct(FactoryConfig factoryConfig) {
        Assert.isInstanceOf(LinearNoiseReductionScorerConf.class, factoryConfig);
        LinearNoiseReductionScorerConf scorerConf = (LinearNoiseReductionScorerConf) factoryConfig;
        super.validateModelScorerConf(scorerConf);

        //main model
        String modelName = scorerConf.getModelInfo().getModelName();
        AbstractDataRetriever dataRetriever = getDataRetriever(modelName);
        List<String> contextFieldNames = dataRetriever.getContextFieldNames();

        //reduction model
        String reductionModelName = scorerConf.getReductionModelInfo().getModelName();
        AbstractDataRetriever reductionDataRetriever = getDataRetriever(reductionModelName);
        Set<String> reductionFeatureNames = reductionDataRetriever.getEventFeatureNames();
        String reductionFeatureName = reductionFeatureNames.iterator().next();
        List<String> reductionContextFieldNames = reductionDataRetriever.getContextFieldNames();

        //additional model
        List<String> additionalModelNames = scorerConf.getAdditionalModelInfos().stream()
                .map(ModelInfo::getModelName)
                .collect(Collectors.toList());
        List<List<String>> additionalContextFieldNames = additionalModelNames.stream()
                .map(additionalModelName -> modelConfService
                        .getModelConf(additionalModelName)
                        .getContextSelectorConf() != null ?
                        getDataRetriever(additionalModelName).getContextFieldNames() : new ArrayList<String>())
                .collect(Collectors.toList());

        return new LinearNoiseReductionScorer(
                scorerConf.getName(),
                factoryService.getProduct(scorerConf.getMainScorerConf()),
                factoryService.getProduct(scorerConf.getReductionScorerConf()),
                modelName,
                contextFieldNames,
                reductionModelName,
                reductionFeatureName,
                reductionContextFieldNames,
                additionalModelNames,
                additionalContextFieldNames,
                scorerConf.getNoiseReductionWeight(),
                scorerConf.getMinNumOfSamplesToInfluence(),
                scorerConf.getEnoughNumOfSamplesToInfluence(),
                scorerConf.isUseCertaintyToCalculateScore(),
                eventModelsCacheService);
    }

    private AbstractDataRetriever getDataRetriever(String modelName) {
        ModelConf modelConf = modelConfService.getModelConf(modelName);
        AbstractDataRetrieverConf dataRetrieverConf = modelConf.getDataRetrieverConf();
        return dataRetrieverFactoryService.getProduct(dataRetrieverConf);
    }
}
