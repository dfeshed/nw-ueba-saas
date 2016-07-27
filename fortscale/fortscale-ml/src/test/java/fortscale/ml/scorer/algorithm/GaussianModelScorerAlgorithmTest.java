package fortscale.ml.scorer.algorithm;

import fortscale.ml.model.ContinuousDataModel;
import fortscale.ml.model.GaussianPriorModel;
import fortscale.ml.scorer.algorithms.GaussianModelScorerAlgorithm;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class GaussianModelScorerAlgorithmTest {
    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWhenGivenNullAsPriorModel() {
        GaussianModelScorerAlgorithm.calculate(null, new GaussianPriorModel(), 10, 0);
    }

    @Test
    public void shouldScore0WhenLessThanTwoSamples() {
        IntStream.of(0, 1)
                .mapToObj(N -> new ContinuousDataModel().setParameters(N, 0, 0, 0))
                .mapToDouble(model -> GaussianModelScorerAlgorithm.calculate(model, null, 10, 1))
                .forEach(score -> Assert.assertEquals(0, score, 0.0000001));
        Assert.assertTrue(GaussianModelScorerAlgorithm.calculate(new ContinuousDataModel().setParameters(2, 0, 0, 0), null, 10, 1) > 0);
    }

    @Test
    public void shouldScore100WhenSdIsZero() {
        Assert.assertEquals(100, GaussianModelScorerAlgorithm.calculate(new ContinuousDataModel().setParameters(10, 0, 0, 0), null, 10, 1), 0.0000001);
    }

    @Test
    public void shouldScoreZeroWhenGivenValueLessThanOrEqualToTheMean() {
        double mean = 5;
        ContinuousDataModel model = new ContinuousDataModel().setParameters(10, mean, 0, 0);
        IntStream.range(0, (int) mean)
                .mapToDouble(value -> GaussianModelScorerAlgorithm.calculate(model, null, 10, value))
                .forEach(score -> Assert.assertEquals(0, score, 0.0000001));
    }

    @Test
    public void shouldScoreAccordingTo65__95__99dot7_ruleWhenNIsReallyBig() {
        // consult https://en.wikipedia.org/wiki/68%E2%80%9395%E2%80%9399.7_rule for details.
        // When number of samples is really big the TDistribution is approximately gaussian,
        // so we expect to get the scores a gaussian would give
        double sd = 0.00004;
        int N = 100000000;
		double mean = 4.3;
		Assert.assertEquals(68.27, GaussianModelScorerAlgorithm.calculate(new ContinuousDataModel().setParameters(N, mean, sd, 0), null, 10, mean + 1 * sd), 0.01);
        Assert.assertEquals(95.45, GaussianModelScorerAlgorithm.calculate(new ContinuousDataModel().setParameters(N, mean, sd, 0), null, 10, mean + 2 * sd), 0.01);
        Assert.assertEquals(99.73, GaussianModelScorerAlgorithm.calculate(new ContinuousDataModel().setParameters(N, mean, sd, 0), null, 10, mean + 3 * sd), 0.01);
    }

	@Test
	public void shouldScoreAlmostAccordingTo65__95__99dot7_ruleWhenNIsSmall() {
		double sd = 0.00004;
		int N = 10;
		double mean = 4.3;
		Assert.assertEquals(65.65, GaussianModelScorerAlgorithm.calculate(new ContinuousDataModel().setParameters(N, mean, sd, 0), null, 10, mean + 1 * sd), 0.01);
		Assert.assertEquals(92.34, GaussianModelScorerAlgorithm.calculate(new ContinuousDataModel().setParameters(N, mean, sd, 0), null, 10, mean + 2 * sd), 0.01);
		Assert.assertEquals(98.5, GaussianModelScorerAlgorithm.calculate(new ContinuousDataModel().setParameters(N, mean, sd, 0), null, 10, mean + 3 * sd), 0.01);
	}

	private void assertScoresMonotonicity(List<Double> scores, boolean isIncreasing) {
		int sign = isIncreasing ? 1 : -1;
		Assert.assertTrue(IntStream.range(0, scores.size() - 1)
				.allMatch(i -> sign * scores.get(i) <= sign * scores.get(i + 1)));
		Assert.assertTrue(sign * scores.get(0) < sign * scores.get(scores.size() - 1));
	}

	@Test
	public void shouldScoreDecreasinglyAsPriorIncreases() {
		double mean = 10.2;
		double sd = 1.2;
		ContinuousDataModel model = new ContinuousDataModel().setParameters(10, mean, sd, 0);

		List<Double> scores = IntStream.range(0, 100)
				.mapToDouble(prior -> GaussianModelScorerAlgorithm.calculate(
						model,
						new GaussianPriorModel()
								.init(Collections.singletonList(new GaussianPriorModel.SegmentPrior(mean, sd + prior, 0))),
						10,
						mean + sd
				))
				.boxed()
				.collect(Collectors.toList());

		assertScoresMonotonicity(scores, false);
	}

	@Test
	public void shouldIgnorePriorWhenNull() {
		double mean = 10.2;
		double sd = 1.2;
		ContinuousDataModel model = new ContinuousDataModel().setParameters(10, mean, sd, 0);
		double value = mean + 1 * sd;
		int globalInfluence = 10;

		double scoreWithNoPrior = GaussianModelScorerAlgorithm.calculate(model, null, globalInfluence, value);
		double scoreWithPriorResultsInNull = GaussianModelScorerAlgorithm.calculate(model, new GaussianPriorModel(), globalInfluence, value);

		Assert.assertEquals(scoreWithNoPrior, scoreWithPriorResultsInNull, 0.01);
	}

	@Test
	public void shouldIgnorePriorWhenLessThenSdOfModel() {
		double mean = 10.2;
		double sd = 1.2;
		ContinuousDataModel model = new ContinuousDataModel().setParameters(10, mean, sd, 0);
		GaussianPriorModel priorModel = new GaussianPriorModel()
				.init(Collections.singletonList(new GaussianPriorModel.SegmentPrior(mean, sd - 1, 0)));
		double value = mean + 1 * sd;
		int globalInfluence = 10;

		double scoreWithNoPrior = GaussianModelScorerAlgorithm.calculate(model, null, globalInfluence, value);
		double scoreWithPriorLessThanSd = GaussianModelScorerAlgorithm.calculate(model, priorModel, globalInfluence, value);

		Assert.assertEquals(scoreWithNoPrior, scoreWithPriorLessThanSd, 0.00000001);
	}

	@Test
	public void shouldResortToMinimalPriorOverOrganizationWhenNoPriorForGivenModel() {
		double meanOfModel = 10;
		double otherMean1 = 20;
		double otherMean2 = 30;
		double sd = 1.2;
		double priorSmall = sd + 1;
		double priorBig = priorSmall + 1;
		ContinuousDataModel model = new ContinuousDataModel().setParameters(10, meanOfModel, sd, 0);
		int globalInfluence = 10;

		double value = meanOfModel + 1 * sd;
		double scoreForModelWithSmallPrior = GaussianModelScorerAlgorithm.calculate(model, new GaussianPriorModel().init(Arrays.asList(
				new GaussianPriorModel.SegmentPrior(meanOfModel, priorSmall, 0),
				new GaussianPriorModel.SegmentPrior(otherMean1, priorBig, 0)
		)), globalInfluence, value);
		double scoreForModelWithNoPrior = GaussianModelScorerAlgorithm.calculate(model, new GaussianPriorModel().init(Arrays.asList(
				new GaussianPriorModel.SegmentPrior(otherMean2, priorSmall, 0),
				new GaussianPriorModel.SegmentPrior(otherMean1, priorBig, 0)
		)), globalInfluence, value);

		Assert.assertEquals(scoreForModelWithSmallPrior, scoreForModelWithNoPrior, 0.00000001);
	}
}
