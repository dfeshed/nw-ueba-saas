package fortscale.ml.scorer.algorithms;

import fortscale.ml.model.ContinuousDataModel;
import fortscale.ml.model.GaussianPriorModel;
import org.apache.commons.math3.distribution.TDistribution;
import org.springframework.util.Assert;

import java.util.stream.IntStream;

public class GaussianModelScorerAlgorithm {
	private int globalInfluence;

	public GaussianModelScorerAlgorithm(int globalInfluence) {
		Assert.isTrue(globalInfluence >= 0, String.format("globalInfluence must be >= 0: %d", globalInfluence));
		this.globalInfluence = globalInfluence;
	}

	public double calculate(ContinuousDataModel model, GaussianPriorModel priorModel, double value) {
		Assert.notNull(model);
		if (model.getN() <= 1) {
			// TDistribution can't handle less than two samples
			return 0;
		}
		return IntStream.of(0, globalInfluence)
				.mapToDouble(globalInfluence -> calcProbOfLessThan(model, priorModel, globalInfluence, value))
				.map(probOfLessThanValue -> Math.max(0, 100 * (2 * probOfLessThanValue - 1)))
				.min()
				.getAsDouble();
	}

	private double calcProbOfLessThan(ContinuousDataModel model,
									  GaussianPriorModel priorModel,
									  int globalInfluence,
									  double value) {
		Double prior = calcPrior(priorModel, model);
		double posterior = calcPosterior(model, prior, globalInfluence);
		double tScore = (value - model.getMean()) / Math.max(0.00000001, posterior);
		double degreesOfFreedom = calcDegreesOfFreedom(model, priorModel, globalInfluence);
		return new TDistribution(degreesOfFreedom).cumulativeProbability(tScore);
	}

	private static Double calcPrior(GaussianPriorModel priorModel, ContinuousDataModel model) {
		if (priorModel == null) {
			return null;
		}
		Double prior = priorModel.getPrior(model.getMean());
		if (prior == null) {
			prior = priorModel.getMinPrior();
		}
		return prior;
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
