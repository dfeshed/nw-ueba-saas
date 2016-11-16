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

	public double calculateScore(double value, ContinuousDataModel model, GaussianPriorModel priorModel) {
		Assert.notNull(model);
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
		double degreesOfFreedom = calcDegreesOfFreedom(model, priorModel, globalInfluence);
		if (degreesOfFreedom <= 0) {
			// TDistribution can't handle non-positive degrees of freedom
			return 0;
		}
		Double priorSd = calcPriorSd(priorModel, model);
		double posteriorSd = calcPosteriorSd(model, priorSd, globalInfluence);
		double tScore = (value - model.getMean()) / Math.max(0.00000001, posteriorSd);
		return new TDistribution(degreesOfFreedom).cumulativeProbability(tScore);
	}

	private static Double calcPriorSd(GaussianPriorModel priorModel, ContinuousDataModel model) {
		if (priorModel == null) {
			return null;
		}
		Double prior = priorModel.getPrior(model.getMean());
		if (prior == null) {
			prior = priorModel.getMinPrior();
		}
		return prior;
	}

	private static double calcPosteriorSd(ContinuousDataModel model,
										  Double priorSd,
										  int globalInfluence) {
		if (priorSd == null) {
			return model.getSd();
		}
		return Math.sqrt((globalInfluence * priorSd * priorSd + model.getN() * model.getSd() * model.getSd()) /
				(globalInfluence + model.getN()));
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
