package fortscale.ml.model.builder;

import fortscale.ml.model.SMARTThresholdModel;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class SMARTThresholdModelBuilderTest {
	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenNullAsInput() {
		new SMARTValuesModelBuilder().build(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailGivenIllegalInputType() {
		new SMARTValuesModelBuilder().build("");
	}

	@Test
	public void shouldBuildModelCorrectlyWhenGivenDataOfOneDay() {
		Map<Long, List<Double>> dateToHighestScores = new HashMap<>();

		long yesterday = 0L;
		List<Double> scores = Arrays.asList(90D, 95D, 99D);
		dateToHighestScores.put(yesterday, scores);

		SMARTThresholdModel newModel =
				new SMARTThresholdModelBuilder(new SMARTThresholdModelBuilderConf(0)).build(dateToHighestScores);

		Assert.assertEquals(50D, newModel.getScoreMappingConf().getMapping().get(
				scores.get(0) + SMARTThresholdModelBuilder.EPSILON), 0.0001);
		Assert.assertEquals(100D, newModel.getScoreMappingConf().getMapping().get(
				scores.get(scores.size() - 1)), 0.0001);
	}

	@Test
	public void shouldBuildModelCorrectlyWhenGivenDataOfTwoDays() {
		Map<Long, List<Double>> dateToHighestScores = new HashMap<>();

		long twoDaysAgo = 0L;
		List<Double> scores1 = Arrays.asList(90D, 95D, 99D);
		dateToHighestScores.put(twoDaysAgo, scores1);

		long yesterday = 1L;
		List<Double> scores2 = Arrays.asList(80D, 95D, 97D);
		dateToHighestScores.put(yesterday, scores2);

		SMARTThresholdModel newModel =
				new SMARTThresholdModelBuilder(new SMARTThresholdModelBuilderConf(0)).build(dateToHighestScores);

		Assert.assertEquals(50D, newModel.getScoreMappingConf().getMapping().get(
				Math.min(scores1.get(0), scores2.get(0)) + SMARTThresholdModelBuilder.EPSILON), 0.0001);
		Assert.assertEquals(100D, newModel.getScoreMappingConf().getMapping().get(
				Math.max(scores1.get(scores1.size() - 1), scores2.get(scores2.size() - 1))), 0.0001);
	}

	@Test
	public void shouldBuildModelCorrectlyWhenGivenEmptyDataOfOneDay() {
		Map<Long, List<Double>> dateToHighestScores = new HashMap<>();

		long yesterday = 0L;
		dateToHighestScores.put(yesterday, Collections.emptyList());

		SMARTThresholdModel newModel =
				new SMARTThresholdModelBuilder(new SMARTThresholdModelBuilderConf(0)).build(dateToHighestScores);

		Map<Double, Double> mapping = newModel.getScoreMappingConf().getMapping();
		Assert.assertEquals(3, mapping.size());
		Assert.assertEquals(0, mapping.get(0D), 0.0001);
		Assert.assertEquals(50, mapping.get(50D), 0.0001);
		Assert.assertEquals(100, mapping.get(100D), 0.0001);
	}

	@Test
	public void shouldBuildModelCorrectlyWhenGivenDataOfOneDayAndEmptyDataOfAnotherDay() {
		Map<Long, List<Double>> dateToHighestScores = new HashMap<>();

		long twoDaysAgo = 0L;
		List<Double> scores = Arrays.asList(90D, 95D, 99D);
		dateToHighestScores.put(twoDaysAgo, scores);

		long yesterday = 1L;
		dateToHighestScores.put(yesterday, Collections.emptyList());

		SMARTThresholdModel newModel =
				new SMARTThresholdModelBuilder(new SMARTThresholdModelBuilderConf(0)).build(dateToHighestScores);

		Assert.assertEquals(50D, newModel.getScoreMappingConf().getMapping().get(
				scores.get(0) + SMARTThresholdModelBuilder.EPSILON), 0.0001);
		Assert.assertEquals(100D, newModel.getScoreMappingConf().getMapping().get(
				scores.get(scores.size() - 1)), 0.0001);
	}

	@Test
	public void shouldNotCreateThresholdLowerThanMinThreshold() {
		Map<Long, List<Double>> dateToHighestScores = new HashMap<>();

		long yesterday = 0L;
		List<Double> scores = Arrays.asList(90D, 95D, 99D);
		dateToHighestScores.put(yesterday, scores);

		SMARTThresholdModel newModel =
				new SMARTThresholdModelBuilder(new SMARTThresholdModelBuilderConf(95)).build(dateToHighestScores);

		Assert.assertEquals(50D, newModel.getScoreMappingConf().getMapping().get(95D), 0.0001);
		Assert.assertEquals(100D, newModel.getScoreMappingConf().getMapping().get(
				scores.get(scores.size() - 1)), 0.0001);
	}
}
