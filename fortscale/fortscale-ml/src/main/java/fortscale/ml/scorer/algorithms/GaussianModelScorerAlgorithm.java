package fortscale.ml.scorer.algorithms;

import fortscale.ml.model.ContinuousDataModel;
import fortscale.ml.model.GaussianPriorModel;
import org.apache.commons.math3.distribution.TDistribution;
import org.springframework.util.Assert;

public class GaussianModelScorerAlgorithm {
	public static double calculate(ContinuousDataModel model, GaussianPriorModel priorModel, double value) {
		Assert.notNull(model);
		if (model.getN() <= 1) {
			// TDistribution can't handle less than two samples
			return 0;
		}
		double tScore = (value - model.getMean()) / (model.getSd()  + 0.00000001);
		double probOfGettingLessThanValue = new TDistribution(model.getN() - 1).cumulativeProbability(tScore);
		return Math.max(0, 100 * (2 * probOfGettingLessThanValue - 1));
	}
}
