package fortscale.ml.scorer.factory;

import fortscale.ml.model.ModelConf;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.AbstractDataRetrieverConf;
import fortscale.ml.scorer.CategoryRarityModelScorer;
import fortscale.ml.scorer.config.CategoryRarityModelScorerConf;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Set;

@Component
public class CategoryRarityModelScorerFactory extends AbstractModelScorerFactory<CategoryRarityModelScorer> {

    @Override
    public String getFactoryName() {
        return CategoryRarityModelScorerConf.SCORER_TYPE;
    }

    @Override
    public Object getProduct(FactoryConfig factoryConfig) {
        Assert.notNull(modelsCacheService);
        CategoryRarityModelScorerConf scorerConf = (CategoryRarityModelScorerConf) factoryConfig;
        String modelName = scorerConf.getModelInfo().getModelName();
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
                contextFieldNames,
                featureName,
                scorerConf.getMinNumOfSamplesToInfluence(),
                scorerConf.getEnoughNumOfSamplesToInfluence(),
                scorerConf.isUseCertaintyToCalculateScore(),
                modelsCacheService, scorerConf.getMinNumOfDistinctValuesToInfluence(),
                scorerConf.getEnoughNumOfDistinctValuesToInfluence(),
                scorerConf.getMaxRareCount(),
                scorerConf.getMaxNumOfRareFeatures()
        );

    }
}
