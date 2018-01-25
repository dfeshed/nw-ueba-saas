package fortscale.ml.scorer.algorithms;

import fortscale.ml.model.CategoryRarityModel;
import fortscale.ml.model.Sigmoid;
import fortscale.utils.logging.Logger;
import org.springframework.util.Assert;

import java.util.List;

/**
 * For documentation and explanation of how this scoring algorithm works - refer to https://fortscale.atlassian.net/wiki/display/FSC/category+rarity+model
 */
public class CategoryRarityModelScorerAlgorithm {
    private static final Logger logger = Logger.getLogger(CategoryRarityModelScorerAlgorithm.class);

    private static final double MIN_POSSIBLE_SCORE = 1;
    private static final double MAX_POSSIBLE_SCORE = 100;
    private static final double RARITY_SUM_EXPONENT = 1.8;

    private int maxRareCount;
    private int maxNumOfRareFeatures;
    private double xWithValueHalfFactor;


    public CategoryRarityModelScorerAlgorithm(Integer maxRareCount, Integer maxNumOfRareFeatures, double xWithValueHalfFactor) {
        assertMaxNumOfRareFeaturesValue(maxNumOfRareFeatures);
        assertMaxRareCountValue(maxRareCount);
        if(maxRareCount > 99) {
            logger.warn(String.format("maxRareCount is suspiciously big: %d", maxRareCount));
            throw new RuntimeException();
        }
        if(maxNumOfRareFeatures > 99) {
            logger.warn(String.format("maxNumOfRareFeatures is suspiciously big: %d", maxNumOfRareFeatures));
            throw new RuntimeException();
        }
        this.maxRareCount = maxRareCount;
        this.maxNumOfRareFeatures = maxNumOfRareFeatures;
        this.xWithValueHalfFactor = xWithValueHalfFactor;
    }

    public static void assertMaxRareCountValue(Integer maxRareCount) {
        Assert.notNull(maxRareCount, "maxRareCount must not be null");
        Assert.isTrue(maxRareCount >= 0, String.format("maxRareCount must be >= 0: %d", maxRareCount));
    }

    public static void assertMaxNumOfRareFeaturesValue(Integer maxNumOfRareFeatures) {
        Assert.notNull(maxNumOfRareFeatures, "maxNumOfRareFeatures must not be null");
        Assert.isTrue(maxNumOfRareFeatures >= 0, String.format("maxNumOfRareFeatures must be >= 0: %d", maxNumOfRareFeatures));
    }

    public double calculateScore(long featureCount, CategoryRarityModel model) {
        Assert.isTrue(featureCount > 0, featureCount < 0 ?
                "featureCount can't be negative - you probably have a bug" : "if you're scoring a first-time-seen feature, you should pass 1 as its count");
        Assert.isTrue(maxRareCount  <= model.getBuckets().size() / 2,
                String.format("maxRareCount must be no larger than %d: %d", model.getBuckets().size() / 2, maxRareCount));
        long totalEvents = model.getNumOfSamples();
        if (totalEvents == 0 || featureCount > maxRareCount) {
            return 0D;
        }
        double numRareEvents = 0;
        double numDistinctRareFeatures = 0;
        List<Double> buckets = model.getBuckets();
        for (int i = 0; i < featureCount; i++) {
            numRareEvents += (i + 1) * buckets.get(i);
            numDistinctRareFeatures += buckets.get(i);
        }
        for (int i = (int) featureCount; i < featureCount + maxRareCount; i++) {
            double commonnessDiscount = calcCommonnessDiscounting(i - featureCount + 2);
            numRareEvents += (i + 1) * buckets.get(i) * commonnessDiscount;
            numDistinctRareFeatures += buckets.get(i) * commonnessDiscount;
        }
        double commonEventProbability = 1 - numRareEvents / totalEvents;
        double numRareFeaturesDiscount = Math.pow(Math.max(0, (maxNumOfRareFeatures - numDistinctRareFeatures) / maxNumOfRareFeatures), RARITY_SUM_EXPONENT);
        double score = commonEventProbability * numRareFeaturesDiscount * calcCommonnessDiscounting(featureCount);
         return Math.floor(MAX_POSSIBLE_SCORE * score);
    }

    private double calcCommonnessDiscounting(double occurrence) {
        // make sure getMaxRareCount() will be scored less than MIN_POSSIBLE_SCORE - so once we multiply
        // by MAX_POSSIBLE_SCORE (inside calculateScore function) we get a rounded score of 0
        return Sigmoid.calcLogisticFunc(
                maxRareCount * xWithValueHalfFactor,
                maxRareCount,
                (MIN_POSSIBLE_SCORE / MAX_POSSIBLE_SCORE) * 0.99999999,
                occurrence - 1);
    }

    public int getMaxRareCount() {
        return maxRareCount;
    }

    public int getMaxNumOfRareFeatures() {
        return maxNumOfRareFeatures;
    }
}
