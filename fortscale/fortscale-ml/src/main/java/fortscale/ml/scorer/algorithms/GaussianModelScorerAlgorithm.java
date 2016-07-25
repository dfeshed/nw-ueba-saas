package fortscale.ml.scorer.algorithms;

import fortscale.ml.model.ContinuousDataModel;
import fortscale.ml.model.GaussianPriorModel;
import org.springframework.util.Assert;

public class GaussianModelScorerAlgorithm {
	public static double calculate(ContinuousDataModel model, GaussianPriorModel priorModel, double value) {
		Assert.notNull(model);
		if (model.getN() <= 1) {
			// TDistribution can't handle less than two samples
			return 0;
		}
		return 100;
	}
}
