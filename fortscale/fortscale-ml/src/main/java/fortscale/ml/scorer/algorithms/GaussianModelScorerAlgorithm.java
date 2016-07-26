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
		Double prior = calcPrior(priorModel, model);
		globalInfluence = calcGlobalInfluence(globalInfluence, prior);
		double posterior = calcPosterior(model, prior, globalInfluence);
		double tScore = (value - model.getMean()) / (posterior + 0.00000001);
		double degreesOfFreedom = calcDegreesOfFreedom(model, priorModel, globalInfluence);
		double probOfGettingLessThanValue = new TDistribution(degreesOfFreedom).cumulativeProbability(tScore);
		return Math.max(0, 100 * (2 * probOfGettingLessThanValue - 1));
	}

	private static int calcGlobalInfluence(int globalInfluence, Double prior) {
		if (prior == null) {
			return 0;
		}
		return globalInfluence;
	}

	private static Double calcPrior(GaussianPriorModel priorModel, ContinuousDataModel model) {
		if (priorModel == null) {
			return null;
		}
		return priorModel.getPrior(model.getMean());
	}

	private static double calcPosterior(ContinuousDataModel model,
										Double prior,
										int globalInfluence) {
		if (prior == null) {
			return model.getSd();
		}
		return (globalInfluence * prior + model.getSd() * model.getN()) /
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
