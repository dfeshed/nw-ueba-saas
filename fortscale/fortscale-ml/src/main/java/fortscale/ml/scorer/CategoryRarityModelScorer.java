package fortscale.ml.scorer;

import fortscale.common.feature.Feature;
import fortscale.common.feature.FeatureStringValue;
import fortscale.ml.model.CategoryRarityModelWithFeatureOccurrencesData;
import fortscale.ml.model.Model;
import fortscale.ml.model.CategoryRarityModel;
import fortscale.ml.scorer.algorithms.CategoryRarityModelScorerAlgorithm;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;


public class CategoryRarityModelScorer extends AbstractModelScorer {

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
                                     List<String> contextFieldNames,
                                     String featureName,
                                     int minNumOfSamplesToInfluence,
                                     int enoughNumOfSamplesToInfluence,
                                     boolean isUseCertaintyToCalculateScore,
                                     int minNumOfDistinctValuesToInfluence,
                                     int enoughNumOfDistinctValuesToInfluence,
                                     int maxRareCount,
                                     int maxNumOfRareFeatures) {

        super(scorerName, modelName, contextFieldNames, featureName, minNumOfSamplesToInfluence, enoughNumOfSamplesToInfluence, isUseCertaintyToCalculateScore);
        init(minNumOfDistinctValuesToInfluence, enoughNumOfDistinctValuesToInfluence, maxRareCount, maxNumOfRareFeatures);
    }

    private void init(int minNumOfDistinctValuesToInfluence, int enoughNumOfDistinctValuesToInfluence, int maxRareCount, int maxNumOfRareFeatures) {
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
        long numOfDistinctRareFeatures = categoryRarityModel.getNumOfDistinctRareFeatures();
        double distinctCertainty = 0;
        if(numOfDistinctRareFeatures >= enoughNumOfDistinctValuesToInfluence){
            distinctCertainty = 1;
        } else if(numOfDistinctRareFeatures >= minNumOfDistinctValuesToInfluence){
            distinctCertainty = ((double)(numOfDistinctRareFeatures - minNumOfDistinctValuesToInfluence + 1)) / (enoughNumOfDistinctValuesToInfluence - minNumOfDistinctValuesToInfluence + 1);
        }


        return certainty*distinctCertainty;
    }

    @Override
    public double calculateScore(Model model, Feature feature) {
        if(!(model instanceof CategoryRarityModelWithFeatureOccurrencesData)) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() +
                    ".calculateScore expects to get a model of type " + CategoryRarityModelWithFeatureOccurrencesData.class.getSimpleName());
        }

        Assert.notNull(feature, "Feature cannot be null");
        Assert.isTrue(!StringUtils.isEmpty(feature.getName()) && StringUtils.hasText(feature.getName()), "Feature name cannot be null or empty");
        Assert.notNull(feature.getValue(), "Feature value cannot be null");
        if(feature.getValue() instanceof FeatureStringValue) {
            Assert.isTrue(!StringUtils.isEmpty(((FeatureStringValue) feature.getValue()).getValue())
                    && StringUtils.hasText(((FeatureStringValue) feature.getValue()).getValue()), "Feature value cannot be null or empty");
        }

        Double count = ((CategoryRarityModelWithFeatureOccurrencesData) model).getFeatureCount(feature);

        if(count==null) {
            count = 1d; // The scorer should handle it as if count=1
        }

        return algorithm.calculateScore((int)Math.round(count), (CategoryRarityModel) model);
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
}
