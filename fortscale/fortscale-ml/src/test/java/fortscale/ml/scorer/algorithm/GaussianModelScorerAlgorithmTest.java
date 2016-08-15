package fortscale.ml.scorer.algorithm;

import fortscale.ml.model.ContinuousDataModel;
import fortscale.ml.model.GaussianPriorModel;
import fortscale.ml.scorer.algorithms.GaussianModelScorerAlgorithm;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class GaussianModelScorerAlgorithmTest {
	private static GaussianModelScorerAlgorithm defaultAlgorithm;

	@BeforeClass
	public static void setup() {
		defaultAlgorithm = new GaussianModelScorerAlgorithm(10);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailWhenGivenNegativeAsGlobalInfluence() {
		new GaussianModelScorerAlgorithm(-1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailWhenGivenNullAsPriorModel() {
		defaultAlgorithm.calculateScore(0, null, new GaussianPriorModel());
	}

    @Test
    public void shouldScore0WhenLessThanTwoSamples() {
        IntStream.of(0, 1)
                .mapToObj(N -> new ContinuousDataModel().setParameters(N, 0, 0, 0))
                .mapToDouble(model -> defaultAlgorithm.calculateScore(1, model, null))
                .forEach(score -> Assert.assertEquals(0, score, 0.0000001));
        Assert.assertTrue(defaultAlgorithm.calculateScore(1, new ContinuousDataModel().setParameters(2, 0, 0, 0), null) > 0);
    }

    @Test
    public void shouldScore100WhenSdIsZero() {
        Assert.assertEquals(1, defaultAlgorithm.calculateScore(1, new ContinuousDataModel().setParameters(10, 0, 0, 0), null), 0.0000001);
    }

    @Test
    public void shouldScoreZeroWhenGivenValueLessThanOrEqualToTheMean() {
        double mean = 5;
        ContinuousDataModel model = new ContinuousDataModel().setParameters(10, mean, 0, 0);
        IntStream.range(0, (int) mean)
                .mapToDouble(value -> defaultAlgorithm.calculateScore(value, model, null))
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
		Assert.assertEquals(0.68, defaultAlgorithm.calculateScore(mean + 1 * sd, new ContinuousDataModel().setParameters(N, mean, sd, 0), null), 0.01);
        Assert.assertEquals(0.95, defaultAlgorithm.calculateScore(mean + 2 * sd, new ContinuousDataModel().setParameters(N, mean, sd, 0), null), 0.01);
        Assert.assertEquals(0.99, defaultAlgorithm.calculateScore(mean + 3 * sd, new ContinuousDataModel().setParameters(N, mean, sd, 0), null), 0.01);
    }

	@Test
	public void shouldScoreAlmostAccordingTo65__95__99dot7_ruleWhenNIsSmall() {
		double sd = 0.00004;
		int N = 10;
		double mean = 4.3;
		Assert.assertEquals(0.65, defaultAlgorithm.calculateScore(mean + 1 * sd, new ContinuousDataModel().setParameters(N, mean, sd, 0), null), 0.01);
		Assert.assertEquals(0.92, defaultAlgorithm.calculateScore(mean + 2 * sd, new ContinuousDataModel().setParameters(N, mean, sd, 0), null), 0.01);
		Assert.assertEquals(0.98, defaultAlgorithm.calculateScore(mean + 3 * sd, new ContinuousDataModel().setParameters(N, mean, sd, 0), null), 0.01);
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
				.mapToDouble(prior -> defaultAlgorithm.calculateScore(
						mean + sd,
						model,
						new GaussianPriorModel()
								.init(Collections.singletonList(new GaussianPriorModel.SegmentPrior(mean, sd + prior, 0)))
				))
				.boxed()
				.collect(Collectors.toList());

		assertScoresMonotonicity(scores, false);
	}

	@Test
	public void shouldScoreDecreasinglyAsGlobalInfluenceIncreases() {
		double mean = 10.2;
		double sd = 1.2;
		ContinuousDataModel model = new ContinuousDataModel().setParameters(10, mean, sd, 0);

		List<Double> scores = IntStream.range(0, 100)
				.mapToDouble(globalInfluence -> new GaussianModelScorerAlgorithm(globalInfluence).calculateScore(
						mean + sd,
						model,
						new GaussianPriorModel()
								.init(Collections.singletonList(new GaussianPriorModel.SegmentPrior(mean, sd + 1, 0)))
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

		double scoreWithNoPrior = defaultAlgorithm.calculateScore(value, model, null);
		double scoreWithPriorResultsInNull = defaultAlgorithm.calculateScore(value, model, new GaussianPriorModel());

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

		double scoreWithNoPrior = defaultAlgorithm.calculateScore(value, model, null);
		double scoreWithPriorLessThanSd = defaultAlgorithm.calculateScore(value, model, priorModel);

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

		double value = meanOfModel + 1 * sd;
		double scoreForModelWithSmallPrior = defaultAlgorithm.calculateScore(value, model, new GaussianPriorModel().init(Arrays.asList(
				new GaussianPriorModel.SegmentPrior(meanOfModel, priorSmall, 0),
				new GaussianPriorModel.SegmentPrior(otherMean1, priorBig, 0)
		)));
		double scoreForModelWithNoPrior = defaultAlgorithm.calculateScore(value, model, new GaussianPriorModel().init(Arrays.asList(
				new GaussianPriorModel.SegmentPrior(otherMean2, priorSmall, 0),
				new GaussianPriorModel.SegmentPrior(otherMean1, priorBig, 0)
		)));

		Assert.assertEquals(scoreForModelWithSmallPrior, scoreForModelWithNoPrior, 0.00000001);
	}
}
