package fortscale.ml.model.builder;

import fortscale.ml.model.SMARTScoreMappingModel;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class SMARTScoreMappingModelBuilderTest {
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

		SMARTScoreMappingModel newModel =
				new SMARTScoreMappingModelBuilder(new SMARTScoreMappingModelBuilderConf(0D, 0D, 0D, 0D)).build(dateToHighestScores);

		Assert.assertEquals(50D, newModel.getScoreMappingConf().getMapping().get(
				scores.get(0) + SMARTScoreMappingModelBuilder.EPSILON), 0.0001);
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

		SMARTScoreMappingModel newModel =
				new SMARTScoreMappingModelBuilder(new SMARTScoreMappingModelBuilderConf(0D, 0D, 0D, 0D)).build(dateToHighestScores);

		Assert.assertEquals(50D, newModel.getScoreMappingConf().getMapping().get(
				(scores1.get(0) + scores2.get(0)) / 2 + SMARTScoreMappingModelBuilder.EPSILON), 0.0001);
		Assert.assertEquals(100D, newModel.getScoreMappingConf().getMapping().get(
				Math.max(scores1.get(scores1.size() - 1), scores2.get(scores2.size() - 1))), 0.0001);
	}

	@Test
	public void shouldBuildModelCorrectlyWhenGivenEmptyDataOfOneDay() {
		Map<Long, List<Double>> dateToHighestScores = new HashMap<>();

		long yesterday = 0L;
		dateToHighestScores.put(yesterday, Collections.emptyList());

		double defaultThreshold = 10;
		double defaultMaximalScore = 20;
		SMARTScoreMappingModelBuilderConf conf = new SMARTScoreMappingModelBuilderConf(defaultThreshold, defaultMaximalScore, 0D, 0D);
		SMARTScoreMappingModel newModel = new SMARTScoreMappingModelBuilder(conf).build(dateToHighestScores);

		Map<Double, Double> mapping = newModel.getScoreMappingConf().getMapping();
		Assert.assertEquals(50, mapping.get(defaultThreshold), 0.0001);
		Assert.assertEquals(100, mapping.get(defaultMaximalScore), 0.0001);
	}

	@Test
	public void shouldBuildModelCorrectlyWhenGivenDataOfOneDayAndEmptyDataOfAnotherDay() {
		Map<Long, List<Double>> dateToHighestScores = new HashMap<>();

		long twoDaysAgo = 0L;
		List<Double> scores = Arrays.asList(90D, 95D, 99D);
		dateToHighestScores.put(twoDaysAgo, scores);

		long yesterday = 1L;
		dateToHighestScores.put(yesterday, Collections.emptyList());

		SMARTScoreMappingModel newModel =
				new SMARTScoreMappingModelBuilder(new SMARTScoreMappingModelBuilderConf(0D, 0D, 0D, 0D)).build(dateToHighestScores);

		Assert.assertEquals(50D, newModel.getScoreMappingConf().getMapping().get(
				scores.get(0) + SMARTScoreMappingModelBuilder.EPSILON), 0.0001);
		Assert.assertEquals(100D, newModel.getScoreMappingConf().getMapping().get(
				scores.get(scores.size() - 1)), 0.0001);
	}

	@Test
	public void shouldNotCreateThresholdLowerThanMinThreshold() {
		Map<Long, List<Double>> dateToHighestScores = new HashMap<>();

		long yesterday = 0L;
		List<Double> scores = Arrays.asList(90D, 95D, 99D);
		dateToHighestScores.put(yesterday, scores);

		double minThreshold = 95;
		SMARTScoreMappingModel newModel =
				new SMARTScoreMappingModelBuilder(new SMARTScoreMappingModelBuilderConf(0D, 0D, minThreshold, minThreshold))
						.build(dateToHighestScores);

		Assert.assertEquals(50D, newModel.getScoreMappingConf().getMapping().get(minThreshold), 0.0001);
		Assert.assertEquals(100D, newModel.getScoreMappingConf().getMapping().get(
				scores.get(scores.size() - 1)), 0.0001);
	}

	@Test
	public void shouldNotCreateMaximalScoreLowerThanMinMaximalScore() {
		Map<Long, List<Double>> dateToHighestScores = new HashMap<>();

		long yesterday = 0L;
		List<Double> scores = Arrays.asList(10D, 25D, 30D);
		dateToHighestScores.put(yesterday, scores);

		double minMaximalScore = 50;
		SMARTScoreMappingModel newModel =
				new SMARTScoreMappingModelBuilder(new SMARTScoreMappingModelBuilderConf(0D, 0D, 0D, minMaximalScore)).build(dateToHighestScores);

		Assert.assertEquals(50D, newModel.getScoreMappingConf().getMapping().get(
				scores.get(0) + SMARTScoreMappingModelBuilder.EPSILON), 0.0001);
		Assert.assertEquals(100D, newModel.getScoreMappingConf().getMapping().get(minMaximalScore), 0.0001);
	}
}
