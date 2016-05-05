package fortscale.ml.scorer;

import fortscale.common.feature.Feature;
import fortscale.common.feature.FeatureStringValue;
import fortscale.ml.model.CategoryRarityModel;
import fortscale.ml.model.Model;
import fortscale.ml.scorer.algorithms.CategoryRarityModelScorerAlgorithm;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;

public class CategoryRarityModelScorer extends AbstractModelScorer {
    private static final String WRONG_MODEL_TYPE_ERROR_MSG = String.format(
            "%s.calculateScore expects to get a model of type %s",
            CategoryRarityModelScorer.class.getSimpleName(),
            CategoryRarityModel.class.getSimpleName());
    private static final String WRONG_FEATURE_VALUE_TYPE_ERROR_MSG = String.format(
            "%s.calculateScore expects to get a feature value of type %s",
            CategoryRarityModelScorer.class.getSimpleName(),
            FeatureStringValue.class.getSimpleName());

    private int minNumOfDistinctValuesToInfluence;
    private int enoughNumOfDistinctValuesToInfluence;

    private CategoryRarityModelScorerAlgorithm algorithm;

    public static void assertMinNumOfDistinctValuesToInfluenceValue(int minNumOfDistinctValuesToInfluence) {
        Assert.isTrue(minNumOfDistinctValuesToInfluence >= 0, String.format("minNumOfDistinctValuesToInfluence must be >= 0: %d", minNumOfDistinctValuesToInfluence));
    }

    public static void assertEnoughNumOfDistinctValuesToInfluenceValue(int enoughNumOfDistinctValuesToInfluence) {
        Assert.isTrue(enoughNumOfDistinctValuesToInfluence >= 0, String.format("enoughNumOfDistinctValuesToInfluence must be >= 0: %d", enoughNumOfDistinctValuesToInfluence));
    }

    public CategoryRarityModelScorer setMinNumOfDistinctValuesToInfluence(int minNumOfDistinctValuesToInfluence) {
        assertMinNumOfDistinctValuesToInfluenceValue(minNumOfDistinctValuesToInfluence);
        this.minNumOfDistinctValuesToInfluence = minNumOfDistinctValuesToInfluence;
        if(minNumOfDistinctValuesToInfluence > enoughNumOfDistinctValuesToInfluence) {
            enoughNumOfDistinctValuesToInfluence = minNumOfDistinctValuesToInfluence;
        }
        return this;
    }

    public CategoryRarityModelScorer setEnoughNumOfDistinctValuesToInfluence(int enoughNumOfDistinctValuesToInfluence) {
        assertEnoughNumOfDistinctValuesToInfluenceValue(enoughNumOfDistinctValuesToInfluence);
        this.enoughNumOfDistinctValuesToInfluence = Math.max(enoughNumOfDistinctValuesToInfluence, minNumOfDistinctValuesToInfluence);
        return this;
    }

    public CategoryRarityModelScorer(String scorerName,
                                     String modelName,
                                     List<String> additionalModelNames,
                                     List<String> contextFieldNames,
                                     List<List<String>> additionalContextFieldNames,
                                     String featureName,
                                     int minNumOfSamplesToInfluence,
                                     int enoughNumOfSamplesToInfluence,
                                     boolean isUseCertaintyToCalculateScore,
                                     int minNumOfDistinctValuesToInfluence,
                                     int enoughNumOfDistinctValuesToInfluence,
                                     int maxRareCount,
                                     int maxNumOfRareFeatures) {

        super(scorerName, modelName, additionalModelNames, contextFieldNames, additionalContextFieldNames,
                featureName, minNumOfSamplesToInfluence, enoughNumOfSamplesToInfluence, isUseCertaintyToCalculateScore);
        setMinNumOfDistinctValuesToInfluence(minNumOfDistinctValuesToInfluence);
        setEnoughNumOfDistinctValuesToInfluence(enoughNumOfDistinctValuesToInfluence);
        algorithm = new CategoryRarityModelScorerAlgorithm(maxRareCount, maxNumOfRareFeatures);
    }

    @Override
    protected double calculateCertainty(Model model) {
        double certainty = super.calculateCertainty(model);
        if(enoughNumOfDistinctValuesToInfluence < 2){
            return certainty;
        }

        if(!(model instanceof CategoryRarityModel)){
            return certainty;
        }


        CategoryRarityModel categoryRarityModel = (CategoryRarityModel) model;
        long numOfDistinctFeatures = categoryRarityModel.getNumOfDistinctFeatures();
        double distinctCertainty = 0;
        if(numOfDistinctFeatures >= enoughNumOfDistinctValuesToInfluence){
            distinctCertainty = 1;
        } else if(numOfDistinctFeatures >= minNumOfDistinctValuesToInfluence){
            distinctCertainty = ((double)(numOfDistinctFeatures - minNumOfDistinctValuesToInfluence + 1)) / (enoughNumOfDistinctValuesToInfluence - minNumOfDistinctValuesToInfluence + 1);
        }


        return certainty*distinctCertainty;
    }

    @Override
    public double calculateScore(Model model, List<Model> additionalModels, Feature feature) {
        Assert.isInstanceOf(CategoryRarityModel.class, model, WRONG_MODEL_TYPE_ERROR_MSG);
        if (additionalModels.size() > 0) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + " doesn't expect to get additional models");
        }
        Assert.notNull(feature, "Feature cannot be null");
        Assert.hasText(feature.getName(), String.format("Feature name cannot be null, empty or blank. scorer: %s", this.toString()));
        Assert.isInstanceOf(FeatureStringValue.class, feature.getValue(), WRONG_FEATURE_VALUE_TYPE_ERROR_MSG);
        Assert.notNull(feature.getValue().toString(), String.format("Feature value cannot be null. feature name: %s, scorer: %s", feature.getName(), this.toString()));

        // Ignoring empty string values
        if(!StringUtils.hasText(feature.getValue().toString())) {
            return 0.0;
        }

        Double count = ((CategoryRarityModel)model).getFeatureCount(feature.getValue().toString());
        if (count == null) count = 1d; // The scorer should handle it as if count = 1
        return algorithm.calculateScore((int)Math.round(count), (CategoryRarityModel)model);
    }

    public int getMinNumOfDistinctValuesToInfluence() {
        return minNumOfDistinctValuesToInfluence;
    }

    public int getEnoughNumOfDistinctValuesToInfluence() {
        return enoughNumOfDistinctValuesToInfluence;
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
                ", minNumOfSamplesToInfluence=" + getMinNumOfSamplesToInfluence() +
                ", enoughNumOfSamplesToInfluence=" + getEnoughNumOfSamplesToInfluence() +
                ", isUseCertaintyToCalculateScore=" + isUseCertaintyToCalculateScore() +
                ", minNumOfDistinctValuesToInfluence=" + minNumOfDistinctValuesToInfluence +
                ", enoughNumOfDistinctValuesToInfluence=" + enoughNumOfDistinctValuesToInfluence +
                '}';
    }
}
