package fortscale.ml.model.prevalance.field;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import fortscale.ml.model.Model;
import org.apache.commons.math3.distribution.TDistribution;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class ContinuousDataModel implements Model {
	public static final int SEPARATOR_BETWEEN_SMALL_AND_LARGE_VALUE_DENSITY = 1;

	private long N; // population size
	private double mean; // average
	private double sd; // standard deviation

	/**
	 * ContinuousDataModel constructor.
	 */
	public ContinuousDataModel() {
		N = 0;
		mean = 0;
		sd = 0;
	}

	/**
	 * Sets new values to the model's parameters.
	 *
	 * @param N    new population size.
	 * @param mean new mean.
	 * @param sd   new standard deviation.
	 */
	public void setParameters(long N, double mean, double sd) {
		this.N = N;
		this.mean = mean;
		this.sd = sd;
	}

	/**
	 * Scores a given value according to the model.
	 *
	 * @param value the value to score.
	 * @return the score.
	 */
	public double calculateScore(double value) {
		if (sd == 0)
			return 0;

		double z = (value - mean) / sd;
		TDistribution tDistribution = new TDistribution(N - 1);

		return z > 0 ?
			tDistribution.density(z) + SEPARATOR_BETWEEN_SMALL_AND_LARGE_VALUE_DENSITY :
			-1 * tDistribution.density(z) - SEPARATOR_BETWEEN_SMALL_AND_LARGE_VALUE_DENSITY;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ContinuousDataModel)) {
			return false;
		}
		ContinuousDataModel o = (ContinuousDataModel) obj;
		if (N == 0) {
			return o.N == 0;
		}
		return o.N == N && o.mean == mean && o.sd == sd;
	}

	@Override
	public String toString() {
		return String.format("<ContinuousDataModel: N=%s, mean=%s, sd=%s>", N, mean, sd);
	}
}
