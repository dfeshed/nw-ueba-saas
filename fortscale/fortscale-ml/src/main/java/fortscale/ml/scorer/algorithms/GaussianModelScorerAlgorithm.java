package fortscale.ml.scorer.algorithms;

import fortscale.ml.model.ContinuousDataModel;
import fortscale.ml.model.GaussianPriorModel;
import org.apache.commons.math3.distribution.TDistribution;
import org.springframework.util.Assert;

public class GaussianModelScorerAlgorithm {
	public static double calculate(ContinuousDataModel model,
								   GaussianPriorModel priorModel,
								   int globalInfluence,
								   double value) {
		Assert.notNull(model);
		if (model.getN() <= 1) {
			// TDistribution can't handle less than two samples
			return 0;
		}
		double tScore = (value - model.getMean()) / (calcSd(model, priorModel, globalInfluence) + 0.00000001);
		double probOfGettingLessThanValue = new TDistribution(calcDegreesOfFreedom(model, priorModel, globalInfluence))
				.cumulativeProbability(tScore);
		return Math.max(0, 100 * (2 * probOfGettingLessThanValue - 1));
	}

	private static double calcSd(ContinuousDataModel model,
								 GaussianPriorModel priorModel,
								 int globalInfluence) {
		if (priorModel == null) {
			return model.getSd();
		}
		return (globalInfluence * priorModel.getPrior(model.getMean()) + model.getSd() * model.getN()) /
				(globalInfluence + model.getN());
	}

	private static double calcDegreesOfFreedom(ContinuousDataModel model,
											   GaussianPriorModel priorModel,
											   int globalInfluence) {
		double df = model.getN() - 1;
		if (priorModel != null) {
			df += globalInfluence;
		}
		return df;
	}
}
