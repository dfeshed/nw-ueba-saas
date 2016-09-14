package fortscale.ml.model.builder;

import fortscale.ml.model.SMARTScoreMappingModel;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SMARTScoreMappingModelBuilderTest {
	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenNullAsInput() {
		new SMARTValuesModelBuilder().build(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenIllegalInputType() {
		new SMARTValuesModelBuilder().build("");
	}

	private SMARTScoreMappingModel buildModel(double defaultThreshold,
											  double defaultMaximalScore,
											  double minThreshold,
											  double minMaximalScore,
											  double lowOutliersFraction,
											  double highOutliersFraction,
											  Double[]... dailyScores) {
		Map<Long, List<Double>> dateToHighestScores = new HashMap<>();
		for (int day = 0; day < dailyScores.length; day++) {
			dateToHighestScores.put((long) day, Arrays.asList(dailyScores[day]));
		}
		SMARTScoreMappingModelBuilderConf conf = new SMARTScoreMappingModelBuilderConf(
				defaultThreshold,
				defaultMaximalScore,
				minThreshold,
				minMaximalScore,
				lowOutliersFraction,
				highOutliersFraction
		);
		return new SMARTScoreMappingModelBuilder(conf).build(dateToHighestScores);
	}

	private void assertModel(double expectedThreshold, double expectedMaximalValue, SMARTScoreMappingModel model) {
		Map<Double, Double> mapping = model.getScoreMappingConf().getMapping();
		Set<Double> actualThresholds = findKeysByValue(mapping, 50D);
		Set<Double> actualMaximalValues = findKeysByValue(mapping, 100D);
		Assert.assertTrue(findKeysByValue(mapping, 100D).contains(100D));
		Assert.assertTrue(findKeysByValue(mapping, 0D).contains(0D));
		Assert.assertTrue(String.format("expected: %f, actual: %s", expectedThreshold, actualThresholds),
				actualThresholds.contains(expectedThreshold));
		Assert.assertTrue(String.format("expected: %f, actual: %s", expectedMaximalValue, actualMaximalValues),
				actualMaximalValues.contains(expectedMaximalValue));
	}

	private void assertModel(double expectedThreshold, SMARTScoreMappingModel model) {
		assertModel(expectedThreshold, 100D, model);
	}

	private Set<Double> findKeysByValue(Map<Double, Double> mapping, double value) {
		return mapping.entrySet().stream()
				.filter(entry -> entry.getValue() == value)
				.mapToDouble(Map.Entry::getKey)
				.boxed()
				.collect(Collectors.toSet());
	}

	@Test
	public void shouldBuildModelCorrectlyWhenGivenDataOfOneDay() {
		Double[] scores = {90D, 95D, 99D};
		SMARTScoreMappingModel model = buildModel(0, 0, 0, 0, 0, 0, scores);

		assertModel(90D + SMARTScoreMappingModelBuilder.EPSILON, 99D, model);
	}

	@Test
	public void shouldBuildModelCorrectlyWhenGivenDataOfTwoDays() {
		Double[] scores1 = {20D, 30D, 40D, 90D, 95D, 99D};
		Double[] scores2 = {20D, 30D, 40D, 80D, 95D, 97D};
		SMARTScoreMappingModel model = buildModel(0, 0, 0, 0, 0, 0, scores1, scores2);

		assertModel(80D + SMARTScoreMappingModelBuilder.EPSILON, 99D, model);
	}

	@Test
	public void shouldIgnoreWeekendAndNoisiestDaysOfWeakWhenCalculatingThreshold() {
		Double[] typicalDayScores = ArrayUtils.toObject(IntStream.range(0, 7).mapToDouble(i -> 90D).toArray());
		Double[] noisiestDayScores = ArrayUtils.toObject(IntStream.range(0, 7).mapToDouble(i -> 99D).toArray());
		Double[] weekendScores = ArrayUtils.toObject(IntStream.range(0, 7).mapToDouble(i -> 50D).toArray());
		Double[][] dailyScores = {
				typicalDayScores,
				typicalDayScores,
				noisiestDayScores,
				typicalDayScores,
				typicalDayScores,
				weekendScores,
				weekendScores
		};

		SMARTScoreMappingModel modelWithOutliers = buildModel(
				0,
				0,
				0,
				0,
				0,
				0,
				dailyScores
		);
		assertModel(99D + SMARTScoreMappingModelBuilder.EPSILON, modelWithOutliers);

		SMARTScoreMappingModel modelWithoutOutliers = buildModel(
				0,
				0,
				0,
				0,
				2.0 / 7,
				1.0 / 7,
				dailyScores
		);
		assertModel(90D + SMARTScoreMappingModelBuilder.EPSILON, modelWithoutOutliers);
	}

	@Test
	public void shouldBuildModelCorrectlyWhenGivenEmptyDataOfOneDay() {
		double defaultThreshold = 30;
		double defaultMaximalScore = 80;
		SMARTScoreMappingModel model = buildModel(defaultThreshold, defaultMaximalScore, 0, 0, 0, 0, new Double[]{});

		assertModel(defaultThreshold, defaultMaximalScore, model);
	}

	@Test
	public void shouldBuildModelCorrectlyWhenGivenDataOfOneDayAndEmptyDataOfAnotherDay() {
		Double[] scores1 = {20D, 30D, 40D, 90D, 95D, 99D};
		Double[] scores2 = {};
		SMARTScoreMappingModel model = buildModel(0, 0, 0, 0, 0, 0, scores1, scores2);

		assertModel(90D + SMARTScoreMappingModelBuilder.EPSILON, 99D, model);
	}

	@Test
	public void shouldBuildModelCorrectlyWhenGivenDataOfOneDayAndPartialDataOfAnotherDay() {
		Double[] scores1 = {20D, 30D, 40D, 90D, 95D, 99D};
		Double[] scores2 = {97D};
		SMARTScoreMappingModel model = buildModel(0, 0, 0, 0, 0, 0, scores1, scores2);

		assertModel(90D + SMARTScoreMappingModelBuilder.EPSILON, 99D, model);
	}

	@Test
	public void shouldNotCreateThresholdLowerThanMinThreshold() {
		double minThreshold = 95;
		Double[] scores = {90D, 95D, 99D};
		SMARTScoreMappingModel model = buildModel(0, 0, minThreshold, minThreshold, 0, 0, scores);

		assertModel(minThreshold, 99D, model);
	}

	@Test
	public void shouldNotCreateMaximalScoreLowerThanMinMaximalScore() {
		double minMaximalScore = 50;
		Double[] scores = {10D, 25D, 30D};
		SMARTScoreMappingModel model = buildModel(0, 0, 0, minMaximalScore, 0, 0, scores);

		assertModel(10D + SMARTScoreMappingModelBuilder.EPSILON, minMaximalScore, model);
	}
}
