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

    private int maxRareCount;
    private int maxNumOfRarePartitions;
    private double xWithValueHalfFactor;

    public CategoryRarityModelScorerAlgorithm(Integer maxRareCount, Integer maxNumOfRarePartitions, double xWithValueHalfFactor) {
        assertMaxNumOfRarePartitionsValue(maxNumOfRarePartitions);
        assertMaxRareCountValue(maxRareCount);

        this.maxRareCount = maxRareCount;
        this.maxNumOfRarePartitions = maxNumOfRarePartitions;
        this.xWithValueHalfFactor = xWithValueHalfFactor;
    }

    public static void assertMaxRareCountValue(Integer maxRareCount) {
        Assert.notNull(maxRareCount, "maxRareCount must not be null");
        Assert.isTrue(maxRareCount >= 0, String.format("maxRareCount must be >= 0: %d", maxRareCount));
        if(maxRareCount > 99) {
            logger.warn(String.format("maxRareCount is suspiciously big: %d", maxRareCount));
            throw new RuntimeException();
        }
    }

    public static void assertMaxNumOfRarePartitionsValue(Integer maxNumOfRarePartitions) {
        Assert.notNull(maxNumOfRarePartitions, "maxNumOfRarePartitions must not be null");
        Assert.isTrue(maxNumOfRarePartitions >= 0, String.format("maxNumOfRarePartitions must be >= 0: %d", maxNumOfRarePartitions));
        if(maxNumOfRarePartitions > 99) {
            logger.warn(String.format("maxNumOfRarePartitions is suspiciously big: %d", maxNumOfRarePartitions));
            throw new RuntimeException();
        }
    }

    public double calculateScore(long featureCount, CategoryRarityModel model) {
        Assert.isTrue(featureCount > 0, featureCount < 0 ?
                "featureCount can't be negative - you probably have a bug" : "if you're scoring a first-time-seen feature, you should pass 1 as its count");
        Assert.isTrue(maxRareCount  <= model.getBuckets().size() / 2,
                String.format("maxRareCount must be no larger than %d: %d", model.getBuckets().size() / 2, maxRareCount));
        if (model.getNumOfPartitions() == 0 || featureCount > maxRareCount) {
            return 0D;
        }

        List<Double> buckets = model.getBuckets();
        double numOfDistinctPartitionsContainingRareFeatureValue = 1 + buckets.get((int) featureCount-1);
        for (int i = (int) featureCount; i < featureCount + maxRareCount; i++) {
            double commonnessDiscount = calcCommonnessDiscounting(maxRareCount, i - featureCount + 2);
            double diffBetweenCurToPrevBucket = buckets.get(i) - buckets.get(i-1);
            numOfDistinctPartitionsContainingRareFeatureValue += diffBetweenCurToPrevBucket * commonnessDiscount;
        }
        double commonEventProbability = 1 - numOfDistinctPartitionsContainingRareFeatureValue / (model.getNumOfPartitions() + 1);
        commonEventProbability = commonEventProbability < 0.7 ? 0.0 : (commonEventProbability - 0.7) / 0.3;
        double numOfDistinctPartitionsContainingRareFeatureValueDiscount = calcCommonnessDiscounting(maxNumOfRarePartitions, numOfDistinctPartitionsContainingRareFeatureValue);
        double featureCountDiscount = calcCommonnessDiscounting(maxRareCount, featureCount);
        double score = commonEventProbability * Math.min(featureCountDiscount,numOfDistinctPartitionsContainingRareFeatureValueDiscount);
        return Math.floor(MAX_POSSIBLE_SCORE * score);
    }

    private double calcCommonnessDiscounting(int range, double occurrence) {
        // make sure getMaxRareCount() will be scored less than MIN_POSSIBLE_SCORE - so once we multiply
        // by MAX_POSSIBLE_SCORE (inside calculateScore function) we get a rounded score of 0
        return Sigmoid.calcLogisticFunc(
                range * xWithValueHalfFactor,
                range,
                (MIN_POSSIBLE_SCORE / MAX_POSSIBLE_SCORE) * 0.99999999,
                occurrence - 1);
    }

    public int getMaxRareCount() {
        return maxRareCount;
    }

    public int getMaxNumOfRarePartitions() {
        return maxNumOfRarePartitions;
    }
}
