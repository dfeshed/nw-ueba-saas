package fortscale.ml.scorer.algorithms;

import fortscale.ml.model.CategoryRarityModel;
import fortscale.utils.logging.Logger;
import org.springframework.util.Assert;

/**
 * For documentation and explanation of how this scoring algorithm works - refer to https://fortscale.atlassian.net/wiki/display/FSC/category+rarity+model
 */
public class CategoryRarityModelScorerAlgorithm {
    private static final Logger logger = Logger.getLogger(CategoryRarityModelScorerAlgorithm.class);

    private static final double MIN_POSSIBLE_SCORE = 1;
    private static final int MAX_POSSIBLE_SCORE = 100;
    private static final double RARITY_SUM_EXPONENT = 1.8;
    private static final int LOGISTIC_FUNCTION_DOMAIN = 3;
    // STEEPNESS makes sure that at the end of the domain (LOGISTIC_FUNCTION_DOMAIN)
    // the function gets 0.99999 * MIN_POSSIBLE_SCORE - so once we multiply by
    // MAX_POSSIBLE_SCORE (inside score function) we get a rounded score of 0
    private static final double STEEPNESS = Math.log(1 / (0.99999 * MIN_POSSIBLE_SCORE / MAX_POSSIBLE_SCORE) - 1) / Math.log(LOGISTIC_FUNCTION_DOMAIN);

    private int maxRareCount;
    private int maxNumOfRareFeatures;


    public CategoryRarityModelScorerAlgorithm(Integer maxRareCount, Integer maxNumOfRareFeatures) throws IllegalArgumentException{
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

    public double calculateScore(int featureCount, CategoryRarityModel model) {
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
        for (int i = featureCount; i < featureCount + maxRareCount; i++) {
            double commonnessDiscount = calcCommonnessDiscounting(i - featureCount + 2, maxRareCount );
            numRareEvents += (i + 1) * buckets[i] * commonnessDiscount;
            numDistinctRareFeatures += buckets[i] * commonnessDiscount;
        }
        double commonEventProbability = 1 - numRareEvents / totalEvents;
        double numRareFeaturesDiscount = 1 - Math.min(1, Math.pow(numDistinctRareFeatures / maxNumOfRareFeatures, RARITY_SUM_EXPONENT));
        double score = commonEventProbability * numRareFeaturesDiscount * calcCommonnessDiscounting(featureCount, maxRareCount);
        return Math.floor(MAX_POSSIBLE_SCORE * score);
    }

    private double calcCommonnessDiscounting(double occurrence, int maxRareCount) {
        return applyLogisticFunc(occurrence - 1, maxRareCount);
    }

    /**
     * Apply a logistic function on the given input.
     * A logistic function behaves approximately like this:
     *    |
     *   1|......
     *    |       .....
     *    |             ...
     *    |                 ..
     *    |                    .
     *    |                     .
     *    |                      .
     *    |                       .
     * 0.5|                       .
     *    |                        .
     *    |                         .
     *    |                          .
     *    |                            ...
     *    |                                .....
     *    |                                      ........
     *   _|______________________________________________
     *    |                                          (maxXValue)
     *
     * For more info, look into
     * 		https://www.google.co.il/search?q=1%2F(1%2B(x%2B1.5)%5E4.18)&oq=1%2F(1%2B(x%2B1.5)%5E4.18)&aqs=chrome..69i57j69i59l2.239j0j7&sourceid=chrome&es_sm=0&ie=UTF-8#q=1%2F(1%2Bx%5E4.182667533025268)
     *
     * @param x the function input.
     * @param maxXValue values above maxXValue will get approximately 0 as output (as shown in the fine ascii art).
     */
    private double applyLogisticFunc(double x, double maxXValue) {
        return 1 / (1 + Math.pow(x * LOGISTIC_FUNCTION_DOMAIN / maxXValue, STEEPNESS));
    }


}
