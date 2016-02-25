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
        Assert.notNull(factoryConfig);
        Assert.isTrue(factoryConfig instanceof  CategoryRarityModelScorerConf);
        CategoryRarityModelScorerConf scorerConf = (CategoryRarityModelScorerConf) factoryConfig;
        String modelName = scorerConf.getModelInfo().getModelName();
        List<String> additionalModelNames = scorerConf.getAdditionalModelInfos().stream()
                .map(ModelInfo::getModelName)
                .collect(Collectors.toList());
        ModelConf modelConf = modelConfService.getModelConf(modelName);
        AbstractDataRetrieverConf dataRetrieverConf = modelConf.getDataRetrieverConf();
        AbstractDataRetriever abstractDataRetriever = dataRetrieverFactoryService.getProduct(dataRetrieverConf);
        List<String> contextFieldNames = abstractDataRetriever.getContextFieldNames();
        Set<String> featureNames = abstractDataRetriever.getEventFeatureNames();

        // Currently in this implementation we use only single feature per model.
        String featureName = featureNames.iterator().next();

        return new CategoryRarityModelScorer(
                scorerConf.getName(),
                modelName,
                additionalModelNames,
                contextFieldNames,
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
}
