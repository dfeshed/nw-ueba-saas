package fortscale.ml.scorer;

import fortscale.common.feature.Feature;
import fortscale.common.feature.FeatureNumericValue;
import fortscale.common.feature.FeatureStringValue;
import fortscale.ml.model.CategoryRarityModel;
import fortscale.ml.model.Model;
import fortscale.ml.model.PartitionedDataModel;
import fortscale.ml.model.cache.EventModelsCacheService;
import fortscale.ml.scorer.algorithms.CategoryRarityModelScorerAlgorithm;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;

public class CategoryRarityModelScorer extends AbstractModelTerminalScorer {
    private static final String WRONG_MODEL_TYPE_ERROR_MSG = String.format(
            "%s.calculateScore expects to get a model of type %s",
            CategoryRarityModelScorer.class.getSimpleName(),
            CategoryRarityModel.class.getSimpleName());
    private static final String WRONG_FEATURE_VALUE_TYPE_ERROR_MSG = String.format(
            "%s.calculateScore expects to get a feature value of type %s",
            CategoryRarityModelScorer.class.getSimpleName(),
            FeatureStringValue.class.getSimpleName());
    private static final String ADDITIONAL_MODELS_ERROR_MSG = String.format(
            "%s.calculateScore expects to get at most one additional model of type %s",
            CategoryRarityModelScorer.class.getSimpleName(),
            PartitionedDataModel.class.getSimpleName());

    private CategoryRarityModelScorerAlgorithm algorithm;


    public CategoryRarityModelScorer(String scorerName,
                                     String modelName,
                                     List<String> additionalModelNames,
                                     List<String> contextFieldNames,
                                     List<List<String>> additionalContextFieldNames,
                                     String featureName,
                                     int minNumOfPartitionsToInfluence,
                                     int enoughNumOfPartitionsToInfluence,
                                     boolean isUseCertaintyToCalculateScore,
                                     int maxRareCount,
                                     int maxNumOfRarePartitions,
                                     double xWithValueHalfFactor,
                                     double minProbability,
                                     EventModelsCacheService eventModelsCacheService) {

        super(scorerName, modelName, additionalModelNames, contextFieldNames, additionalContextFieldNames,
                featureName, minNumOfPartitionsToInfluence, enoughNumOfPartitionsToInfluence,
                isUseCertaintyToCalculateScore, eventModelsCacheService);

        algorithm = new CategoryRarityModelScorerAlgorithm(maxRareCount, maxNumOfRarePartitions, xWithValueHalfFactor, minProbability);
    }

    private PartitionedDataModel extractPartitionDataModel(List<Model> additionalModels){
        if (additionalModels.isEmpty()) {
            return null;
        } else {
            Model additionalModel = additionalModels.get(0);
            return  (PartitionedDataModel)additionalModel;
        }

    }

    @Override
    protected double calculateScore(Model model, List<Model> additionalModels, Feature feature) {
        String featureValue = feature.getValue().toString();
        // Ignoring empty string values
        if (!StringUtils.hasText(featureValue)) {
            return 0.0;
        }

        PartitionedDataModel partitionedDataModel = extractPartitionDataModel(additionalModels);

        CategoryRarityModel categoryRarityModel = (CategoryRarityModel)model;
        if(categoryRarityModel == null){
            categoryRarityModel = new CategoryRarityModel();
            categoryRarityModel.init(null, null, 0,
                    partitionedDataModel.getNumOfPartitions(), 0);
        }

        Double count = categoryRarityModel.getFeatureCount(featureValue);
        if (count == null) count = 0d;
        if (partitionedDataModel != null) categoryRarityModel.setNumOfPartitions(partitionedDataModel.getNumOfPartitions());
        return algorithm.calculateScore((int)Math.round(count+1), categoryRarityModel);
    }

    @Override
    protected double calculateCertainty(Model model, List<Model> additionalModels){
        PartitionedDataModel additionalModel = extractPartitionDataModel(additionalModels);
        if(model == null && additionalModel == null){
            return 1;
        }

        long numOfPartitions = 0;
        if(additionalModel != null){
            numOfPartitions = additionalModel.getNumOfPartitions();
        } else {
            if(!(model instanceof PartitionedDataModel))
            {
                throw new RuntimeException(String.format("can calculate certainty only for models of type %s, got=%s instead ",PartitionedDataModel.class,model.getClass().toString()));
            }
            numOfPartitions = ((PartitionedDataModel) model).getNumOfPartitions();
        }
        return calculateCertainty(numOfPartitions);
    }

    @Override
    protected boolean canScore(Model mainModel, List<Model> additionalModels, Feature feature){
        if(additionalModels.size()>1){
            throw new IllegalArgumentException(ADDITIONAL_MODELS_ERROR_MSG);
        }

        Model additionalModel = additionalModels.isEmpty() ? null : additionalModels.get(0);
        if (additionalModel != null) Assert.isInstanceOf(PartitionedDataModel.class, additionalModel, ADDITIONAL_MODELS_ERROR_MSG);
        if (mainModel == null && additionalModel == null) return false;

        if(mainModel!=null) {
            Assert.isInstanceOf(CategoryRarityModel.class, mainModel, WRONG_MODEL_TYPE_ERROR_MSG);
        }

        if (feature == null || feature.getValue() == null) {
            //todo: add metrics.
            return false;
        }
        Assert.hasText(feature.getName(), String.format("Feature name cannot be null, empty or blank. scorer: %s", this.toString()));
        Assert.isTrue((feature.getValue() instanceof FeatureStringValue) || (feature.getValue() instanceof FeatureNumericValue) , WRONG_FEATURE_VALUE_TYPE_ERROR_MSG);
        Assert.notNull(feature.getValue().toString(), String.format("Feature value cannot be null. feature name: %s, scorer: %s", feature.getName(), this.toString()));


        return true;
    }

    public CategoryRarityModelScorerAlgorithm getAlgorithm() {
        return algorithm;
    }

    @Override
    public String toString() {
        return "CategoryRarityModelScorer{" +
                "modelName='" + getModelName() + '\'' +
                ", contextFieldNames=" + getContextFieldNames() +
                ", featureName='" + getFeatureName() + '\'' +
                ", minNumOfSamplesToInfluence=" + getMinNumOfPartitionsToInfluence() +
                ", enoughNumOfSamplesToInfluence=" + getEnoughNumOfPartitionsToInfluence() +
                ", isUseCertaintyToCalculateScore=" + isUseCertaintyToCalculateScore() +
                '}';
    }
}
