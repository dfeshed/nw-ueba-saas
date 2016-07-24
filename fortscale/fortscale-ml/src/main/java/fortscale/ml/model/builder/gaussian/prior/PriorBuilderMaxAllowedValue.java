package fortscale.ml.model.builder.gaussian.prior;

import fortscale.ml.model.ContinuousDataModel;
import org.springframework.util.Assert;

import java.util.List;
import java.util.PriorityQueue;

/**
 * This PriorBuilder gives a standard deviation prior such that encountering values below some value V will be reasonable.
 * More precisely, it inspects the maximal value of all the models, peeks the one of the specified quantile (which
 * is specified in the ctor), and decides that this value V shall be a reasonable value to encounter, i.e. Pr(V) = 0.95.
 * This way, it can ensure that models with not enough data (such that the prior will dictate the score) will get low
 * scores for values up until V.
 */
public class PriorBuilderMaxAllowedValue implements PriorBuilder {
	private double quantile;

	public PriorBuilderMaxAllowedValue(double quantile) {
		Assert.isTrue(quantile >= 0 && quantile <= 1);
		this.quantile = quantile;
	}

	@Override
	public Double calcPrior(List<ContinuousDataModel> models, double mean) {
		if (models.isEmpty()) {
			return null;
		}

		PriorityQueue<Double> queue = new PriorityQueue<>();
		models.stream()
				.mapToDouble(ContinuousDataModel::getMaxValue)
				.forEach(queue::add);

		double maxValueOverModels = 0;
		for (int i = 0; i <= (models.size() - 1) * quantile; i++) {
			maxValueOverModels = queue.poll();
		}
		// for normal distribution, getting a value that is at most two standard
 		// deviations from the mean has ~0.95 probability, so return std such that
 		// maxValueOverModels will get 0.95 probability
		return (maxValueOverModels - mean) / 2.0;
	}
}
