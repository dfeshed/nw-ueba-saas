package fortscale.ml.model.prevalance.field;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import fortscale.utils.time.TimestampUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

@RunWith(JUnit4.class)
public class CategoryRarityModelTest {
	private Double calcScore(int minEvents, int maxRareCount, int maxNumOfRareFeatures, Map<String, Integer> featureValueToCountMap, int featureCountToScore) {
		Map<Integer, Double> occurrencesToNumOfFeatures = new HashMap<>();
		for (int count : featureValueToCountMap.values()) {
			double lastCount = occurrencesToNumOfFeatures.getOrDefault(count, 0D);
			occurrencesToNumOfFeatures.put(count, lastCount + 1);
		}
		CategoryRarityModel model = new CategoryRarityModel(minEvents, maxRareCount, maxNumOfRareFeatures, occurrencesToNumOfFeatures);
		return model.calculateScore(featureCountToScore);
	}

	private Double calcScore(int maxRareCount, int maxNumOfRareFeatures, Map<String, Integer> featureValueToCountMap, int featureCountToScore) {
		return calcScore(1, maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, featureCountToScore);
	}

	private void assertScoreRange(int maxRareCount, int maxNumOfRareFeatures, Map<String, Integer> featureValueToCountMap, int featureCount, double expectedRangeMin, double expectedRangeMax) {
		double score = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, featureCount);
		Assert.assertTrue(String.format("score (%e) >= expectedRangeMin (%e) does not hold", score, expectedRangeMin), score >= expectedRangeMin);
		Assert.assertTrue(String.format("score (%e) <= expectedRangeMax (%e) does not hold", score, expectedRangeMax), score <= expectedRangeMax);
	}

	private Map<String, Integer> createFeatureValueToCountWithConstantCounts(int... numOfFeaturesAndCounts) {
		if (numOfFeaturesAndCounts.length % 2 == 1) {
			throw new IllegalArgumentException("should get an even number of parameters");
		}
		Map<String, Integer> res = new HashMap<>();
		for (int i = 0; i < numOfFeaturesAndCounts.length; i += 2) {
			int numOfFeatures = numOfFeaturesAndCounts[i];
			int count = numOfFeaturesAndCounts[i + 1];
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
	 ************ TEST BASIC MODEL BEHAVIOUR WHEN MODEL PARAMETERS ARE ISOLATED **********
	 *************************************************************************************
	 *************************************************************************************/

	@Test
	public void shouldScoreNullWhenGivenNotEnoughOfData() {
		int count = 10;
		int maxRareCount = 5;
		int maxNumOfRareFeatures = 15;
		Map<String, Integer> featureValueToCountWithConstantCounts = createFeatureValueToCountWithConstantCounts(1, count);
		int featureCountToScore = 1;

		Assert.assertNull(calcScore(count + 1, maxRareCount, maxNumOfRareFeatures, featureValueToCountWithConstantCounts, featureCountToScore));
		Assert.assertNotNull(calcScore(count, maxRareCount, maxNumOfRareFeatures, featureValueToCountWithConstantCounts, featureCountToScore));
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
		int maxNumOfRareFeatures = 100;
		int veryRareFeatureCount = 1;
		Assert.assertEquals(100, calcScore(maxRareCount, maxNumOfRareFeatures, createFeatureValueToCountWithConstantCounts(10000, maxRareCount + 2), veryRareFeatureCount), 0.0001);
	}

	@Test
	public void shouldScore0WhenThereAreMoreThanMaxNumOfRareFeaturesRareFeatures() {
		int maxRareCount = 100;
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

	private double[][][] calcScoresOverConfigurationMatrix(Map<String, Integer> featureValueToCountMap, int maxMaxRareCount, int maxMaxNumOfRareFeatures, int maxFeatureCountToScore) {
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
		assertMonotonicity(calcScoresOverConfigurationMatrix(10, 100, 10), PARAMETER.FEATURE_COUNT, false);
	}

	@Test
	public void shouldScoreIncreasinglyWhenMaxNumOfRareFeaturesIncreases() {
		Map<String, Integer> featureValueToCountMap = new HashMap<>();
		featureValueToCountMap.put("veryRareFeature", 1);
		featureValueToCountMap.put("veryCommonFeature", 1000);
		assertMonotonicity(calcScoresOverConfigurationMatrix(featureValueToCountMap, 10, 100, 10), PARAMETER.MAX_NUM_OF_RARE_FEATURES, true);
	}

	@Test
	public void shouldScoreConstantlyWhenMaxNumOfRareFeaturesIncreasesButModelDataIsEmpty() {
		assertMonotonicity(calcScoresOverConfigurationMatrix(10, 100, 10), PARAMETER.MAX_NUM_OF_RARE_FEATURES, null);
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
	 *********** GRAPHS SHOWING HOW MODEL BEHAVES WHEN PARAMETERS ARE ISOLATED ***********
	 ************************** JUST PUT "PRINT_GRAPHS = true" ***************************
	 *************************************************************************************
	 *************************************************************************************/

	private static final boolean PRINT_GRAPHS = false;

	private static boolean printingOffOverride = false;

	private static void turnOffPrinting() {
		printingOffOverride = true;
	}

	private static void revertPrinting() {
		printingOffOverride = false;
	}

	private static void print(String msg) {
		if (PRINT_GRAPHS && !printingOffOverride) {
			System.out.print(msg);
		}
	}

	private static void println(String msg) {
		print(msg + "\n");
	}

	private static void println() {
		println("");
	}

	private void printNewLineOrHeader(boolean printedHeader, String googleSheetName, int fromCount, int toCount) {
		int counts[] = new int[toCount - fromCount + 1];
		for (int i = 0; i < counts.length; i++) {
			counts[i] = fromCount + i;
		}
		printNewLineOrHeader(printedHeader, googleSheetName, counts);
	}

	private void printGoogleSheetsExplaination(String googleSheetName) {
		println("\n\n\nCopy the following output into \"" + googleSheetName + "\" sheet in the following URL: https://docs.google.com/spreadsheets/d/1eNqu2K3mIUCH3b-NXeQM5VqBkcaEqwcSxiNWZ-FzdHg/edit#gid=1047563136&vpid=A1\n");
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
		double[][][] scores = calcScoresOverConfigurationMatrix(maxMaxRareCount, 100, maxFeatureCountToScore);

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
	 ***** (BUT NOT AS BASIC AS THE TESTS WHICH TRY TO ISOLATE THE MODEL PARAMETERS ******
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

		int count = 100;
		Map<String, Integer> featureValueToCountMap = new HashMap<>();
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

			Map<String, Integer> featureValueToCountMap = new HashMap<>();
			for (int i = 0; i < 2; i++) {
				featureValueToCountMap.put(String.format("test%d", i), 100);
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

		Map<String, Integer> featureValueToCountMap = new HashMap<>();
		String rareFeature = "rareFeature";
		int[] rareCounts = new int[]{1, 2, 3, 4, 8, 9};
		int[] commonCounts = new int[]{50, 100, 150, 200, 400, 450};
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

			Map<String, Integer> featureValueToCountMap = createFeatureValueToCountWithConstantCounts(maxNumOfRareFeatures - 1, 2000);
			int rareFeatureCountA = 1;
			featureValueToCountMap.put("rareFeatureA", rareFeatureCountA);
			double score = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, rareFeatureCountA);
			Assert.assertEquals(97, score, 1);

			int rareFeatureCountB = 2;
			featureValueToCountMap.put("rareFeatureB", 2);
			double scoreA = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, rareFeatureCountA);
			double scoreB = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, rareFeatureCountB);
			Assert.assertEquals(scoreA, scoreB, 1);
			Assert.assertEquals(modelConfig == 0 ? 86 : 91, scoreA, 1);

			int[] counts = new int[]{2, 1, 1, 1};
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

			Map<String, Integer> featureValueToCountMap = createFeatureValueToCountWithConstantCounts(maxNumOfRareFeatures - 1, 2000);
			int veryRareFeatureCount = 1;
			featureValueToCountMap.put("veryRareFeatureValue", veryRareFeatureCount);
			double score = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, 1);
			Assert.assertEquals(96, score, 1);

			int[] rareFeatureCounts = modelConfig == 0 ? new int[]{3, 4, 2, 3, 4} : new int[]{5, 6, 4, 5, 6, 4};
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

		Map<String, Integer> featureValueToCountMap = createFeatureValueToCountWithConstantCounts(maxNumOfRareFeatures - 1, 15);

		int rareFeatureCountA = 2;
		featureValueToCountMap.put("rareFeatureValue-A", rareFeatureCountA);
		double score = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, rareFeatureCountA);
		Assert.assertEquals(92, score, 1);

		int rareFeatureCountB = 3;
		featureValueToCountMap.put("rareFeatureValue-B", rareFeatureCountB);
		score = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, rareFeatureCountA);
		Assert.assertEquals(80, score, 1);

		double[] scores = new double[]{57, 40};
		for (int i = 0; i < scores.length; i++) {
			int rareFeatureCount3 = 3;
			featureValueToCountMap.put("newRareFeatureValue-" + i, rareFeatureCount3);
			score = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, rareFeatureCount3);
			Assert.assertEquals(scores[i], score, 1);
		}
	}

	@Test
	public void testingScoreOfOnlyRareFeatureValues() {
		int maxRareCount = 10;
		int maxNumOfRareFeatures = 6;

		Map<String, Integer> featureValueToCountMap = new HashMap<>();
		int rareFeatureCount = 1;
		featureValueToCountMap.put("rareFeatureValue", rareFeatureCount);
		for (int i = 0; i < 4; i++) {
			featureValueToCountMap.put("newRareFeatureValue-" + i, 2);
		}

		double score = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, rareFeatureCount);
		Assert.assertEquals(0, score, 1);
	}

	@Test
	public void testingScoreOfRareFeatureValueAgainstMediumFeatureValuesAcrossTime() {
		int maxRareCount = 10;
		int maxNumOfRareFeatures = 6;

		Map<String, Integer> featureValueToCountMap = new HashMap<>();

		int[] rareFeatureValues = new int[]{2, 4};
		int[] mediumFeatureValues = new int[]{8, 10};
		double[] rareFeatureScores = new double[]{79, 47};
		for (int i = 0; i < rareFeatureScores.length; i++) {
			for (int j =  0; j < 10; j++) {
				featureValueToCountMap.put("mediumFeatureValue-" + j, mediumFeatureValues[i]);
			}
			featureValueToCountMap.put("rareFeatureValue", rareFeatureValues[i]);
			double score = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, rareFeatureValues[i]);
			Assert.assertEquals(rareFeatureScores[i], score, 1);
		}
	}

	@Test
	public void testRareToMediumFeatureValueAgainstMediumLargeFeatureValues() {
		int maxRareCount = 10;
		int maxNumOfRareFeatures = 6;

		int mediumLargeFeatureCount = 13;
		Map<String, Integer> featureValueToCountMap = createFeatureValueToCountWithConstantCounts(maxNumOfRareFeatures - 1, mediumLargeFeatureCount);
		double score = calcScore(maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, mediumLargeFeatureCount);
		Assert.assertEquals(0, score, 0);

		int[] rareFeatureCounts = new int[]{1, 2, 3, 4, 5, 8};
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
	 *********** IN ORDER TO GENERATE THE DATA, RUN get_ssh_research_data.bash ***********
	 ********* (from research project: https://bitbucket.org/fortscale/research) *********
	 ******************* (DON'T FORGET TO CHANGE "PRINT_GRAPHS = true") ******************
	 *************************************************************************************
	 *************************************************************************************/

	private static class TestEventsBatch {
		public int num_of_events;
		public long time_bucket;
		public String normalized_src_machine;
		public String normalized_dst_machine;
	}

	private String getAbsoluteFilePath(String fileName) throws FileNotFoundException {
		URL fileURL = getClass().getClassLoader().getResource(fileName);
		if (fileURL == null) {
			throw new FileNotFoundException("file " + fileName + " not exist");
		}
		return fileURL.getFile();
	}

	private List<TestEventsBatch> readEventsFromCsv(String csvFileName) throws IOException {
		File csvFile = new File(getAbsoluteFilePath(csvFileName));
		CsvSchema schema = CsvSchema.emptySchema().withHeader().withColumnSeparator(',');
		MappingIterator<TestEventsBatch> it = new CsvMapper().reader(TestEventsBatch.class).with(schema).readValues(csvFile);
		List<TestEventsBatch> res = new ArrayList<>();
		while (it.hasNext()){
			TestEventsBatch event = it.next();
			if (!StringUtils.isBlank(event.normalized_dst_machine)) {
				res.add(event);
			}
		}
		return res;
	}

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

	private final static String EMPTY_STRING = "(empty string)";
	private final static String COLOR_NORMAL = "\033[0m";

	/**
	 * Print info about the context available when the given feature value was scored, e.g.:
	 * #0  hostname_36643275                       : 4356   	0000000000111111111122222222223333
	 * #1  hostname_16101171                       : 226    	0000000000111111111122
	 * #2  service_name_177266206                  : 397    	00000000001111111111222
	 *
	 * 		scoring hostname_74957113 which has 1 events. In total there are 4980 events spread across 3 features.
	 * 		score: 100
	 *
	 * @param eventTime time in which the event has occurred.
	 * @param featureValue the feature value who's been scored.
	 * @param featureValueToCountMap context info - what feature values were encountered in the past, and how often.
	 */
	private void printEvent(long eventTime, String featureValue, Double score, Map<String, Integer> featureValueToCountMap) {
		List<String> featureValues = new ArrayList<>(featureValueToCountMap.keySet());
		printFeatureValuesHistogram(featureValues, featureValueToCountMap);
		int featureValueIndex = featureValues.indexOf(featureValue);
		int totalNumOfEvents = 1;
		for (int count : featureValueToCountMap.values()) {
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
	 * #0  hostname_36643275                       : 4356   	0000000000111111111122222222223333
	 * #1  hostname_16101171                       : 226    	0000000000111111111122
	 * #2  service_name_177266206                  : 397    	00000000001111111111222
	 *
	 * @param featureValues the available feature values in the distribution.
	 *                      The histogram's bars will be ordered according to featureValues.
	 * @param featureValueToCountMap the distribution of feature values.
	 *                               The keys of this map are the feature values contained in featureValues.
	 */
	private void printFeatureValuesHistogram(List<String> featureValues, Map<String, Integer> featureValueToCountMap) {
		String BAR_COLORS[] = new String[]{"\033[36m", "\033[32m", "\033[33m", "\033[31m"};

		for (int featureValueInd = 0; featureValueInd < featureValues.size(); featureValueInd++) {
			String featureValue = featureValues.get(featureValueInd);
			int count = featureValueToCountMap.get(featureValue);
			String bar = "";
			int base = 0;
			int barLength = count;
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
			println(String.format("#%-3d%s%s%s: %-7d\t%s",
					featureValueInd,
					featureColor,
					StringUtils.rightPad(StringUtils.isBlank(featureValue) ? EMPTY_STRING : featureValue, 40),
					COLOR_NORMAL,
					count,
					bar));
		}
	}

	private static class ScoredFeature {
		public String featureValue;
		public Double score;

		public ScoredFeature(String featureValue, Double score) {
			this.featureValue = featureValue;
			this.score = score;
		}
	}

	private static class ScenarioStats {
		private Map<String, FeatureStats> featureTofeatureStats;
		private List<ScoredFeature> featureValueAndScores;
		private int numOfProcessedEvents;

		public ScenarioStats() {
			featureTofeatureStats = new HashMap<>();
			featureValueAndScores = new ArrayList<>();
			numOfProcessedEvents = 0;
		}

		public void addEventInfo(long eventTime, String feature, Double score, boolean isScoreInteresting) {
			numOfProcessedEvents++;
			FeatureStats featureStats = featureTofeatureStats.get(feature);
			if (featureStats == null) {
				featureStats = new FeatureStats();
				featureTofeatureStats.put(feature, featureStats);
			}
			featureStats.addEventInfo(eventTime, numOfProcessedEvents);
			if (isScoreInteresting) {
				featureValueAndScores.add(new ScoredFeature(feature, score));
			}
		}

		public void print() {
			if (featureValueAndScores.isEmpty()) {
				return;
			}

			println("first time events:");
			for (FeatureStats featureStats : featureTofeatureStats.values()) {
				String date = getFormattedDate(featureStats.firstEventTime);
				println(String.format("\t#%-8d %s", featureStats.firstEventIndex, date));
			}
		}
	}

	private static class FeatureStats {
		public Long firstEventTime;
		private int firstEventIndex;
		public Long lastEventTime;
		private int lastEventIndex;

		public FeatureStats() {
			firstEventTime = null;
			lastEventTime = null;
		}

		public void addEventInfo(long eventTime, int eventIndex) {
			if (firstEventTime == null) {
				firstEventTime = eventTime;
				firstEventIndex = eventIndex;
				lastEventTime = eventTime;
				lastEventIndex = eventIndex;
			} else {
				firstEventTime = Math.min(firstEventTime, eventTime);
				firstEventIndex = Math.min(firstEventIndex, eventIndex);
				lastEventTime = Math.max(lastEventTime, eventTime);
				lastEventIndex = Math.max(lastEventIndex, eventIndex);
			}
		}
	}

	/**
	 * Run a real data scenario.
	 * The first 90% of the events won't be scored (they are only used for building the model).
	 * @param scenarioInfo all the info about the scenario needed in order to run it.
	 * @param minDate events occurring before this time won't be scored.
	 * @param minInterestingScore scores smaller than this number are considered not interesting, and won't be included in the result.
	 * @param printContextInfo if true, context info will be printed (aids in understanding the result scores).
	 * @return statistics about the result of running the scenario.
	 * @throws IOException
	 */
	private ScenarioStats runRealScenario(ScenarioInfo scenarioInfo, int minDate, int minInterestingScore, boolean printContextInfo) throws IOException {
		int maxRareCount = 10;
		int maxNumOfRareFeatures = 6;
		Map<String, Integer> featureValueToCountMap = new HashMap<>();
		ScenarioStats scenarioStats = new ScenarioStats();
		for (final TestEventsBatch eventsBatch : scenarioInfo.eventsBatches) {
			for (int i = 0; i < eventsBatch.num_of_events; i++) {
				int eventFeatureCount = featureValueToCountMap.getOrDefault(eventsBatch.normalized_dst_machine, 0);
				Double score = calcScore(1, maxRareCount, maxNumOfRareFeatures, featureValueToCountMap, eventFeatureCount + 1);
				boolean isScoreInteresting = eventsBatch.time_bucket >= minDate && score != null && score > minInterestingScore;
				scenarioStats.addEventInfo(eventsBatch.time_bucket, eventsBatch.normalized_dst_machine, score, isScoreInteresting);
				if (isScoreInteresting) {
					if (printContextInfo) {
						printEvent(eventsBatch.time_bucket, eventsBatch.normalized_dst_machine, score, featureValueToCountMap);
					}
				}
				featureValueToCountMap.put(eventsBatch.normalized_dst_machine, eventFeatureCount + 1);
			}
		}
		if (printContextInfo) {
			scenarioStats.print();
		}
		return scenarioStats;
	}

	private static String getFormattedDate(long date) {
		return new SimpleDateFormat("dd/MM/yy HH:mm:ss").format(new Date(TimestampUtils.convertToMilliSeconds(date)));
	}

	private class ScenarioInfo {
		public String filePath;
		private List<TestEventsBatch> eventsBatches;
		public int numOfEvents;
		public Long firstEventTime;
		public Long lastEventTime;

		public ScenarioInfo(String filePath) throws IOException {
			this.filePath = filePath;
			eventsBatches = readEventsFromCsv(filePath);
			numOfEvents = 0;
			firstEventTime = null;
			lastEventTime = null;
			if (eventsBatches != null) {
				for (TestEventsBatch eventsBatch : eventsBatches) {
					numOfEvents += eventsBatch.num_of_events;
				}
				if (!eventsBatches.isEmpty()) {
					firstEventTime = eventsBatches.get(0).time_bucket;
					lastEventTime = eventsBatches.get(eventsBatches.size() - 1).time_bucket;
				}
			}
		}
	}

	/**
	 * Given the results of running a real data scenario, print a graph of the results.
	 * @param googleSheetName the name of the google sheet which is capable of displaying the results of this function.
	 * @param featureValueAndScores the result of running a real data scenario.
	 */
	private void printRealScenarioGraph(String googleSheetName, List<ScoredFeature> featureValueAndScores) {
		printGoogleSheetsExplaination("real-scenario-" + googleSheetName);
		Set<String> featureValuesSet = new HashSet<>();
		for (ScoredFeature scoredFeature : featureValueAndScores) {
			featureValuesSet.add(scoredFeature.featureValue);
		}
		List<String> featureValues = new ArrayList<>(featureValuesSet);
		for (String featureValue : featureValues) {
			print((StringUtils.isBlank(featureValue) ? EMPTY_STRING : featureValue) + "\t");
		}
		println();
		String tabs = "";
		for (int i = 0; i < featureValues.size(); i++) {
			tabs += "\t";
		}
		for (ScoredFeature scoredFeature : featureValueAndScores) {
			int featureValueIndex = featureValues.indexOf(scoredFeature.featureValue);
			print(tabs.substring(0, featureValueIndex));
			print("" + scoredFeature.score);
			print(tabs.substring(0, featureValues.size() - featureValueIndex));
			println();
		}
	}

	private void runAndPrintRealScenario(ScenarioInfo scenarioInfo, int minDate, int minInterestingScore) throws IOException {
		if (PRINT_GRAPHS == false) {
			return;
		}
		ScenarioStats scenarioStats = runRealScenario(scenarioInfo, minDate, minInterestingScore, true);
		printRealScenarioGraph(scenarioInfo.filePath.substring(0, scenarioInfo.filePath.indexOf('/')), scenarioStats.featureValueAndScores);
	}

	public static final String REAL_SCENARIOS_SSH_SRC_MACHINE_PATH = "ssh-src-machine";

	@Test
	public void testRealScenarioSshSrcMachineUsername_42423294() throws IOException {
		String filePath = REAL_SCENARIOS_SSH_SRC_MACHINE_PATH + "/username_42423294.csv";
		try {
			ScenarioInfo scenarioInfo = new ScenarioInfo(filePath);
			runAndPrintRealScenario(scenarioInfo, (int) (scenarioInfo.firstEventTime + (scenarioInfo.lastEventTime - scenarioInfo.firstEventTime) * 0.9), 0);
		} catch (FileNotFoundException e) {
			println("file not found");
		}
	}

	private static class UsersStatistics {
		public int numOfRegularUsers;
		public Map<String, Integer> anomalousUserScenarioToNumOfEvents;

		public UsersStatistics() {
			this.numOfRegularUsers = 0;
			this.anomalousUserScenarioToNumOfEvents = new HashMap<>();
		}

		public int getNumOfRegularUsers() {
			return numOfRegularUsers;
		}

		public int getNumOfAnomalousUsers() {
			return anomalousUserScenarioToNumOfEvents.size();
		}
	}

	private class ScenariosInfo {
		public Long firstEventTime;
		public Long lastEventTime;

		private List<Map.Entry<ScenarioInfo, Integer>> sortedScenariosByNumOfEvents;

		public ScenariosInfo(String dirPath) throws IOException {
			List<ScenarioInfo> scenarioInfos = getScenarioInfos(dirPath);
			Map<ScenarioInfo, Integer> scenarioToNumOfEvents = new HashMap<>(scenarioInfos.size());
			if (scenarioInfos.isEmpty()) {
				firstEventTime = null;
				lastEventTime = null;
				sortedScenariosByNumOfEvents = new ArrayList<>();
			} else {
				firstEventTime = Long.MAX_VALUE;
				lastEventTime = Long.MIN_VALUE;
				for (ScenarioInfo scenarioInfo : scenarioInfos) {
					if (scenarioInfo.numOfEvents > 0) {
						firstEventTime = Math.min(firstEventTime, scenarioInfo.firstEventTime);
						lastEventTime = Math.max(lastEventTime, scenarioInfo.lastEventTime);
						scenarioToNumOfEvents.put(scenarioInfo, scenarioInfo.numOfEvents);
					}
				}

				sortedScenariosByNumOfEvents = sortMapByValues(scenarioToNumOfEvents);
			}
		}

		private List<ScenarioInfo> getScenarioInfos(String dirPath) throws IOException {
			File[] scenarioFiles = new File(dirPath).listFiles();
			List<ScenarioInfo> scenarioInfos = new ArrayList<>(scenarioFiles.length);
			for (File file : scenarioFiles) {
				scenarioInfos.add(new ScenarioInfo(REAL_SCENARIOS_SSH_SRC_MACHINE_PATH + "/" + file.getName()));
			}
			return scenarioInfos;
		}

		public int size() {
			return sortedScenariosByNumOfEvents.size();
		}

		public ScenarioInfo get(int i) {
			return sortedScenariosByNumOfEvents.get(i).getKey();
		}
	}

	@Test
	public void testRealScenariosHowManyAnomalousUsers() throws IOException {
		ScenariosInfo scenariosInfo;
		try {
			scenariosInfo = new ScenariosInfo(getAbsoluteFilePath(REAL_SCENARIOS_SSH_SRC_MACHINE_PATH));
		} catch (FileNotFoundException e) {
			println("directory not found");
			return;
		}
		int minDate = (int) (scenariosInfo.firstEventTime + (scenariosInfo.lastEventTime - scenariosInfo.firstEventTime) * 0.9);

		// run all the scenarios and create some statistics:
		Map<Integer, UsersStatistics> logNumOfEventsToUsersStatistics = new HashMap<>();
		Map<ScenarioInfo, ScenarioStats> scenarioToStats = new HashMap<>(scenariosInfo.size());
		for (int i = 0; i < scenariosInfo.size(); i++) {
			ScenarioInfo scenarioInfo = scenariosInfo.get(i);
			println("\nrunning scenario " + i + " / " + scenariosInfo.size() + " " + scenarioInfo.filePath + " (min date " + getFormattedDate(minDate) + ")");
			ScenarioStats scenarioStats = runRealScenario(scenarioInfo, minDate, 50, true);
			scenarioToStats.put(scenarioInfo, scenarioStats);

			int logNumOfEvents = (int) (Math.log(scenarioInfo.numOfEvents) / Math.log(10));
			UsersStatistics usersStatistics = logNumOfEventsToUsersStatistics.get(logNumOfEvents);
			if (usersStatistics == null) {
				usersStatistics = new UsersStatistics();
				logNumOfEventsToUsersStatistics.put(logNumOfEvents, usersStatistics);
			}
			if (!scenarioStats.featureValueAndScores.isEmpty()) {
				usersStatistics.anomalousUserScenarioToNumOfEvents.put(scenarioInfo.filePath, scenarioInfo.numOfEvents);
			} else {
				usersStatistics.numOfRegularUsers++;
			}
		}

		// print interesting stuff about the results:
		printAnomalousUsersRatios(scenariosInfo, logNumOfEventsToUsersStatistics);

		// assert stuff
		int totalAnomalousUsers = getTotalAnomalousUsers(logNumOfEventsToUsersStatistics);
		Assert.assertEquals(0.109, (double) totalAnomalousUsers / scenariosInfo.size(), 0.01);
	}

	private Integer getTotalAnomalousUsers(Map<Integer, UsersStatistics> logNumOfEventsToUsersStatistics) {
		return logNumOfEventsToUsersStatistics.entrySet().stream()
				.map((entry) -> entry.getValue().getNumOfAnomalousUsers())
				.reduce((numOfAnomalousUsers1, numOfAnomalousUsers2) -> numOfAnomalousUsers1 + numOfAnomalousUsers2)
				.get();
	}

	private void printAnomalousUsersRatios(ScenariosInfo scenariosInfo, Map<Integer, UsersStatistics> logNumOfEventsToUsersStatistics) {
		println(String.format("\n%s: <anomalous users / <total users> -> followed by a list of the anomalous users", StringUtils.rightPad("<number of events>", 20)));
		for (Map.Entry<Integer, UsersStatistics> entry : logNumOfEventsToUsersStatistics.entrySet()) {
			Integer logNumOfEvents = entry.getKey();
			UsersStatistics usersStatistics = entry.getValue();
			String logNumOfEventsRange = (int) Math.pow(10, logNumOfEvents) + " - " + (int) Math.pow(10, logNumOfEvents + 1);
			println(String.format("%s: %3d%%   %-6d / %-6d anomalous users",
					StringUtils.rightPad(logNumOfEventsRange, 20),
					100 * usersStatistics.getNumOfAnomalousUsers() / (usersStatistics.getNumOfRegularUsers() + usersStatistics.getNumOfAnomalousUsers()),
					usersStatistics.getNumOfAnomalousUsers(),
					usersStatistics.numOfRegularUsers + usersStatistics.getNumOfAnomalousUsers()));
			for (Map.Entry<String, Integer> e : sortMapByValues(usersStatistics.anomalousUserScenarioToNumOfEvents)) {
				println(String.format("\t%-6d: %s", e.getValue(), e.getKey()));
			}
		}
		println(String.format("\ntotal %d / %d anomalous users", getTotalAnomalousUsers(logNumOfEventsToUsersStatistics), scenariosInfo.size()));
	}

	private static <T> List<Map.Entry<T, Integer>> sortMapByValues(Map<T, Integer> m) {
		List<Map.Entry<T, Integer>> sortedList = new ArrayList<>(m.entrySet());
		Collections.sort(sortedList, (o1, o2) -> o1.getValue().compareTo(o2.getValue()));
		return sortedList;
	}
}
