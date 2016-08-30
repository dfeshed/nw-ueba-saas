package fortscale.ml.model.builder.gaussian.prior;

import fortscale.ml.model.ContinuousDataModel;
import org.springframework.util.Assert;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This PriorBuilder gives a standard deviation prior such that encountering values below some value V will be reasonable.
 * More precisely, it inspects the maximal value of all the models, peeks the one of the specified quantile (which
 * is specified in the ctor), and decides that this value V shall be a reasonable value to encounter, i.e. Pr(V) = 0.95.
 * This way, it can ensure that models with not enough data (such that the prior will dictate the score) will get low
 * scores for values up until V.
 */
public class PriorBuilderMaxAllowedValue implements PriorBuilder {
	private double quantile;
	private int minQuantileComplementSize;
	private Double minAllowedDistFromMean;

	public PriorBuilderMaxAllowedValue(double quantile, int minQuantileComplementSize, Double minAllowedDistFromMean) {
		Assert.isTrue(quantile >= 0 && quantile <= 1);
		Assert.isTrue(minQuantileComplementSize >= 0);
		this.quantile = quantile;
		this.minQuantileComplementSize = minQuantileComplementSize;
		this.minAllowedDistFromMean = minAllowedDistFromMean;
	}

	@Override
	public Double calcPrior(List<ContinuousDataModel> models, double mean) {
		if (models.isEmpty()) {
			return null;
		}

		int quantileIndex = Math.min(Math.max(0, models.size() - 1 - minQuantileComplementSize),
				(int) Math.floor((models.size() - 1) * this.quantile));
		double maxValueOverModels = models.stream()
				.sorted(Comparator.comparing(ContinuousDataModel::getMaxValue))
				.collect(Collectors.toList())
				.get(quantileIndex)
				.getMaxValue();

		if (minAllowedDistFromMean != null) {
			maxValueOverModels = Math.max(maxValueOverModels, mean + minAllowedDistFromMean);
		}
		// for normal distribution, getting a value that is at most two standard
 		// deviations from the mean has ~0.95 probability, so return std such that
 		// maxValueOverModels will get 0.95 probability
		return (maxValueOverModels - mean) / 2.0;
	}
}
