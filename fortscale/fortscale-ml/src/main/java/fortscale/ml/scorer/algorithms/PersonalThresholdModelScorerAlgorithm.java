package fortscale.ml.scorer.algorithms;

import fortscale.ml.model.PersonalThresholdModel;
import org.springframework.util.Assert;

public class PersonalThresholdModelScorerAlgorithm {
	public static double calculateScore(double highScoreProbability,
										int numOfSamples,
										PersonalThresholdModel model,
										double maxRatioFromUniformThreshold) {
		Assert.isTrue(highScoreProbability >= 0 && highScoreProbability <= 1);
		Assert.isTrue(numOfSamples > 0);
		Assert.notNull(model);
		Assert.isTrue(maxRatioFromUniformThreshold > 0);
		double power = Math.log(0.5) / Math.log(model.calcThreshold(numOfSamples, maxRatioFromUniformThreshold));
		return 100 * Math.pow(highScoreProbability, power);
	}
}
