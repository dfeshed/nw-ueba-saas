package fortscale.ml.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class ContinuousDataModel implements Model {
	private long N; // population size
	private double mean; // average
	private double sd; // standard deviation
	private double maxValue; // the maximal value the model encountered

	/**
	 * ContinuousDataModel constructor.
	 */
	public ContinuousDataModel() {
		N = 0;
		mean = 0;
		sd = 0;
		maxValue = 0;
	}

	/**
	 * Sets new values to the model's parameters.
	 *
	 * @param N			new population size.
	 * @param mean		new mean.
	 * @param sd		new standard deviation.
	 * @param maxValue	new maximal value.
	 */
	public void setParameters(long N, double mean, double sd, double maxValue) {
		this.N = N;
		this.mean = mean;
		this.sd = sd;
		this.maxValue = maxValue;
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
		return o.N == N && o.mean == mean && o.sd == sd && o.maxValue == maxValue;
	}

	@Override
	public String toString() {
		return String.format("<ContinuousDataModel: N=%d, mean=%f, sd=%f, maxValue=%f>", N, mean, sd, maxValue);
	}

	@Override
	public long getNumOfSamples() {
		return N;
	}

	public long getN() {
		return N;
	}

	public double getMean() {
		return mean;
	}

	public double getSd() {
		return sd;
	}

	public double getMaxValue() {
		return maxValue;
	}
}
