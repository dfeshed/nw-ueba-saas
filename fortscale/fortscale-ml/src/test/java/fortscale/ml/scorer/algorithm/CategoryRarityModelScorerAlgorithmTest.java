package fortscale.ml.scorer.algorithm;

import fortscale.ml.model.CategoryRarityModel;
import fortscale.ml.model.builder.CategoryRarityModelBuilder;
import fortscale.ml.scorer.algorithms.CategoryRarityModelScorerAlgorithm;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class CategoryRarityModelScorerAlgorithmTest {

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenNegativeAsMaxRareCount() {
        new CategoryRarityModelScorerAlgorithm(-1, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenNegativeAsMaxNumOfRareFeatures() {
        new CategoryRarityModelScorerAlgorithm(1, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenTooLargeMaxRareCountValue() {
        new CategoryRarityModelScorerAlgorithm(CategoryRarityModel.NUM_OF_BUCKETS, 1);
    }

    @Test
    public void shouldScoreAccordingToMaxRareCount() {
        Map<String, Integer> modelBuilderData = new HashMap<>();
        int rareCount = 4;
        modelBuilderData.put("commonValue", 1000);
        modelBuilderData.put("rareValue", rareCount);
        CategoryRarityModelBuilder categoryRarityModelBuilder = new CategoryRarityModelBuilder();
        
        CategoryRarityModel categoryRarityModel = (CategoryRarityModel) categoryRarityModelBuilder.build(modelBuilderData);
        int maxNumOfRareFeatures = 10;
        CategoryRarityModelScorerAlgorithm algorithmWithSmallMaxRareCount = new CategoryRarityModelScorerAlgorithm(rareCount - 1, maxNumOfRareFeatures);
        CategoryRarityModelScorerAlgorithm algorithmWithBigMaxRareCount = new CategoryRarityModelScorerAlgorithm(rareCount + 1, maxNumOfRareFeatures);

        double scoreWithBigMaxRareCount = algorithmWithBigMaxRareCount.calculateScore(rareCount, categoryRarityModel);
        double scoreWithSmallMaxRareCount = algorithmWithSmallMaxRareCount.calculateScore(rareCount, categoryRarityModel);
        Assert.assertTrue(String.format("scoreWithBigMaxRareCount (%f) should be > scoreWithSmallMaxRareCount (%f)", scoreWithBigMaxRareCount, scoreWithSmallMaxRareCount),
                scoreWithBigMaxRareCount > scoreWithSmallMaxRareCount);
    }

    @Test
    public void shouldScoreAccordingToMaxNumOfRareFeatures() {
        int maxRareCount = 10;
        int rareCount = 1;
        int commonCount = 1000;
        int numOfFeatures = 20;
        Map<String, Integer> modelBuilderData = new HashMap<>();
        modelBuilderData.put("rareValue", rareCount);
        for (int i = 0; i < numOfFeatures; i += 2) {
            modelBuilderData.put("commonValue-" + i, commonCount);
        }
        CategoryRarityModelBuilder categoryRarityModelBuilder = new CategoryRarityModelBuilder();
        CategoryRarityModel categoryRarityModel = (CategoryRarityModel) categoryRarityModelBuilder.build(modelBuilderData);

        CategoryRarityModelScorerAlgorithm algorithmWithBigMaxNumOfRareFeatures = new CategoryRarityModelScorerAlgorithm(maxRareCount, numOfFeatures - 10);
        CategoryRarityModelScorerAlgorithm algorithmWithSmallMaxNumOfRareFeatures = new CategoryRarityModelScorerAlgorithm(maxRareCount, numOfFeatures - 15);

        double scoreWithBigMaxNumOfRareFeatures = algorithmWithBigMaxNumOfRareFeatures.calculateScore(rareCount, categoryRarityModel);
        double scoreWithSmallMaxNumOfRareFeatures = algorithmWithSmallMaxNumOfRareFeatures.calculateScore(rareCount, categoryRarityModel);
        Assert.assertTrue(scoreWithBigMaxNumOfRareFeatures > scoreWithSmallMaxNumOfRareFeatures);
    }

}
