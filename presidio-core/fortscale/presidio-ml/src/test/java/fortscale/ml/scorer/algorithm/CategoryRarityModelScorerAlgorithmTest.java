package fortscale.ml.scorer.algorithm;

import fortscale.common.feature.CategoricalFeatureValue;
import fortscale.common.util.GenericHistogram;
import fortscale.ml.model.CategoryRarityModel;
import fortscale.ml.model.builder.CategoryRarityModelBuilder;
import fortscale.ml.model.builder.CategoryRarityModelBuilderConf;
import fortscale.ml.model.metrics.CategoryRarityModelBuilderMetricsContainer;
import fortscale.ml.scorer.algorithms.CategoryRarityModelScorerAlgorithm;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.time.TimestampUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.mockito.Mockito.mock;

public class CategoryRarityModelScorerAlgorithmTest extends AbstractScorerTest {

    private CategoryRarityModelBuilderMetricsContainer categoryRarityMetricsContainer = mock(CategoryRarityModelBuilderMetricsContainer.class);
    private static final double X_WITH_VALUE_HALF_FACTOR = 0.3333333333333333;
    private static final double MIN_PROBABILITY = 0.7;

    private void updateCategoricalFeatureValue
            (CategoricalFeatureValue categoricalFeatureValue,
             String featureValue, Long count,
             Instant startTime){
        updateCategoricalFeatureValue(categoricalFeatureValue, featureValue, count, startTime, 90);
    }

    private void updateCategoricalFeatureValue
            (CategoricalFeatureValue categoricalFeatureValue,
             String featureValue, Long count,
             Instant startTime, int modelDuration){
        Map<String, Long> featureValueToCountMap = new HashMap<>();
        featureValueToCountMap.put(featureValue, count);
        updateCategoricalFeatureValue(categoricalFeatureValue, featureValueToCountMap, startTime, modelDuration, false);
    }

    private void updateCategoricalFeatureValue
            (CategoricalFeatureValue categoricalFeatureValue,
             Map<String, Long> featureValueToCountMap,
             Instant startTime,
             boolean isRestartTimeForEachFeature){
        updateCategoricalFeatureValue(categoricalFeatureValue, featureValueToCountMap, startTime, 90, isRestartTimeForEachFeature);
    }

    private void updateCategoricalFeatureValue
            (CategoricalFeatureValue categoricalFeatureValue,
             Map<String, Long> featureValueToCountMap,
             Instant startTime,
             int modelDuration,
             boolean isRestartTimeForEachFeature){
        Instant curTime = startTime;
        int numOfDays = 0;
        for (Map.Entry<String, Long> entry : featureValueToCountMap.entrySet()) {
            if(isRestartTimeForEachFeature){
                curTime = startTime;
                numOfDays = 0;
            }
            Long numOfOccurences = entry.getValue();
            while (numOfOccurences >0)
            {
                if(numOfDays == modelDuration){
                    curTime = startTime;
                    numOfDays = 0;
                }
                GenericHistogram histogram = new GenericHistogram();
                histogram.add(entry.getKey(),1D);
                categoricalFeatureValue.add(histogram,curTime);
                curTime = curTime.plus(1,ChronoUnit.DAYS);
                numOfOccurences--;
                numOfDays++;
            }

        }
    }

    private double calcScore(int maxRareCount,
                             int maxNumOfRarePartitions,
                             Map<String, Long> featureValueToCountMap,
                             long featureCountToScore) {
        return calcScore(maxRareCount, maxNumOfRarePartitions, featureValueToCountMap, featureCountToScore, true);
    }

    private double calcScore(int maxRareCount,
                             int maxNumOfRarePartitions,
                             Map<String, Long> featureValueToCountMap,
                             long featureCountToScore,  boolean isRestartTimeForEachFeature) {
        Instant startTime = Instant.parse("2007-12-03T10:00:00.00Z");
        CategoricalFeatureValue categoricalFeatureValue = new CategoricalFeatureValue(FixedDurationStrategy.HOURLY);
        updateCategoricalFeatureValue(categoricalFeatureValue, featureValueToCountMap, startTime, isRestartTimeForEachFeature);

        return calcScore(maxRareCount, maxNumOfRarePartitions, featureCountToScore,categoricalFeatureValue);
    }

    private double calcScore(int maxRareCount,
                             int maxNumOfRarePartitions,
                             long featureCountToScore,
                             CategoricalFeatureValue categoricalFeatureValue) {

        CategoryRarityModelBuilderConf config = new CategoryRarityModelBuilderConf(maxRareCount + maxNumOfRarePartitions);
        config.setPartitionsResolutionInSeconds(86400);
        CategoryRarityModel model = (CategoryRarityModel)new CategoryRarityModelBuilder(config, categoryRarityMetricsContainer).build(categoricalFeatureValue);
        CategoryRarityModelScorerAlgorithm scorerAlgorithm =
                new CategoryRarityModelScorerAlgorithm(maxRareCount, maxNumOfRarePartitions,
                        X_WITH_VALUE_HALF_FACTOR, MIN_PROBABILITY);
        return scorerAlgorithm.calculateScore(featureCountToScore, model);
    }

    private void assertScoreRange(int maxRareCount, int maxNumOfRarePartitions, Map<String, Long> featureValueToCountMap, int featureCount, double expectedRangeMin, double expectedRangeMax) {
        assertScoreRange(maxRareCount, maxNumOfRarePartitions, featureValueToCountMap, featureCount, expectedRangeMin, expectedRangeMax, true);
    }

    private void assertScoreRange(int maxRareCount, int maxNumOfRarePartitions, Map<String, Long> featureValueToCountMap, int featureCount, double expectedRangeMin, double expectedRangeMax, boolean isRestartTimeForEachFeature) {
        double score = calcScore(maxRareCount, maxNumOfRarePartitions, featureValueToCountMap, featureCount, isRestartTimeForEachFeature);
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
        int maxNumOfRarePartitions = 15;

        calcScore(maxRareCount, maxNumOfRarePartitions, createFeatureValueToCountWithConstantCounts(1, 1), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenNegativeAsMaxRareCount() {
        new CategoryRarityModelScorerAlgorithm(-1, 1, X_WITH_VALUE_HALF_FACTOR, MIN_PROBABILITY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenNegativeAsmaxNumOfRarePartitions() {
        new CategoryRarityModelScorerAlgorithm(1, -1, X_WITH_VALUE_HALF_FACTOR, MIN_PROBABILITY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenTooLargeMaxRareCountPlusMaxNumOfPartitionsValue() {
        CategoryRarityModel model = new CategoryRarityModel();
        Map<Long, Integer> occurrencesToNumOfPartitions = new HashMap<>();
        occurrencesToNumOfPartitions.put(1L, 1);
        int numOfBuckets = 10;
        model.init(occurrencesToNumOfPartitions, null, numOfBuckets, 0, 1);
        CategoryRarityModelScorerAlgorithm algorithm =
                new CategoryRarityModelScorerAlgorithm(numOfBuckets / 2 + 1, numOfBuckets / 2 ,
                        X_WITH_VALUE_HALF_FACTOR, MIN_PROBABILITY);
        algorithm.calculateScore(1, model);
    }

    @Test
    public void shouldScore0ToFeatureCountsGreaterThanMaxRareCount() {
        int maxNumOfRarePartitions = 10;
        for (int maxRareCount = 1; maxRareCount < 10; maxRareCount++) {
            for (int count = 1; count <= maxRareCount + 1; count++) {
                double rangeMin = (count == maxRareCount + 1) ? 0 : 1;
                double rangeMax = (count == maxRareCount + 1) ? 0 : 100;
                assertScoreRange(maxRareCount, maxNumOfRarePartitions, createFeatureValueToCountWithConstantCounts(1, 10000), count, rangeMin, rangeMax);
            }
        }
    }

    @Test
    public void shouldScore96ToVeryRareFeatureWhenNoOtherRareFeaturesNoMatterWhatIsMaxRareCount() {
        int maxNumOfRarePartitions = 10;
        int veryRareFeatureCount = 1;
        for (int maxRareCount = 1; maxRareCount < 10; maxRareCount++) {
            Assert.assertEquals(96, calcScore(maxRareCount, maxNumOfRarePartitions, createFeatureValueToCountWithConstantCounts(1, 10000), veryRareFeatureCount), 0.0001);
        }
    }

    @Test
    public void shouldScore96ToVeryRareFeatureEvenWhenThereAreCommonFeatures() {
        int maxRareCount = 20;
        int maxNumOfRarePartitions = 20;
        int veryRareFeatureCount = 1;
        Assert.assertEquals(96, calcScore(maxRareCount, maxNumOfRarePartitions, createFeatureValueToCountWithConstantCounts(10000, maxRareCount + 2), veryRareFeatureCount, false), 0.0001);
    }

    @Test
    public void shouldScore0WhenThereAreMoreThanmaxNumOfRarePartitionsRareFeatures() {
        int maxRareCount = 10;
        int count = 1;
        for (int maxNumOfRarePartitions = 1; maxNumOfRarePartitions < 10; maxNumOfRarePartitions++) {
            for (int numOfFeatures = 0; numOfFeatures <= maxNumOfRarePartitions; numOfFeatures++) {
                double rangeMin = (numOfFeatures == maxNumOfRarePartitions) ? 0 : 1;
                double rangeMax = (numOfFeatures == maxNumOfRarePartitions) ? 0 : 100;
                assertScoreRange(maxRareCount, maxNumOfRarePartitions, createFeatureValueToCountWithConstantCounts(numOfFeatures, count, 1, 10000), count, rangeMin, rangeMax, false);
            }
        }
    }

    @Test
    public void shouldScore100ToVeryRareFeatureWhenNoOtherRareFeaturesNoMatterWhatIsmaxNumOfRarePartitions() {
        int maxRareCount = 10;
        int veryRareFeatureCount = 1;
        for (int maxNumOfRarePartitions = 1; maxNumOfRarePartitions < 10; maxNumOfRarePartitions++) {
            Assert.assertEquals(96, calcScore(maxRareCount, maxNumOfRarePartitions, createFeatureValueToCountWithConstantCounts(1, 90), veryRareFeatureCount), 0.0001);
        }
    }

    private double[][][] calcScoresOverConfigurationMatrix(int maxMaxRareCount, int maxmaxNumOfRarePartitions, int maxFeatureCountToScore) {
        return calcScoresOverConfigurationMatrix(createFeatureValueToCountWithConstantCounts(1, 100), maxMaxRareCount, maxmaxNumOfRarePartitions, maxFeatureCountToScore);
    }

    private double[][][] calcScoresOverConfigurationMatrix(Map<String, Long> featureValueToCountMap, int maxMaxRareCount, int maxmaxNumOfRarePartitions, int maxFeatureCountToScore) {
        double[][][] scores = new double[maxMaxRareCount][][];
        for (int maxRareCount = 1; maxRareCount <= maxMaxRareCount; maxRareCount++) {
            scores[maxRareCount - 1] = new double[maxmaxNumOfRarePartitions][];
            for (int maxNumOfRarePartitions = 1; maxNumOfRarePartitions <= maxmaxNumOfRarePartitions; maxNumOfRarePartitions++) {
                scores[maxRareCount - 1][maxNumOfRarePartitions - 1] = new double[maxFeatureCountToScore];
                for (int featureCountToScore = 1; featureCountToScore <= maxFeatureCountToScore; featureCountToScore++) {
                    scores[maxRareCount - 1][maxNumOfRarePartitions - 1][featureCountToScore - 1] = calcScore(maxRareCount, maxNumOfRarePartitions, featureValueToCountMap, featureCountToScore);
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

    private void assertMonotonicity(List<List<Double>> scoresSeries, Boolean shouldIncrease) {
        boolean hasStrongMonotonicity = false;
        for (List<Double> scores : scoresSeries) {
            if (scores.isEmpty()) {
                continue;
            }
            for (int i = 1; i < scores.size(); i++) {
                double scoresDelta = scores.get(i) - scores.get(i - 1);
                Assert.assertTrue(scoresDelta * (shouldIncrease ? 1 : -1) >= 0);
            }
            hasStrongMonotonicity = hasStrongMonotonicity || Math.abs(scores.get(scores.size() - 1) - scores.get(0)) > 0.0001;
        }
        if (shouldIncrease != null) {
            // it's ok that some series are constant, but if all of them are - the model probably has a bug
            Assert.assertTrue(hasStrongMonotonicity);
        }
    }

    private void assertMonotonicity(double[][][] scores, PARAMETER overParameter, Boolean shouldIncrease) {
        List<List<Double>> scoresSeries = new ArrayList<>();
        if (overParameter == PARAMETER.MAX_RARE_COUNT) {
            for (int maxNumOfRarePartitionsInd = 0; maxNumOfRarePartitionsInd < scores[0].length; maxNumOfRarePartitionsInd++) {
                for (int featureCountInd = 0; featureCountInd < scores[0][0].length; featureCountInd++) {
                    scoresSeries.add(new ArrayList<>());
                    for (int maxRareCountInd = 0; maxRareCountInd < scores.length; maxRareCountInd++) {
                        scoresSeries.get(scoresSeries.size() - 1).add(scores[maxRareCountInd][maxNumOfRarePartitionsInd][featureCountInd]);
                    }
                }
            }
        } else if (overParameter == PARAMETER.MAX_NUM_OF_RARE_FEATURES) {
            for (int maxRareCountInd = 0; maxRareCountInd < scores.length; maxRareCountInd++) {
                for (int featureCountInd = 0; featureCountInd < scores[0][0].length; featureCountInd++) {
                    scoresSeries.add(new ArrayList<>());
                    for (int maxNumOfRarePartitionsInd = 0; maxNumOfRarePartitionsInd < scores[0].length; maxNumOfRarePartitionsInd++) {
                        scoresSeries.get(scoresSeries.size() - 1).add(scores[maxRareCountInd][maxNumOfRarePartitionsInd][featureCountInd]);
                    }
                }
            }
        } else {
            for (int maxRareCountInd = 0; maxRareCountInd < scores.length; maxRareCountInd++) {
                for (int maxNumOfRarePartitionsInd = 0; maxNumOfRarePartitionsInd < scores[0].length; maxNumOfRarePartitionsInd++) {
                    scoresSeries.add(new ArrayList<>());
                    for (int featureCountInd = 0; featureCountInd < scores[0][0].length; featureCountInd++) {
                        scoresSeries.get(scoresSeries.size() - 1).add(scores[maxRareCountInd][maxNumOfRarePartitionsInd][featureCountInd]);
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
    public void shouldScoreIncreasinglyWhenmaxNumOfRarePartitionsIncreases() {
        Map<String, Long> featureValueToCountMap = new HashMap<>();
        featureValueToCountMap.put("veryRareFeature", 1L);
        featureValueToCountMap.put("veryCommonFeature", 100L);
        assertMonotonicity(calcScoresOverConfigurationMatrix(featureValueToCountMap, 10, 90, 10), PARAMETER.MAX_NUM_OF_RARE_FEATURES, true);
    }

    @Test
    public void shouldScoreConstantlyWhenmaxNumOfRarePartitionsIncreasesButModelDataIsEmpty() {
        assertMonotonicity(calcScoresOverConfigurationMatrix(10, 90, 10), PARAMETER.MAX_NUM_OF_RARE_FEATURES, true);
    }

    @Test
    public void shouldScoreIncreasinglyWhenProbabilityForRareFeatureEventsIncreases() {
        int maxRareCount = 10;
        int maxNumOfRarePartitions = 6;

        int veryRareFeatureCount = 1;
        List<Double> scores = new ArrayList<>();
        for (int commonFeatureCount = 10; commonFeatureCount < 100; commonFeatureCount += 10) {
            scores.add(calcScore(maxRareCount, maxNumOfRarePartitions, createFeatureValueToCountWithConstantCounts(1, veryRareFeatureCount, 1, commonFeatureCount), veryRareFeatureCount));
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

        int maxNumOfRarePartitionsToPrint = 10;
        boolean printedHeader = false;
        for (int maxRareCount = 0; maxRareCount < scores.length; maxRareCount++) {
            printNewLineOrHeader(printedHeader, "maxRareCountEffect", 1, scores[0][0].length);
            printedHeader = true;
            print(maxRareCount + "\t");
            for (int featureCount = 0; featureCount < scores[0][0].length; featureCount++) {
                print(scores[maxRareCount][maxNumOfRarePartitionsToPrint - 1][featureCount] + "\t");
            }
        }
    }

    @Test
    public void shouldScoreDecreasinglyWhenNumberOfRareFeaturesWithSameCountIncreases() {
        int maxRareCountToPrint = 15;
        int maxNumOfRarePartitionss[] = new int[]{5, 7, 9, 11, 13, 15};
        int counts[] = new int[]{1,4};

        boolean printedHeader = false;
        List<List<Double>> scoresSeries = new ArrayList<>();
        for (int maxRareCount = 1; maxRareCount < 20; maxRareCount++) {
            for (int count : counts) {
                for (int maxNumOfRarePartitions : maxNumOfRarePartitionss) {
                    int maxNumOfFeatures = maxRareCount + 1;
                    if (maxRareCount == maxRareCountToPrint) {
                        revertPrinting();
                        printNewLineOrHeader(printedHeader, "maxNumOfRarePartitionsEffect1", 0, maxNumOfFeatures - 1);
                        printedHeader = true;
                    } else {
                        turnOffPrinting();
                    }
                    print(count + "->" + maxNumOfRarePartitions + "\t");
                    List<Double> scores = new ArrayList<>(maxNumOfFeatures + 1);
                    for (int numOfFeatures = 0; numOfFeatures <= maxNumOfFeatures; numOfFeatures++) {
                        double score = calcScore(
                                maxRareCount,
                                maxNumOfRarePartitions,
                                createFeatureValueToCountWithConstantCounts(numOfFeatures, count, 10, 100),
                                count, false);
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
        int maxNumOfRarePartitionssToPrint[] = new int[]{5, 10, 15, 20, 30, 40, 50};

        List<List<Double>> scoresSeries = new ArrayList<>();
        boolean printedHeader = false;
        for (int maxRareCount = 1; maxRareCount < 30; maxRareCount++) {
            for (int numOfFeatures = 0; numOfFeatures < 10; numOfFeatures++) {
                for (int maxNumOfRarePartitions = 5; maxNumOfRarePartitions < 100; maxNumOfRarePartitions += 5) {
                    if (numOfFeatures <= maxNumOfFeaturesToPrint && maxRareCount == maxRareCountToPrint && ArrayUtils.contains(maxNumOfRarePartitionssToPrint, maxNumOfRarePartitions)) {
                        revertPrinting();
                        printNewLineOrHeader(printedHeader, "maxNumOfRarePartitionsEffect2", 1, maxRareCount);
                        printedHeader = true;
                    } else {
                        turnOffPrinting();
                    }
                    print(numOfFeatures + "->" + maxNumOfRarePartitions + "\t");
                    List<Double> scores = new ArrayList<>(maxRareCount - 1);
                    for (int featureCount = 1; featureCount <= maxRareCount; featureCount++) {
                        double score = calcScore(maxRareCount, maxNumOfRarePartitions, createFeatureValueToCountWithConstantCounts(numOfFeatures, featureCount, 10, 10), featureCount, false);
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
        int maxNumOfRarePartitions = 20;
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
                Instant startTime = Instant.parse("2007-12-03T10:00:00.00Z");
                CategoricalFeatureValue categoricalFeatureValue = new CategoricalFeatureValue(FixedDurationStrategy.HOURLY);
                updateCategoricalFeatureValue(categoricalFeatureValue, "daily-feature", 90L, startTime, 90);
                updateCategoricalFeatureValue(categoricalFeatureValue, createFeatureValueToCountWithConstantCounts(maxFeatureCount, featureCount), startTime, maxFeatureCount, false);
                double score = calcScore(maxRareCount, maxNumOfRarePartitions, 1, categoricalFeatureValue);
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
        int maxNumOfRarePartitions = 15;
        int veryRareFeatureCount = 1;
        int veryCommonFeatureCount = 10000;
        double scoreWithManyCommons = calcScore(maxRareCount, maxNumOfRarePartitions, createFeatureValueToCountWithConstantCounts(maxNumOfRarePartitions - 1, veryCommonFeatureCount), veryRareFeatureCount);
        double scoreWithOneCommon = calcScore(maxRareCount, maxNumOfRarePartitions, createFeatureValueToCountWithConstantCounts(1, veryCommonFeatureCount), veryRareFeatureCount);
        Assert.assertEquals(scoreWithOneCommon, scoreWithManyCommons, 1);
        Assert.assertTrue(scoreWithOneCommon >= 96);
    }

    @Test
    public void shouldScoreSecondSeenVeryRareFeatureIncreasinglyWhenCommonFeatureCountIncreases() {
        int maxRareCount = 10;
        int maxNumOfRarePartitions = 15;

        int veryRareFeatureCount = 1;
        List<Double> scores = new ArrayList<>();
        for (int commonFeatureCount = 20; commonFeatureCount < 1000; commonFeatureCount += 10) {
            scores.add(calcScore(maxRareCount, maxNumOfRarePartitions, createFeatureValueToCountWithConstantCounts(1, veryRareFeatureCount, 1, commonFeatureCount), veryRareFeatureCount));
        }
        List<List<Double>> scoresSeries = new ArrayList<>(1);
        scoresSeries.add(scores);
        assertMonotonicity(scoresSeries, true);
    }

    @Test
    public void elementaryCheck() {
        int maxRareCount = 15;
        int maxNumOfRarePartitions = 15;

        long count = 100;
        Map<String, Long> featureValueToCountMap = new HashMap<>();
        for (int i = 0; i < 100; i++) {
            featureValueToCountMap.put(String.format("test%d", i), count);
        }
        double score = calcScore(maxRareCount, maxNumOfRarePartitions, featureValueToCountMap, count);
        Assert.assertEquals(0.0, score, 0.0);
    }

    @Test
    public void simpleInputOutput() {
        for (int modelConfig = 0; modelConfig < 2; modelConfig++) {
            int maxRareCount = modelConfig == 0 ? 10 : 8;
            int maxNumOfRarePartitions = modelConfig == 0 ? 15 : 10;

            Map<String, Long> featureValueToCountMap = new HashMap<>();
            for (int i = 0; i < 2; i++) {
                featureValueToCountMap.put(String.format("test%d", i), 100L);
            }

            int[] counts = modelConfig == 0 ? new int[]{1, 2, 3, 4, 6} : new int[]{1, 2, 3, 4, 6};
            double[] scores = modelConfig == 0 ? new double[]{96, 92, 79, 51, 12} : new double[]{96, 91, 68, 32, 5};
            for (int i = 0; i < scores.length; i++) {
                Map<String, Long> featureValueToCountMapWithRareValue = new HashMap<>(featureValueToCountMap);
                int count = counts[i];
                if(count>1) {
                    featureValueToCountMapWithRareValue.put("rare-feature", count - 1L);
                }
                double score = calcScore(maxRareCount, maxNumOfRarePartitions, featureValueToCountMapWithRareValue, count);
                Assert.assertEquals(scores[i], score, 0);
            }
        }
    }

    @Test
    public void testingScoreOfVeryRareFeatureValueAgainstVeryLargeFeatureValuesWithValuesIncreasingByTime() {
        int maxRareCount = 10;
        int maxNumOfRarePartitions = 15;

        String rareFeature = "rareFeature";
        long[] rareCounts = new long[]{1, 2, 3, 4, 8, 9};
        long[] commonCounts = new long[]{20, 40, 60, 80, 160, 180};
        double[] scores = new double[]{84, 83, 74, 50, 3, 1};
        for (int i = 0; i < scores.length; i++) {
            Map<String, Long> featureValueToCountMap = new HashMap<>();
            for (int j = 0; j < maxNumOfRarePartitions - 1; j++) {
                featureValueToCountMap.put("commonFeature-" + j, commonCounts[i]);
            }
            if(rareCounts[i]>1) {
                featureValueToCountMap.put(rareFeature, rareCounts[i]-1);
            }
            double score = calcScore(maxRareCount, maxNumOfRarePartitions, featureValueToCountMap, rareCounts[i]);
            Assert.assertEquals(scores[i], score, 0);
        }
    }

    @Test
    public void testingScoreOfVeryRareFeatureValuesAgainstVeryLargeFeatureValues() {
        for (int modelConfig = 0; modelConfig < 2; modelConfig++) {
            int maxRareCount = modelConfig == 0 ? 8 : 12;
            int maxNumOfRarePartitions = modelConfig == 0 ? 10 : 15;


            Map<String, Long> featureValueToCountMap = createFeatureValueToCountWithConstantCounts(maxNumOfRarePartitions - 1, 200);
            long rareFeatureCountA = 1;
            featureValueToCountMap.put("rareFeatureA", rareFeatureCountA);
            double score = calcScore(maxRareCount, maxNumOfRarePartitions, featureValueToCountMap, rareFeatureCountA);
            Assert.assertEquals(modelConfig == 0 ? 92 : 92, score,1);

            long rareFeatureCountB = 2L;
            featureValueToCountMap.put("rareFeatureB", rareFeatureCountB);
            double scoreA = calcScore(maxRareCount, maxNumOfRarePartitions, featureValueToCountMap, rareFeatureCountA,false);
            double scoreB = calcScore(maxRareCount, maxNumOfRarePartitions, featureValueToCountMap, rareFeatureCountB, false);
            Assert.assertEquals(scoreA, scoreB, 1);
            Assert.assertEquals(modelConfig == 0 ? 52 : 76, scoreA, 1);

            long[] counts = new long[]{2, 1, 1, 1};
            double[] scores = modelConfig == 0 ? new double[]{12, 6, 4, 0} : new double[]{40, 24, 14, 8};
            for (int i = 0; i < scores.length; i++) {
                featureValueToCountMap.put(String.format("rareFeature-%d", i), counts[i]);
                score = calcScore(maxRareCount, maxNumOfRarePartitions, featureValueToCountMap, counts[i], false);
                Assert.assertEquals(scores[i], score, 1);
            }
        }
    }

    @Test
    public void testingScoreOfOneVeryRareFeatureValueAndManyRareFeatureValuesAgainstVeryLargeFeatureValues() {
        for (int modelConfig = 0; modelConfig < 2; modelConfig++) {
            int maxRareCount = modelConfig == 0 ? 6 : 8;
            int maxNumOfRarePartitions = modelConfig == 0 ? 10 : 15;

            Map<String, Long> featureValueToCountMap = createFeatureValueToCountWithConstantCounts(maxNumOfRarePartitions - 1, 2000);
            long veryRareFeatureCount = 1;
            featureValueToCountMap.put("veryRareFeatureValue", veryRareFeatureCount);
            double score = calcScore(maxRareCount, maxNumOfRarePartitions, featureValueToCountMap, 1);
            Assert.assertEquals(modelConfig == 0 ? 92 : 93, score, 1);

            long[] rareFeatureCounts = modelConfig == 0 ? new long[]{4, 3, 4, 3, 4} : new long[]{4, 3, 4, 3, 4};
            double[] rareFeaturesScores = modelConfig == 0 ? new double[]{12, 1, 1, 0, 0} : new double[]{30, 8, 1, 0, 0};
            double[] veryRareFeaturesScores = modelConfig == 0 ? new double[]{86, 48, 32, 10, 6} : new double[]{82, 42, 20, 6, 3};
            for (int i = 0; i < rareFeatureCounts.length; i++) {
                featureValueToCountMap.put("rareFeatureValue-" + i, rareFeatureCounts[i]);
                score = calcScore(maxRareCount, maxNumOfRarePartitions, featureValueToCountMap, veryRareFeatureCount, false);
                Assert.assertEquals(veryRareFeaturesScores[i], score, 1);
                score = calcScore(maxRareCount, maxNumOfRarePartitions, featureValueToCountMap, rareFeatureCounts[i], false);
                Assert.assertEquals(rareFeaturesScores[i], score, 1);
            }
        }
    }

    @Test
    public void testingScoreOfRareFeatureValuesAgainstMediumFeatureValuesWhichSpreadAccross90Days() {
        int maxRareCount = 10;
        int maxNumOfRarePartitions = 15;

        Map<String, Long> featureValueToCountMap = createFeatureValueToCountWithConstantCounts(maxNumOfRarePartitions - 1, 15);
        Instant startTime = Instant.parse("2007-12-03T10:00:00.00Z");
        CategoricalFeatureValue categoricalFeatureValue = new CategoricalFeatureValue(FixedDurationStrategy.HOURLY);
        updateCategoricalFeatureValue(categoricalFeatureValue, featureValueToCountMap, startTime, false);

        long rareFeatureCountA = 2;
        updateCategoricalFeatureValue(categoricalFeatureValue,"rareFeatureValue-A", rareFeatureCountA, startTime);
        double score = calcScore(maxRareCount, maxNumOfRarePartitions, rareFeatureCountA, categoricalFeatureValue);
        Assert.assertEquals(87, score, 1);

        long rareFeatureCountB = 3;
        updateCategoricalFeatureValue(categoricalFeatureValue,"rareFeatureValue-B", rareFeatureCountB, startTime);

        score = calcScore(maxRareCount, maxNumOfRarePartitions, rareFeatureCountA, categoricalFeatureValue);
        Assert.assertEquals(76, score, 1);

        score = calcScore(maxRareCount, maxNumOfRarePartitions, rareFeatureCountB, categoricalFeatureValue);
        Assert.assertEquals(76, score, 1);



        for (int i = 0; i < 3; i++) {
            long rareFeatureCount6 = 6;
            updateCategoricalFeatureValue(categoricalFeatureValue,"newRareFeatureValue-" + i, rareFeatureCount6, startTime);
            score = calcScore(maxRareCount, maxNumOfRarePartitions, rareFeatureCount6, categoricalFeatureValue);
            Assert.assertEquals(11, score, 1);
        }

        score = calcScore(maxRareCount, maxNumOfRarePartitions, rareFeatureCountA, categoricalFeatureValue);
        Assert.assertEquals(59, score, 1);

        score = calcScore(maxRareCount, maxNumOfRarePartitions, rareFeatureCountB, categoricalFeatureValue);
        Assert.assertEquals(42, score, 1);
    }

    @Test
    public void testingScoreOfOnlyRareFeatureValues() {
        int maxRareCount = 10;
        int maxNumOfRarePartitions = 15;

        Map<String, Long> featureValueToCountMap = new HashMap<>();
        long rareFeatureCount = 1;
        featureValueToCountMap.put("rareFeatureValue", rareFeatureCount);
        for (int i = 0; i < 4; i++) {
            featureValueToCountMap.put("newRareFeatureValue-" + i, 2L);
        }

        double score = calcScore(maxRareCount, maxNumOfRarePartitions, featureValueToCountMap, rareFeatureCount, false);
        Assert.assertEquals(0, score, 1);
    }

    @Test
    public void testingScoreOfRareFeatureValueAgainstMediumFeatureValuesAcrossTime() {
        int maxRareCount = 8;
        int maxNumOfRarePartitions = 15;

        Map<String, Long> featureValueToCountMap = new HashMap<>();

        long[] rareFeatureCounts = new long[]{2, 4};
        long[] mediumFeatureCounts = new long[]{8, 10};
        double[] rareFeatureScores = new double[]{45, 16};
        for (int i = 0; i < rareFeatureScores.length; i++) {
            for (int j =  0; j < 10; j++) {
                featureValueToCountMap.put("mediumFeatureValue-" + j, mediumFeatureCounts[i]);
            }
            featureValueToCountMap.put("rareFeatureValue", rareFeatureCounts[i]);
            double score = calcScore(maxRareCount, maxNumOfRarePartitions, featureValueToCountMap, rareFeatureCounts[i], false);
            Assert.assertEquals(rareFeatureScores[i], score, 1);
        }
    }

    @Test
    public void testRareToMediumFeatureValueAgainstMediumLargeFeatureValues() {
        int maxRareCount = 8;
        int maxNumOfRarePartitions = 15;

        int mediumLargeFeatureCount = 13;
        Map<String, Long> featureValueToCountMap = createFeatureValueToCountWithConstantCounts(maxNumOfRarePartitions - 1, mediumLargeFeatureCount);
        double score = calcScore(maxRareCount, maxNumOfRarePartitions, featureValueToCountMap, mediumLargeFeatureCount);
        Assert.assertEquals(0, score, 0);

        long[] rareFeatureCounts = new long[]{1, 2, 3, 4, 5, 8};
        double[] rareFeatureScores = new double[]{96, 91, 68, 32, 12, 0};
        for (int i = 0; i < rareFeatureScores.length; i++) {
            Map<String, Long> featureValueToCountMapWithRareValue = new HashMap<>(featureValueToCountMap);
            if(rareFeatureCounts[i]>1) {
                featureValueToCountMapWithRareValue.put("rareFeature", rareFeatureCounts[i] - 1);
            }
            score = calcScore(maxRareCount, maxNumOfRarePartitions, featureValueToCountMapWithRareValue, rareFeatureCounts[i], false);
            Assert.assertEquals(rareFeatureScores[i], score, 1);
        }
    }

    @Test
    public void testRareToMediumFeatureValueAgainstRareFeatureValueAndMediumFeatureValue() {
        int maxRareCount = 10;
        int maxNumOfRarePartitions = 15;

        double[] scores = new double[]{40, 6, 0, 0, 0, 0, 0, 0, 0, 0};
        for (int rareFeatureCount = 1; rareFeatureCount < 11; rareFeatureCount++) {
            double score = calcScore(maxRareCount, maxNumOfRarePartitions, createFeatureValueToCountWithConstantCounts(1, 4, 1, 15, 1, rareFeatureCount-1), rareFeatureCount, false);
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
        private int maxNumOfRarePartitions;
        private Map<String, Long> featureValueToCountMap;
        private Map<String, Set<Date>> featureValueToDaysMap;

        @Override
        public void onScenarioRunStart() {
            maxRareCount = 10;
            maxNumOfRarePartitions = 6;
            featureValueToCountMap = new HashMap<>();
            featureValueToDaysMap = new HashMap<>();
        }

        @Override
        public Double onScore(TestEventsBatch eventsBatch) {
            long eventFeatureCount = featureValueToCountMap.getOrDefault(eventsBatch.getFeature(), 0L);
            return calcScore(maxRareCount, maxNumOfRarePartitions, featureValueToCountMap, eventFeatureCount + 1);
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
