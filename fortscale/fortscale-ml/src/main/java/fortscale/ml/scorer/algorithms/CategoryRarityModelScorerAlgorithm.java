package fortscale.ml.scorer.algorithms;

import fortscale.ml.model.CategoryRarityModel;
import fortscale.ml.model.prevalance.field.Sigmoid;
import fortscale.utils.logging.Logger;
import org.springframework.util.Assert;

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


    public CategoryRarityModelScorerAlgorithm(Integer maxRareCount, Integer maxNumOfRareFeatures) {
        assertMaxNumOfRareFeaturesValue(maxNumOfRareFeatures);
        assertMaxRareCountValue(maxRareCount);
        if(maxRareCount > 99) {
            logger.warn(String.format("maxRareCount is suspeciously big: %d", maxRareCount));
        }
        if(maxNumOfRareFeatures > 99) {
            logger.warn(String.format("maxNumOfRareFeatures is suspeciously big: %d", maxNumOfRareFeatures));
        }
        this.maxRareCount = maxRareCount;
        this.maxNumOfRareFeatures = maxNumOfRareFeatures;
    }

    public static void assertMaxRareCountValue(Integer maxRareCount) {
        Assert.notNull(maxRareCount, "maxRareCount must not be null");
        Assert.isTrue(maxRareCount >= 0, String.format("maxRareCount must be >= 0: %d", maxRareCount));
        Assert.isTrue(maxRareCount  <= CategoryRarityModel.NUM_OF_BUCKETS / 2, String.format("maxRareCount must be no larger then %d: %d", CategoryRarityModel.NUM_OF_BUCKETS / 2, maxRareCount));
    }

    public static void assertMaxNumOfRareFeaturesValue(Integer maxNumOfRareFeatures) {
        Assert.notNull(maxNumOfRareFeatures, "maxNumOfRareFeatures must not be null");
        Assert.isTrue(maxNumOfRareFeatures >= 0, String.format("maxNumOfRareFeatures must be >= 0: %d", maxNumOfRareFeatures));
    }

    public double calculateScore(long featureCount, CategoryRarityModel model) {
        Assert.isTrue(featureCount > 0, featureCount < 0 ?
                "featureCount can't be negative - you probably have a bug" : "if you're scoring a first-time-seen feature, you should pass 1 as its count");
        if(model==null) {
            return 0D;
        }
        long totalEvents = model.getNumOfSamples();
        if (totalEvents == 0 || featureCount > maxRareCount) {
            return 0D;
        }
        double numRareEvents = 0;
        double numDistinctRareFeatures = 0;
        double[] buckets = model.getBuckets();
        for (int i = 0; i < featureCount; i++) {
            numRareEvents += (i + 1) * buckets[i];
            numDistinctRareFeatures += buckets[i];
        }
        for (int i = (int) featureCount; i < featureCount + maxRareCount; i++) {
            double commonnessDiscount = calcCommonnessDiscounting(i - featureCount + 2);
            numRareEvents += (i + 1) * buckets[i] * commonnessDiscount;
            numDistinctRareFeatures += buckets[i] * commonnessDiscount;
        }
        double commonEventProbability = 1 - numRareEvents / totalEvents;
        double numRareFeaturesDiscount = 1 - Math.min(1, Math.pow(numDistinctRareFeatures / maxNumOfRareFeatures, RARITY_SUM_EXPONENT));
        double score = commonEventProbability * numRareFeaturesDiscount * calcCommonnessDiscounting(featureCount);
        return Math.floor(MAX_POSSIBLE_SCORE * score);
    }

    private double calcCommonnessDiscounting(double occurrence) {
        // make sure getMaxRareCount() will be scored less than MIN_POSSIBLE_SCORE - so once we multiply
        // by MAX_POSSIBLE_SCORE (inside calculateScore function) we get a rounded score of 0
        return Sigmoid.calcLogisticFunc(
                maxRareCount * 0.3333333333333333,
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
