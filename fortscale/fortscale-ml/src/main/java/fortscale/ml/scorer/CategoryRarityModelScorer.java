package fortscale.ml.scorer;

import fortscale.common.feature.Feature;
import fortscale.ml.model.CategoryRarityModelWithFeatureOccurrencesData;
import fortscale.ml.model.Model;
import fortscale.ml.model.cache.ModelsCacheService;
import fortscale.ml.model.CategoryRarityModel;
import fortscale.ml.scorer.algorithms.CategoryRarityModelScorerAlgorithm;

import java.util.List;


public class CategoryRarityModelScorer extends AbstractModelScorer {

    private int minNumOfDiscreetValuesToInfluence;
    private int enoughNumOfDiscreetValuesToInfluence;

    private CategoryRarityModelScorerAlgorithm algorithm;

    public CategoryRarityModelScorer(String scorerName, String modelName,
                                     List<String> contextFieldNames,
                                     String featureName,
                                     int minNumOfSamplesToInfluence,
                                     int enoughNumOfSamplesToInfluence,
                                     boolean isUseCertaintyToCalculateScore,
                                     ModelsCacheService modelsCacheService,
                                     int minNumOfDiscreetValuesToInfluence,
                                     int enoughNumOfDiscreetValuesToInfluence,
                                     int maxRareCount,
                                     int maxNumOfRareFeatures) {

        super(scorerName, modelName, contextFieldNames, featureName, minNumOfSamplesToInfluence, enoughNumOfSamplesToInfluence, isUseCertaintyToCalculateScore, modelsCacheService);
        this.enoughNumOfDiscreetValuesToInfluence = enoughNumOfDiscreetValuesToInfluence;
        this.minNumOfDiscreetValuesToInfluence = minNumOfDiscreetValuesToInfluence;

        algorithm = new CategoryRarityModelScorerAlgorithm(maxRareCount, maxNumOfRareFeatures);
    }

    /**
     * This constructor is provided in order to be able to use the scorer without the modelCacheService.
     * @param scorerName
     * @param featureName
     * @param minNumOfSamplesToInfluence
     * @param enoughNumOfSamplesToInfluence
     * @param isUseCertaintyToCalculateScore
     * @param minNumOfDiscreetValuesToInfluence
     * @param enoughNumOfDiscreetValuesToInfluence
     * @param maxRareCount
     * @param maxNumOfRareFeatures
     */
    public CategoryRarityModelScorer(String scorerName,
                                     String featureName,
                                     int minNumOfSamplesToInfluence,
                                     int enoughNumOfSamplesToInfluence,
                                     boolean isUseCertaintyToCalculateScore,
                                     int minNumOfDiscreetValuesToInfluence,
                                     int enoughNumOfDiscreetValuesToInfluence,
                                     int maxRareCount,
                                     int maxNumOfRareFeatures) {

        super(scorerName, featureName, minNumOfSamplesToInfluence, enoughNumOfSamplesToInfluence, isUseCertaintyToCalculateScore);
        this.enoughNumOfDiscreetValuesToInfluence = enoughNumOfDiscreetValuesToInfluence;
        this.minNumOfDiscreetValuesToInfluence = minNumOfDiscreetValuesToInfluence;

        algorithm = new CategoryRarityModelScorerAlgorithm(maxRareCount, maxNumOfRareFeatures);
    }

    @Override
    protected double calculateCertainty(Model model) {
        double certainty = super.calculateCertainty(model);
        if(enoughNumOfDiscreetValuesToInfluence < 2){
            return certainty;
        }

        if(!(model instanceof CategoryRarityModel)){
            return certainty;
        }


        CategoryRarityModel categoryRarityModel = (CategoryRarityModel) model;
        long numOfDistinctRareFeatures = categoryRarityModel.getNumOfDistinctRareFeatures();
        double discreetCertainty = 0;
        if(numOfDistinctRareFeatures >= enoughNumOfDiscreetValuesToInfluence){
            discreetCertainty = 1;
        } else if(numOfDistinctRareFeatures >= minNumOfDiscreetValuesToInfluence){
            discreetCertainty = ((double)(numOfDistinctRareFeatures - minNumOfDiscreetValuesToInfluence + 1)) / (enoughNumOfDiscreetValuesToInfluence - minNumOfDiscreetValuesToInfluence + 1);
        }


        return certainty*discreetCertainty;
    }

    @Override
    public double calculateScore(Model model, Feature feature) {
        if(model==null || !(model instanceof CategoryRarityModelWithFeatureOccurrencesData)) {
            return 0;
        }

        if(feature==null || feature.getValue()==null) {
            return 0;
        }

        Double count = ((CategoryRarityModelWithFeatureOccurrencesData) model).getFeatureCount(feature);

        return algorithm.calculateScore((int)Math.round(count), (CategoryRarityModel) model);
    }



}
