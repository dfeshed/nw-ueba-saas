package fortscale.ml.scorer;

import fortscale.common.feature.Feature;
import fortscale.ml.model.CategoryRarityModelWithFeatureOccurrencesData;
import fortscale.ml.model.Model;
import fortscale.ml.model.cache.ModelsCacheService;
import fortscale.ml.model.CategoryRarityModel;
import fortscale.ml.scorer.algorithms.CategoryRarityModelScorerAlgorithm;
import org.springframework.util.Assert;

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
        return this;
    }

    public CategoryRarityModelScorer setEnoughNumOfDistinctValuesToInfluence(int enoughNumOfDistinctValuesToInfluence) {
        assertEnoughNumOfDistinctValuesToInfluenceValue(enoughNumOfDistinctValuesToInfluence);
        this.enoughNumOfDistinctValuesToInfluence = enoughNumOfDistinctValuesToInfluence;
        return this;
    }

    public CategoryRarityModelScorer(String scorerName, String modelName,
                                     List<String> contextFieldNames,
                                     String featureName,
                                     int minNumOfSamplesToInfluence,
                                     int enoughNumOfSamplesToInfluence,
                                     boolean isUseCertaintyToCalculateScore,
                                     ModelsCacheService modelsCacheService,
                                     int minNumOfDistinctValuesToInfluence,
                                     int enoughNumOfDistinctValuesToInfluence,
                                     int maxRareCount,
                                     int maxNumOfRareFeatures) {

        super(scorerName, modelName, contextFieldNames, featureName, minNumOfSamplesToInfluence, enoughNumOfSamplesToInfluence, isUseCertaintyToCalculateScore, modelsCacheService);
        init(minNumOfDistinctValuesToInfluence, enoughNumOfDistinctValuesToInfluence, maxRareCount, maxNumOfRareFeatures);
    }

    /**
     * This constructor is provided in order to be able to use the scorer without the modelCacheService.
     * @param scorerName
     * @param featureName
     * @param minNumOfSamplesToInfluence
     * @param enoughNumOfSamplesToInfluence
     * @param isUseCertaintyToCalculateScore
     * @param minNumOfDistinctValuesToInfluence
     * @param enoughNumOfDistinctValuesToInfluence
     * @param maxRareCount
     * @param maxNumOfRareFeatures
     */
    public CategoryRarityModelScorer(String scorerName,
                                     String featureName,
                                     int minNumOfSamplesToInfluence,
                                     int enoughNumOfSamplesToInfluence,
                                     boolean isUseCertaintyToCalculateScore,
                                     int minNumOfDistinctValuesToInfluence,
                                     int enoughNumOfDistinctValuesToInfluence,
                                     int maxRareCount,
                                     int maxNumOfRareFeatures) {

        super(scorerName, featureName, minNumOfSamplesToInfluence, enoughNumOfSamplesToInfluence, isUseCertaintyToCalculateScore);
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

        Double count = ((CategoryRarityModelWithFeatureOccurrencesData) model).getFeatureCount(feature);

        return algorithm.calculateScore((int)Math.round(count), (CategoryRarityModel) model);
    }



}
