package fortscale.ml.scorer.algorithms;

import fortscale.ml.model.ContinuousDataModel;
import org.apache.commons.math3.distribution.TDistribution;

public class ContinuousValuesModelScorerAlgorithm {
	public static final int SEPARATOR_BETWEEN_SMALL_AND_LARGE_VALUE_DENSITY = 1;

	public static double calculateScore(double value, ContinuousDataModel model) {
		if (model.getN() <= 1) {
			return 0;
		}
		double z = (value - model.getMean()) / (model.getSd()  + 0.000001);
		TDistribution tDistribution = new TDistribution(model.getN() - 1);
		return z > 0 ?
				tDistribution.density(z) + SEPARATOR_BETWEEN_SMALL_AND_LARGE_VALUE_DENSITY :
				-1 * tDistribution.density(z) - SEPARATOR_BETWEEN_SMALL_AND_LARGE_VALUE_DENSITY;
	}
}
