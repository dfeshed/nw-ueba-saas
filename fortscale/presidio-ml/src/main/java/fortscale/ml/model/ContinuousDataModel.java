package fortscale.ml.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(
		fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE,
		setterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE)
public class ContinuousDataModel implements IContinuousDataModel {
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
	 * @return			this (for chaining).
	 */
	public ContinuousDataModel setParameters(long N, double mean, double sd, double maxValue) {
		this.N = N;
		this.mean = mean;
		this.sd = sd;
		this.maxValue = maxValue;
		return this;
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

	@Override
	public long getN() {
		return N;
	}

	@Override
	public double getMean() {
		return mean;
	}

	@Override
	public double getSd() {
		return sd;
	}

	@Override
	public double getMaxValue() {
		return maxValue;
	}
}
