package fortscale.ml.scorer.algorithm;

import fortscale.common.datastructures.GenericHistogram;
import fortscale.ml.model.CategoryRarityModel;
import fortscale.ml.model.builder.CategoryRarityModelBuilder;
import fortscale.ml.model.builder.CategoryRarityModelBuilderConf;
import fortscale.ml.scorer.algorithms.CategoryRarityModelScorerAlgorithm;
import fortscale.utils.time.TimestampUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class CategoryRarityModelScorerAlgorithmTest extends AbstractScorerTest {

    private double calcScore(int maxRareCount,
                             int maxNumOfRareFeatures,
                             Map<String, Long> featureValueToCountMap,
                             long featureCountToScore) {
        GenericHistogram histogram = new GenericHistogram();
        featureValueToCountMap.entrySet().forEach(entry -> histogram.add(entry.getKey(), entry.getValue().doubleValue()));
        CategoryRarityModel model = (CategoryRarityModel)new CategoryRarityModelBuilder(new CategoryRarityModelBuilderConf(maxRareCount * 2)).build(histogram);
        CategoryRarityModelScorerAlgorithm scorerAlgorithm = new CategoryRarityModelScorerAlgorithm(maxRareCount, maxNumOfRareFeatures);
        return scorerAlgorithm.calculateScore(featureCountToScore, model);
    }

    private void assertScoreRange(int maxRareCount, int maxNumOfRareFeatures, Map<String, Long> featureValueToCountMap, int featureCount, double expectedRangeMin, double expectedRangeMax) {
        double score = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, featureCount);
        Assert.assertTrue(String.format("score (%e) >= expectedRangeMin (%e) does not hold", score, expectedRangeMin), score >= expectedRangeMin);
        Assert.assertTrue(String.format("score (%e) <= expectedRangeMax (%e) does not hold", score, expectedRangeMax), score <= expectedRangeMax);
    }

    private Map<String, Long> createFeatureValueToCountWithConstantCounts(long... numOfFeaturesAndCounts) {
        if (numOfFeaturesAndCounts.length % 2 == 1) {
            throw new IllegalArgumentException("should get an even number of parameters");
        }
        Map<String, Long> res = new HashMap<>();
        for (int i = 0; i < numOfFeaturesAndCounts.length; i += 2) {
            long numOfFeatures = numOfFeaturesAndCounts[i];
            long count = numOfFeaturesAndCounts[i + 1];
            if (count > 0) {
                while (numOfFeatures-- > 0) {
                    res.put("feature-" + i + "-" + numOfFeatures, count);
                }
            }
        }
        return res;
    }

    /*************************************************************************************
     *************************************************************************************
     *********** TEST BASIC SCORER BEHAVIOUR WHEN SCORER PARAMETERS ARE ISOLATED *********
     *************************************************************************************
     *************************************************************************************/

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWhenScoring0() {
        int maxRareCount = 5;
        int maxNumOfRareFeatures = 15;

        calcScore(maxRareCount, maxNumOfRareFeatures, createFeatureValueToCountWithConstantCounts(1, 1), 0);
    }

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
        CategoryRarityModel model = new CategoryRarityModel();
        Map<Long, Double> occurrencesToNumOfFeatures = new HashMap<>();
        occurrencesToNumOfFeatures.put(1L, 1D);
        int numOfBuckets = 10;
        model.init(occurrencesToNumOfFeatures, numOfBuckets);
        new CategoryRarityModelScorerAlgorithm(numOfBuckets / 2 + 1, 1).calculateScore(1, model);
    }

    @Test
    public void shouldScore0ToFeatureCountsGreaterThanMaxRareCount() {
        int maxNumOfRareFeatures = 10;
        for (int maxRareCount = 1; maxRareCount < 10; maxRareCount++) {
            for (int count = 1; count <= maxRareCount + 1; count++) {
                double rangeMin = (count == maxRareCount + 1) ? 0 : 1;
                double rangeMax = (count == maxRareCount + 1) ? 0 : 100;
                assertScoreRange(maxRareCount, maxNumOfRareFeatures, createFeatureValueToCountWithConstantCounts(1, 10000), count, rangeMin, rangeMax);
            }
        }
    }

    @Test
    public void shouldScore100ToVeryRareFeatureWhenNoOtherRareFeaturesNoMatterWhatIsMaxRareCount() {
        int maxNumOfRareFeatures = 10;
        int veryRareFeatureCount = 1;
        for (int maxRareCount = 1; maxRareCount < 10; maxRareCount++) {
            Assert.assertEquals(100, calcScore(maxRareCount, maxNumOfRareFeatures, createFeatureValueToCountWithConstantCounts(1, 10000), veryRareFeatureCount), 0.0001);
        }
    }

    @Test
    public void shouldScore100ToVeryRareFeatureEvenWhenThereAreCommonFeatures() {
        int maxRareCount = 35;
        int maxNumOfRareFeatures = 90;
        int veryRareFeatureCount = 1;
        Assert.assertEquals(100, calcScore(maxRareCount, maxNumOfRareFeatures, createFeatureValueToCountWithConstantCounts(10000, maxRareCount + 2), veryRareFeatureCount), 0.0001);
    }

    @Test
    public void shouldScore0WhenThereAreMoreThanMaxNumOfRareFeaturesRareFeatures() {
        int maxRareCount = 50;
        int count = 1;
        for (int maxNumOfRareFeatures = 1; maxNumOfRareFeatures < 10; maxNumOfRareFeatures++) {
            for (int numOfFeatures = 0; numOfFeatures <= maxNumOfRareFeatures; numOfFeatures++) {
                double rangeMin = (numOfFeatures == maxNumOfRareFeatures) ? 0 : 1;
                double rangeMax = (numOfFeatures == maxNumOfRareFeatures) ? 0 : 100;
                assertScoreRange(maxRareCount, maxNumOfRareFeatures, createFeatureValueToCountWithConstantCounts(numOfFeatures, count, 1, 10000), count, rangeMin, rangeMax);
            }
        }
    }

    @Test
    public void shouldScore100ToVeryRareFeatureWhenNoOtherRareFeaturesNoMatterWhatIsMaxNumOfRareFeatures() {
        int maxRareCount = 10;
        int veryRareFeatureCount = 1;
        for (int maxNumOfRareFeatures = 1; maxNumOfRareFeatures < 10; maxNumOfRareFeatures++) {
            Assert.assertEquals(100, calcScore(maxRareCount, maxNumOfRareFeatures, createFeatureValueToCountWithConstantCounts(1, 10000), veryRareFeatureCount), 0.0001);
        }
    }

    private double[][][] calcScoresOverConfigurationMatrix(int maxMaxRareCount, int maxMaxNumOfRareFeatures, int maxFeatureCountToScore) {
        return calcScoresOverConfigurationMatrix(createFeatureValueToCountWithConstantCounts(1, 10000), maxMaxRareCount, maxMaxNumOfRareFeatures, maxFeatureCountToScore);
    }

    private double[][][] calcScoresOverConfigurationMatrix(Map<String, Long> featureValueToCountMap, int maxMaxRareCount, int maxMaxNumOfRareFeatures, int maxFeatureCountToScore) {
        double[][][] scores = new double[maxMaxRareCount][][];
        for (int maxRareCount = 1; maxRareCount <= maxMaxRareCount; maxRareCount++) {
            scores[maxRareCount - 1] = new double[maxMaxNumOfRareFeatures][];
            for (int maxNumOfRareFeatures = 1; maxNumOfRareFeatures <= maxMaxNumOfRareFeatures; maxNumOfRareFeatures++) {
                scores[maxRareCount - 1][maxNumOfRareFeatures - 1] = new double[maxFeatureCountToScore];
                for (int featureCountToScore = 1; featureCountToScore <= maxFeatureCountToScore; featureCountToScore++) {
                    scores[maxRareCount - 1][maxNumOfRareFeatures - 1][featureCountToScore - 1] = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, featureCountToScore);
                }
            }
        }
        return scores;
    }

    private enum PARAMETER {
        MAX_RARE_COUNT,
        MAX_NUM_OF_RARE_FEATURES,
        FEATURE_COUNT
    }

    private void assertMonotonicity(List<List<Double>> scoresSeries, @Nullable Boolean shouldIncrease) {
        boolean hasStrongMonotonicity = false;
        for (List<Double> scores : scoresSeries) {
            if (scores.isEmpty()) {
                continue;
            }
            for (int i = 1; i < scores.size(); i++) {
                double scoresDelta = scores.get(i) - scores.get(i - 1);
                if (shouldIncrease == null) {
                    Assert.assertTrue(scoresDelta == 0);
                } else {
                    Assert.assertTrue(scoresDelta * (shouldIncrease ? 1 : -1) >= 0);
                }
            }
            hasStrongMonotonicity = hasStrongMonotonicity || Math.abs(scores.get(scores.size() - 1) - scores.get(0)) > 0.0001;
        }
        if (shouldIncrease != null) {
            // it's ok that some series are constant, but if all of them are - the model probably has a bug
            Assert.assertTrue(hasStrongMonotonicity);
        }
    }

    private void assertMonotonicity(@Nonnull double[][][] scores, PARAMETER overParameter, @Nullable Boolean shouldIncrease) {
        List<List<Double>> scoresSeries = new ArrayList<>();
        if (overParameter == PARAMETER.MAX_RARE_COUNT) {
            for (int maxNumOfRareFeaturesInd = 0; maxNumOfRareFeaturesInd < scores[0].length; maxNumOfRareFeaturesInd++) {
                for (int featureCountInd = 0; featureCountInd < scores[0][0].length; featureCountInd++) {
                    scoresSeries.add(new ArrayList<>());
                    for (int maxRareCountInd = 0; maxRareCountInd < scores.length; maxRareCountInd++) {
                        scoresSeries.get(scoresSeries.size() - 1).add(scores[maxRareCountInd][maxNumOfRareFeaturesInd][featureCountInd]);
                    }
                }
            }
        } else if (overParameter == PARAMETER.MAX_NUM_OF_RARE_FEATURES) {
            for (int maxRareCountInd = 0; maxRareCountInd < scores.length; maxRareCountInd++) {
                for (int featureCountInd = 0; featureCountInd < scores[0][0].length; featureCountInd++) {
                    scoresSeries.add(new ArrayList<>());
                    for (int maxNumOfRareFeaturesInd = 0; maxNumOfRareFeaturesInd < scores[0].length; maxNumOfRareFeaturesInd++) {
                        scoresSeries.get(scoresSeries.size() - 1).add(scores[maxRareCountInd][maxNumOfRareFeaturesInd][featureCountInd]);
                    }
                }
            }
        } else {
            for (int maxRareCountInd = 0; maxRareCountInd < scores.length; maxRareCountInd++) {
                for (int maxNumOfRareFeaturesInd = 0; maxNumOfRareFeaturesInd < scores[0].length; maxNumOfRareFeaturesInd++) {
                    scoresSeries.add(new ArrayList<>());
                    for (int featureCountInd = 0; featureCountInd < scores[0][0].length; featureCountInd++) {
                        scoresSeries.get(scoresSeries.size() - 1).add(scores[maxRareCountInd][maxNumOfRareFeaturesInd][featureCountInd]);
                    }
                }
            }
        }
        assertMonotonicity(scoresSeries, shouldIncrease);
    }

    @Test
    public void shouldScoreDecreasinglyWhenFeatureCountIncreases() {
        assertMonotonicity(calcScoresOverConfigurationMatrix(10, 90, 10), PARAMETER.FEATURE_COUNT, false);
    }

    @Test
    public void shouldScoreIncreasinglyWhenMaxNumOfRareFeaturesIncreases() {
        Map<String, Long> featureValueToCountMap = new HashMap<>();
        featureValueToCountMap.put("veryRareFeature", 1L);
        featureValueToCountMap.put("veryCommonFeature", 1000L);
        assertMonotonicity(calcScoresOverConfigurationMatrix(featureValueToCountMap, 10, 90, 10), PARAMETER.MAX_NUM_OF_RARE_FEATURES, true);
    }

    @Test
    public void shouldScoreConstantlyWhenMaxNumOfRareFeaturesIncreasesButModelDataIsEmpty() {
        assertMonotonicity(calcScoresOverConfigurationMatrix(10, 90, 10), PARAMETER.MAX_NUM_OF_RARE_FEATURES, null);
    }

    @Test
    public void shouldScoreIncreasinglyWhenProbabilityForRareFeatureEventsIncreases() {
        int maxRareCount = 10;
        int maxNumOfRareFeatures = 6;

        int veryRareFeatureCount = 1;
        List<Double> scores = new ArrayList<>();
        for (int commonFeatureCount = 10; commonFeatureCount < 100; commonFeatureCount += 10) {
            scores.add(calcScore(maxRareCount, maxNumOfRareFeatures, createFeatureValueToCountWithConstantCounts(1, veryRareFeatureCount, 1, commonFeatureCount), veryRareFeatureCount));
        }
        List<List<Double>> scoresSeries = new ArrayList<>();
        scoresSeries.add(scores);
        assertMonotonicity(scoresSeries, true);
    }




    /*************************************************************************************
     *************************************************************************************
     *********** GRAPHS SHOWING HOW SCORER BEHAVES WHEN PARAMETERS ARE ISOLATED **********
     ************* JUST PUT "PRINT_GRAPHS = true" IN AbstractScorerTest.java *************
     *************************************************************************************
     *************************************************************************************/

    private void printNewLineOrHeader(boolean printedHeader, String googleSheetName, int fromCount, int toCount) {
        int counts[] = new int[toCount - fromCount + 1];
        for (int i = 0; i < counts.length; i++) {
            counts[i] = fromCount + i;
        }
        printNewLineOrHeader(printedHeader, googleSheetName, counts);
    }

    private void printNewLineOrHeader(boolean printedHeader, String googleSheetName, int counts[]) {
        if (printedHeader) {
            println();
        } else {
            String featureCountsStr = "featureCount";
            for (int count : counts) {
                featureCountsStr += "\t" + count;
            }
            printGoogleSheetsExplaination(googleSheetName);
            println(featureCountsStr);
        }
    }

    @Test
    public void shouldScoreIncreasinglyWhenMaxRareCountIncreases() {
        int maxMaxRareCount = 10;
        int maxFeatureCountToScore = maxMaxRareCount + 1;
        double[][][] scores = calcScoresOverConfigurationMatrix(maxMaxRareCount, 90, maxFeatureCountToScore);

        assertMonotonicity(scores, PARAMETER.MAX_RARE_COUNT, true);

        int maxNumOfRareFeaturesToPrint = 10;
        boolean printedHeader = false;
        for (int maxRareCount = 0; maxRareCount < scores.length; maxRareCount++) {
            printNewLineOrHeader(printedHeader, "maxRareCountEffect", 1, scores[0][0].length);
            printedHeader = true;
            print(maxRareCount + "\t");
            for (int featureCount = 0; featureCount < scores[0][0].length; featureCount++) {
                print(scores[maxRareCount][maxNumOfRareFeaturesToPrint - 1][featureCount] + "\t");
            }
        }
    }

    @Test
    public void shouldScoreDecreasinglyWhenNumberOfRareFeaturesWithSameCountIncreases() {
        int maxRareCountToPrint = 15;
        int maxNumOfRareFeaturess[] = new int[]{5, 7, 9, 11, 13, 15};
        int counts[] = new int[]{1,4};

        boolean printedHeader = false;
        List<List<Double>> scoresSeries = new ArrayList<>();
        for (int maxRareCount = 1; maxRareCount < 20; maxRareCount++) {
            for (int count : counts) {
                for (int maxNumOfRareFeatures : maxNumOfRareFeaturess) {
                    int maxNumOfFeatures = maxRareCount + 1;
                    if (maxRareCount == maxRareCountToPrint) {
                        revertPrinting();
                        printNewLineOrHeader(printedHeader, "maxNumOfRareFeaturesEffect1", 0, maxNumOfFeatures - 1);
                        printedHeader = true;
                    } else {
                        turnOffPrinting();
                    }
                    print(count + "->" + maxNumOfRareFeatures + "\t");
                    List<Double> scores = new ArrayList<>(maxNumOfFeatures + 1);
                    for (int numOfFeatures = 0; numOfFeatures <= maxNumOfFeatures; numOfFeatures++) {
                        double score = calcScore(
                                maxRareCount,
                                maxNumOfRareFeatures,
                                createFeatureValueToCountWithConstantCounts(numOfFeatures, count, 10, 1000),
                                count);
                        scores.add(score);
                        print(score + "\t");
                    }
                    scoresSeries.add(scores);
                }
            }
        }
        assertMonotonicity(scoresSeries, false);
    }

    @Test
    public void shouldScoreDecreasinglyWhenThereAreManyFeaturesWithTheSameCountAndThenTheirCountIncreasesByOne() {
        int maxNumOfFeaturesToPrint = 2;
        int maxRareCountToPrint = 15;
        int maxNumOfRareFeaturessToPrint[] = new int[]{5, 10, 15, 20, 30, 40, 50};

        List<List<Double>> scoresSeries = new ArrayList<>();
        boolean printedHeader = false;
        for (int maxRareCount = 1; maxRareCount < 30; maxRareCount++) {
            for (int numOfFeatures = 0; numOfFeatures < 10; numOfFeatures++) {
                for (int maxNumOfRareFeatures = 5; maxNumOfRareFeatures < 100; maxNumOfRareFeatures += 5) {
                    if (numOfFeatures <= maxNumOfFeaturesToPrint && maxRareCount == maxRareCountToPrint && ArrayUtils.contains(maxNumOfRareFeaturessToPrint, maxNumOfRareFeatures)) {
                        revertPrinting();
                        printNewLineOrHeader(printedHeader, "maxNumOfRareFeaturesEffect2", 1, maxRareCount);
                        printedHeader = true;
                    } else {
                        turnOffPrinting();
                    }
                    print(numOfFeatures + "->" + maxNumOfRareFeatures + "\t");
                    List<Double> scores = new ArrayList<>(maxRareCount - 1);
                    for (int featureCount = 1; featureCount <= maxRareCount; featureCount++) {
                        double score = calcScore(maxRareCount, maxNumOfRareFeatures, createFeatureValueToCountWithConstantCounts(numOfFeatures, featureCount, 10, 1000), featureCount);
                        if (featureCount > 1) {
                            scores.add(score);
                        }
                        print(score + "\t");
                    }
                    scoresSeries.add(scores);
                }
            }
        }
        assertMonotonicity(scoresSeries, false);
    }

    @Test
    public void shouldScoreIncreasinglyWhenLessRareFeatureComparedToVeryRareFeatureBecomesEvenLessRare() {
        int maxNumOfRareFeatures = 1;
        int maxMaxRareCount = 10;
        int maxFeatureCount = maxMaxRareCount + 1;

        List<List<Double>> scoresSeries = new ArrayList<>(maxFeatureCount + 1);
        boolean printedHeader = false;
        for (int maxRareCount = 1; maxRareCount <= maxMaxRareCount; maxRareCount++) {
            printNewLineOrHeader(printedHeader, "lessRareFeatureEffect", 0, maxFeatureCount);
            printedHeader = true;
            print(maxRareCount + "\t");
            List<Double> scores = new ArrayList<>(maxFeatureCount + 1);
            for (int featureCount = 0; featureCount <= maxFeatureCount; featureCount++) {
                double score = calcScore(maxRareCount, maxNumOfRareFeatures, createFeatureValueToCountWithConstantCounts(1, featureCount, 1, 10000), 1);
                if (featureCount > 0) {
                    scores.add(score);
                }
                print(score + "\t");
            }
            scoresSeries.add(scores);
        }
        assertMonotonicity(scoresSeries, true);
    }



    /*************************************************************************************
     *************************************************************************************
     ****************** TEST VARIOUS SCENARIOS - FROM BASIC TO ADVANCED ******************
     ***** (BUT NOT AS BASIC AS THE TESTS WHICH TRY TO ISOLATE THE SCORER PARAMETERS *****
     *************************************************************************************
     *************************************************************************************/

    @Test
    public void shouldScoreFirstSeenVeryRareFeatureTheSameWhenBuildingWithVeryCommonFeaturesAndWithoutThem() {
        int maxRareCount = 10;
        int maxNumOfRareFeatures = 6;
        int veryRareFeatureCount = 1;
        int veryCommonFeatureCount = 10000;
        double scoreWithManyCommons = calcScore(maxRareCount, maxNumOfRareFeatures, createFeatureValueToCountWithConstantCounts(maxNumOfRareFeatures - 1, veryCommonFeatureCount), veryRareFeatureCount);
        double scoreWithOneCommon = calcScore(maxRareCount, maxNumOfRareFeatures, createFeatureValueToCountWithConstantCounts(1, veryCommonFeatureCount), veryRareFeatureCount);
        Assert.assertEquals(scoreWithOneCommon, scoreWithManyCommons, 1);
        Assert.assertTrue(scoreWithOneCommon >= 99);
    }

    @Test
    public void shouldScoreSecondSeenVeryRareFeatureIncreasinglyWhenCommonFeatureCountIncreases() {
        int maxRareCount = 10;
        int maxNumOfRareFeatures = 6;

        int veryRareFeatureCount = 1;
        List<Double> scores = new ArrayList<>();
        for (int commonFeatureCount = 20; commonFeatureCount < 1000; commonFeatureCount += 10) {
            scores.add(calcScore(maxRareCount, maxNumOfRareFeatures, createFeatureValueToCountWithConstantCounts(1, veryRareFeatureCount, 1, commonFeatureCount), veryRareFeatureCount));
        }
        List<List<Double>> scoresSeries = new ArrayList<>(1);
        scoresSeries.add(scores);
        assertMonotonicity(scoresSeries, true);
    }

    @Test
    public void elementaryCheck() {
        int maxRareCount = 15;
        int maxNumOfRareFeatures = 5;

        long count = 100;
        Map<String, Long> featureValueToCountMap = new HashMap<>();
        for (int i = 0; i < 100; i++) {
            featureValueToCountMap.put(String.format("test%d", i), count);
        }
        double score = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, count);
        Assert.assertEquals(0.0, score, 0.0);
    }

    @Test
    public void simpleInputOutput() {
        for (int modelConfig = 0; modelConfig < 2; modelConfig++) {
            int maxRareCount = modelConfig == 0 ? 10 : 6;
            int maxNumOfRareFeatures = modelConfig == 0 ? 6 : 5;

            Map<String, Long> featureValueToCountMap = new HashMap<>();
            for (int i = 0; i < 2; i++) {
                featureValueToCountMap.put(String.format("test%d", i), 100L);
            }

            int[] counts = modelConfig == 0 ? new int[]{1, 3, 4, 6} : new int[]{1, 2, 3, 4};
            double[] scores = modelConfig == 0 ? new double[]{100, 89, 60, 15} : new double[]{100, 94, 50, 15};
            for (int i = 0; i < scores.length; i++) {
                double score = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, counts[i]);
                Assert.assertEquals(scores[i], score, 0);
            }
        }
    }

    @Test
    public void testingScoreOfVeryRareFeatureValueAgainstVeryLargeFeatureValuesWithValuesIncreasingByTime() {
        int maxRareCount = 10;
        int maxNumOfRareFeatures = 6;

        Map<String, Long> featureValueToCountMap = new HashMap<>();
        String rareFeature = "rareFeature";
        long[] rareCounts = new long[]{1, 2, 3, 4, 8, 9};
        long[] commonCounts = new long[]{50, 100, 150, 200, 400, 450};
        double[] scores = new double[]{95, 95, 85, 58, 4, 2};
        for (int i = 0; i < scores.length; i++) {
            for (int j = 0; j < maxNumOfRareFeatures - 1; j++) {
                featureValueToCountMap.put("commonFeature-" + j, commonCounts[i]);
            }
            featureValueToCountMap.put(rareFeature, rareCounts[i]);
            double score = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, rareCounts[i]);
            Assert.assertEquals(scores[i], score, 0);
        }
    }

    @Test
    public void testingScoreOfVeryRareFeatureValuesAgainstVeryLargeFeatureValues() {
        for (int modelConfig = 0; modelConfig < 2; modelConfig++) {
            int maxRareCount = modelConfig == 0 ? 10 : 15;
            int maxNumOfRareFeatures = modelConfig == 0 ? 6 : 8;

            Map<String, Long> featureValueToCountMap = createFeatureValueToCountWithConstantCounts(maxNumOfRareFeatures - 1, 2000);
            long rareFeatureCountA = 1;
            featureValueToCountMap.put("rareFeatureA", rareFeatureCountA);
            double score = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, rareFeatureCountA);
            Assert.assertEquals(97, score, 1);

            int rareFeatureCountB = 2;
            featureValueToCountMap.put("rareFeatureB", 2L);
            double scoreA = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, rareFeatureCountA);
            double scoreB = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, rareFeatureCountB);
            Assert.assertEquals(scoreA, scoreB, 1);
            Assert.assertEquals(modelConfig == 0 ? 86 : 91, scoreA, 1);

            long[] counts = new long[]{2, 1, 1, 1};
            double[] scores = modelConfig == 0 ? new double[]{70, 51, 28, 0} : new double[]{82, 71, 57, 40};
            for (int i = 0; i < scores.length; i++) {
                featureValueToCountMap.put(String.format("rareFeature-%d", i), counts[i]);
                score = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, counts[i]);
                Assert.assertEquals(scores[i], score, 1);
            }
        }
    }

    @Test
    public void testingScoreOfOneVeryRareFeatureValueAndManyRareFeatureValuesAgainstVeryLargeFeatureValues() {
        for (int modelConfig = 0; modelConfig < 2; modelConfig++) {
            int maxRareCount = modelConfig == 0 ? 10 : 15;
            int maxNumOfRareFeatures = modelConfig == 0 ? 6 : 8;

            Map<String, Long> featureValueToCountMap = createFeatureValueToCountWithConstantCounts(maxNumOfRareFeatures - 1, 2000);
            long veryRareFeatureCount = 1;
            featureValueToCountMap.put("veryRareFeatureValue", veryRareFeatureCount);
            double score = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, 1);
            Assert.assertEquals(96, score, 1);

            long[] rareFeatureCounts = modelConfig == 0 ? new long[]{3, 4, 2, 3, 4} : new long[]{5, 6, 4, 5, 6, 4};
            double[] rareFeaturesScores = modelConfig == 0 ? new double[]{77, 44, 53, 25, 0} : new double[]{66, 41, 63, 40, 20, 19};
            double[] veryRareFeaturesScores = modelConfig == 0 ? new double[]{87, 79, 62, 42, 27} : new double[]{92, 90, 81, 73, 66, 53};
            for (int i = 0; i < rareFeatureCounts.length; i++) {
                featureValueToCountMap.put("rareFeatureValue-" + i, rareFeatureCounts[i]);
                score = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, veryRareFeatureCount);
                Assert.assertEquals(veryRareFeaturesScores[i], score, 1);
                score = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, rareFeatureCounts[i]);
                Assert.assertEquals(rareFeaturesScores[i], score, 1);
            }
        }
    }

    @Test
    public void testingScoreOfRareFeatureValuesAgainstMediumFeatureValues() {
        int maxRareCount = 10;
        int maxNumOfRareFeatures = 6;

        Map<String, Long> featureValueToCountMap = createFeatureValueToCountWithConstantCounts(maxNumOfRareFeatures - 1, 15);

        long rareFeatureCountA = 2;
        featureValueToCountMap.put("rareFeatureValue-A", rareFeatureCountA);
        double score = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, rareFeatureCountA);
        Assert.assertEquals(92, score, 1);

        long rareFeatureCountB = 3;
        featureValueToCountMap.put("rareFeatureValue-B", rareFeatureCountB);
        score = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, rareFeatureCountA);
        Assert.assertEquals(80, score, 1);

        double[] scores = new double[]{57, 40};
        for (int i = 0; i < scores.length; i++) {
            long rareFeatureCount3 = 3;
            featureValueToCountMap.put("newRareFeatureValue-" + i, rareFeatureCount3);
            score = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, rareFeatureCount3);
            Assert.assertEquals(scores[i], score, 1);
        }
    }

    @Test
    public void testingScoreOfOnlyRareFeatureValues() {
        int maxRareCount = 10;
        int maxNumOfRareFeatures = 6;

        Map<String, Long> featureValueToCountMap = new HashMap<>();
        long rareFeatureCount = 1;
        featureValueToCountMap.put("rareFeatureValue", rareFeatureCount);
        for (int i = 0; i < 4; i++) {
            featureValueToCountMap.put("newRareFeatureValue-" + i, 2L);
        }

        double score = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, rareFeatureCount);
        Assert.assertEquals(0, score, 1);
    }

    @Test
    public void testingScoreOfRareFeatureValueAgainstMediumFeatureValuesAcrossTime() {
        int maxRareCount = 10;
        int maxNumOfRareFeatures = 6;

        Map<String, Long> featureValueToCountMap = new HashMap<>();

        long[] rareFeatureCounts = new long[]{2, 4};
        long[] mediumFeatureCounts = new long[]{8, 10};
        double[] rareFeatureScores = new double[]{79, 47};
        for (int i = 0; i < rareFeatureScores.length; i++) {
            for (int j =  0; j < 10; j++) {
                featureValueToCountMap.put("mediumFeatureValue-" + j, mediumFeatureCounts[i]);
            }
            featureValueToCountMap.put("rareFeatureValue", rareFeatureCounts[i]);
            double score = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, rareFeatureCounts[i]);
            Assert.assertEquals(rareFeatureScores[i], score, 1);
        }
    }

    @Test
    public void testRareToMediumFeatureValueAgainstMediumLargeFeatureValues() {
        int maxRareCount = 10;
        int maxNumOfRareFeatures = 6;

        int mediumLargeFeatureCount = 13;
        Map<String, Long> featureValueToCountMap = createFeatureValueToCountWithConstantCounts(maxNumOfRareFeatures - 1, mediumLargeFeatureCount);
        double score = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, mediumLargeFeatureCount);
        Assert.assertEquals(0, score, 0);

        long[] rareFeatureCounts = new long[]{1, 2, 3, 4, 5, 8};
        double[] rareFeatureScores = new double[]{95, 93, 80, 53, 28, 2};
        for (int i = 0; i < rareFeatureScores.length; i++) {
            featureValueToCountMap.put("rareFeature", rareFeatureCounts[i]);
            score = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, rareFeatureCounts[i]);
            Assert.assertEquals(rareFeatureScores[i], score, 1);
        }
    }

    @Test
    public void testRareToMediumFeatureValueAgainstRareFeatureValueAndMediumFeatureValue() {
        int maxRareCount = 10;
        int maxNumOfRareFeatures = 6;

        double[] scores = new double[]{85, 78, 67, 46, 23, 11, 4, 2, 1, 0};
        for (int rareFeatureCount = 1; rareFeatureCount < 11; rareFeatureCount++) {
            double score = calcScore(maxRareCount, maxNumOfRareFeatures, createFeatureValueToCountWithConstantCounts(1, 4, 1, 15), rareFeatureCount);
            Assert.assertEquals(scores[rareFeatureCount - 1], score, 1);
        }
    }



    /*************************************************************************************
     *************************************************************************************
     ***************************** TEST REAL DATA SCENARIOS ******************************
     ***************** THESE TESTS ARE MORE OF RESEARCH SCRIPTS THAN TESTS ***************
     ********* THEY ARE MEANT FOR RUNNING REAL DATA SCENARIOS AND THEN INSPECTING ********
     ************ THE RESULTS BY HANDS (ALTHOUGH ASSERTS COULD BE USED AS WELL) **********
     ************** READ AbstractModelTest.java'S DOCUMENTATION FOR MORE INFO ************
     *************************************************************************************
     *************************************************************************************/

    private static Map<String, String> featureValueToColor = new HashMap<>();

    /**
     * Get a color string (used within System.out.print) for the given feature value.
     * It's promised that if called with the same feature value, the same color will be returned.
     */
    private String getFeatureColor(String featureValue) {
        String FEATURE_COLORS[] = new String[]{"\033[34m", "\033[35m", "\033[32m", "\033[33m", "\033[36m", "\033[31m"};

        if (featureValueToColor.get(featureValue) == null) {
            featureValueToColor.put(featureValue, FEATURE_COLORS[featureValueToColor.size() % FEATURE_COLORS.length]);
        }
        return featureValueToColor.get(featureValue);
    }

    private final static String COLOR_NORMAL = "\033[0m";

    /**
     * Print info about the context available when the given feature value was scored, e.g.:
     * #0  hostname_36643275                       : events count 4356    (unique days 39)	0000000000111111111122222222223333
     * #1  hostname_16101171                       : events count 226     (unique days 12)	0000000000111111111122
     * #2  service_name_177266206                  : events count 397     (unique days 11) 	00000000001111111111222
     *
     * 		scoring hostname_74957113 which has 1 events. In total there are 4980 events spread across 3 features.
     * 		score: 100
     * @param eventTime time in which the event has occurred.
     * @param featureValue the feature value who's been scored.
     * @param featureValueToCountMap context info - what feature values were encountered in the past, and how often.
     * @param featureValueToDaysMap context info - how many days each feature value has been encountered in the past.
     */
    private void printEvent(long eventTime, String featureValue, Double score, Map<String, Long> featureValueToCountMap, Map<String, Set<Date>> featureValueToDaysMap) {
        List<String> featureValues = new ArrayList<>(featureValueToCountMap.keySet());
        printFeatureValuesHistogram(featureValues, featureValueToCountMap, featureValueToDaysMap);
        int featureValueIndex = featureValues.indexOf(featureValue);
        int totalNumOfEvents = 1;
        for (long count : featureValueToCountMap.values()) {
            totalNumOfEvents += count;
        }
        println(String.format("\n\t%s: scoring %s%s%s%s which has %d events. In total there are %d events spread across %d features.",
                getFormattedDate(eventTime),
                getFeatureColor(featureValue),
                featureValue,
                COLOR_NORMAL,
                featureValueIndex == -1 ? "" : " (#" + featureValueIndex + ")",
                featureValueIndex == -1 ? 1 : featureValueToCountMap.get(featureValue) + 1,
                totalNumOfEvents,
                featureValueToCountMap.size()));
        println(String.format("\tscore: %d", score.intValue()));
        println("\n");
    }

    /**
     * Print the histogram of the distribution over feature values, e.g.:
     * #0  hostname_36643275                       : events count 4356    (unique days 39)	0000000000111111111122222222223333
     * #1  hostname_16101171                       : events count 226     (unique days 12)	0000000000111111111122
     * #2  service_name_177266206                  : events count 397     (unique days 11) 	00000000001111111111222
     * @param featureValues the available feature values in the distribution.
     *                      The histogram's bars will be ordered according to featureValues.
     * @param featureValueToCountMap the distribution of feature values.
     * @param featureValueToDaysMap how many days each feature value has been encountered in the past
     */
    private void printFeatureValuesHistogram(List<String> featureValues, Map<String, Long> featureValueToCountMap, Map<String, Set<Date>> featureValueToDaysMap) {
        String BAR_COLORS[] = new String[]{"\033[36m", "\033[32m", "\033[33m", "\033[31m"};

        for (int featureValueInd = 0; featureValueInd < featureValues.size(); featureValueInd++) {
            String featureValue = featureValues.get(featureValueInd);
            long count = featureValueToCountMap.get(featureValue);
            String bar = "";
            int base = 0;
            long barLength = count;
            while (barLength > 0) {
                String color = BAR_COLORS[Math.min(base, BAR_COLORS.length - 1)];
                bar += color;
                for (int i = 0; i < 10 && barLength > 0; i++) {
                    bar += base;
                    barLength -= Math.pow(10, base);
                }
                base++;
            }
            bar += COLOR_NORMAL;
            String featureColor = getFeatureColor(featureValue);
            println(String.format("#%-3d%s%s%s: events count %-7d (unique days %-2d)\t%s",
                    featureValueInd,
                    featureColor,
                    StringUtils.rightPad(StringUtils.isBlank(featureValue) ? "(empty string)" : featureValue, 40),
                    COLOR_NORMAL,
                    count,
                    featureValueToDaysMap.get(featureValue).size(),
                    bar));
        }
    }

    private class CategoryRarityScenarioCallbacks extends ScenarioCallbacks {
        private int maxRareCount;
        private int maxNumOfRareFeatures;
        private Map<String, Long> featureValueToCountMap;
        private Map<String, Set<Date>> featureValueToDaysMap;

        @Override
        public void onScenarioRunStart() {
            maxRareCount = 10;
            maxNumOfRareFeatures = 6;
            featureValueToCountMap = new HashMap<>();
            featureValueToDaysMap = new HashMap<>();
        }

        @Override
        public Double onScore(TestEventsBatch eventsBatch) {
            long eventFeatureCount = featureValueToCountMap.getOrDefault(eventsBatch.getFeature(), 0L);
            return calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, eventFeatureCount + 1);
        }

        @Override
        public void onPrintEvent(ScoredEvent scoredEvent) {
            printEvent(scoredEvent.event.time_bucket, scoredEvent.event.getFeature(), scoredEvent.score, featureValueToCountMap, featureValueToDaysMap);
        }

        @Override
        public void onFinishProcessEvent(TestEventsBatch eventsBatch) {
            long eventFeatureCount = featureValueToCountMap.getOrDefault(eventsBatch.getFeature(), 0L);
            featureValueToCountMap.put(eventsBatch.getFeature(), eventFeatureCount + 1);
            Set<Date> eventFeatureDays = featureValueToDaysMap.getOrDefault(eventsBatch.getFeature(), new HashSet<>());
            eventFeatureDays.add(new Date(TimestampUtils.convertToMilliSeconds(eventsBatch.time_bucket / (60 * 60 * 24) * 60 * 60 * 24)));
            featureValueToDaysMap.put(eventsBatch.getFeature(), eventFeatureDays);
        }
    }

    @Test
    public void testRealScenarioSshSrcMachineUsername_278997272() throws IOException {
        try {
            runAndPrintRealScenario(new CategoryRarityScenarioCallbacks(), "username_278997272.csv", 0);
        } catch (FileNotFoundException e) {
            println("file not found");
        }
    }

    @Test
    public void testRealScenariosHowManyAnomalousUsers() throws IOException {
        boolean RUN_FAST = true;
        if (RUN_FAST) {
            testRealScenariosHowManyAnomalousUsers(new CategoryRarityScenarioCallbacks(), 0.136, 50, 900);
        }
        else {
            testRealScenariosHowManyAnomalousUsers(new CategoryRarityScenarioCallbacks(), 0.138, 50);
        }
    }
}
