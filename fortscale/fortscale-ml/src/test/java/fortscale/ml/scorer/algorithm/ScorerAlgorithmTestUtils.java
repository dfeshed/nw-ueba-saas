package fortscale.ml.scorer.algorithm;

import org.junit.Assert;

import java.util.List;
import java.util.stream.IntStream;


public class ScorerAlgorithmTestUtils {
	public static void assertScoresIncrease(List<Double> scores) {
		assertScoresMonotonicity(scores, true);
	}

	public static void assertScoresDecrease(List<Double> scores) {
		assertScoresMonotonicity(scores, false);
	}

	private static void assertScoresMonotonicity(List<Double> scores, boolean isIncreasing) {
		int sign = isIncreasing ? 1 : -1;
		Assert.assertTrue(IntStream.range(0, scores.size() - 1)
				.allMatch(i -> sign * scores.get(i) <= sign * scores.get(i + 1)));
		Assert.assertTrue(sign * scores.get(0) < sign * scores.get(scores.size() - 1));
	}
}
