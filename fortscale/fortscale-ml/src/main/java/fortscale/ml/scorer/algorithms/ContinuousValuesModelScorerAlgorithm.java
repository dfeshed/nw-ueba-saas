package fortscale.ml.scorer.algorithms;

import fortscale.ml.model.prevalance.field.ContinuousDataModel;
import org.apache.commons.math3.distribution.TDistribution;

public class ContinuousValuesModelScorerAlgorithm {
	public static final int SEPARATOR_BETWEEN_SMALL_AND_LARGE_VALUE_DENSITY = 1;

	public static double calculate(long N, double mean, double sd, double value) {
		double z = (value - mean) / (sd  + 0.000001);
		TDistribution tDistribution = new TDistribution(N - 1);
		return z > 0 ?
				tDistribution.density(z) + SEPARATOR_BETWEEN_SMALL_AND_LARGE_VALUE_DENSITY :
				-1 * tDistribution.density(z) - SEPARATOR_BETWEEN_SMALL_AND_LARGE_VALUE_DENSITY;
	}

	public static double calculate(ContinuousDataModel model, double value) {
		return calculate(model.getN(), model.getMean(), model.getSd(), value);
	}
}
