package fortscale.ml.scorer.factory;

import fortscale.ml.model.ModelConf;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.AbstractDataRetrieverConf;
import fortscale.ml.scorer.CategoryRarityModelScorer;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.config.CategoryRarityModelScorerConf;
import fortscale.ml.scorer.config.ModelInfo;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CategoryRarityModelScorerFactory extends AbstractModelScorerFactory {
    @Override
    public String getFactoryName() {
        return CategoryRarityModelScorerConf.SCORER_TYPE;
    }

    @Override
    public Scorer getProduct(FactoryConfig factoryConfig) {
        Assert.isInstanceOf(CategoryRarityModelScorerConf.class, factoryConfig);
        CategoryRarityModelScorerConf scorerConf = (CategoryRarityModelScorerConf)factoryConfig;
        super.validateModelScorerConf(scorerConf);

        String modelName = scorerConf.getModelInfo().getModelName();
        List<String> additionalModelNames = scorerConf.getAdditionalModelInfos().stream()
                .map(ModelInfo::getModelName)
                .collect(Collectors.toList());
        AbstractDataRetriever dataRetriever = getDataRetriever(modelName);
        List<String> contextFieldNames = dataRetriever.getContextFieldNames();
        List<List<String>> additionalContextFieldNames = additionalModelNames.stream()
                .map(additionalModelName -> modelConfService
                        .getModelConf(additionalModelName)
                        .getContextSelectorConf() != null ?
                        getDataRetriever(additionalModelName).getContextFieldNames() : new ArrayList<String>())
                .collect(Collectors.toList());
        Set<String> featureNames = dataRetriever.getEventFeatureNames();

        // Currently in this implementation we use only single feature per model.
        String featureName = featureNames.iterator().next();

        return new CategoryRarityModelScorer(
                scorerConf.getName(),
                modelName,
                additionalModelNames,
                contextFieldNames,
                additionalContextFieldNames,
                featureName,
                scorerConf.getMinNumOfSamplesToInfluence(),
                scorerConf.getEnoughNumOfSamplesToInfluence(),
                scorerConf.isUseCertaintyToCalculateScore(),
                scorerConf.getMinNumOfDistinctValuesToInfluence(),
                scorerConf.getEnoughNumOfDistinctValuesToInfluence(),
                scorerConf.getMaxRareCount(),
                scorerConf.getMaxNumOfRareFeatures()
        );
    }

    private AbstractDataRetriever getDataRetriever(String modelName) {
        ModelConf modelConf = modelConfService.getModelConf(modelName);
        AbstractDataRetrieverConf dataRetrieverConf = modelConf.getDataRetrieverConf();
        return dataRetrieverFactoryService.getProduct(dataRetrieverConf);
    }
}
