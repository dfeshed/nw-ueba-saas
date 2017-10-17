package fortscale.ml.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(
		fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE,
		setterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE)
public class ContinuousMaxDataModel implements IContinuousDataModel{
	private long N; // population size
	private double mean; // average
	private double continuousMaxSd; // standard deviation of max
	private double continuousSd; // standard deviation of all the records
	private double maxValue; // the maximal value the model encountered

	/**
	 * ContinuousDataModel constructor.
	 */
	public ContinuousMaxDataModel() {
		N = 0;
		mean = 0;
		continuousMaxSd = 0;
		continuousSd = 0;
		maxValue = 0;
	}

	public ContinuousMaxDataModel(long N, double mean, double continuousMaxSd, double continuousSd, double maxValue) {
		this.N = N;
		this.mean = mean;
		this.continuousMaxSd = continuousMaxSd;
		this.continuousSd = continuousSd;
		this.maxValue = maxValue;
	}

	/**
	 * Sets new values to the model's parameters.
	 *
	 * @param N			new population size.
	 * @param mean		new mean.
	 * @param continuousMaxSd		new standard deviation.
	 * @param continuousSd		new standard deviation.
	 * @param maxValue	new maximal value.
	 * @return			this (for chaining).
	 */
	public ContinuousMaxDataModel setParameters(long N, double mean, double continuousMaxSd, double continuousSd, double maxValue) {
		this.N = N;
		this.mean = mean;
		this.continuousMaxSd = continuousMaxSd;
		this.continuousSd = continuousSd;
		this.maxValue = maxValue;
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ContinuousMaxDataModel)) {
			return false;
		}
		ContinuousMaxDataModel o = (ContinuousMaxDataModel) obj;
		if (N == 0) {
			return o.N == 0;
		}
		return o.N == N && o.mean == mean && o.getSd() == getSd() && o.maxValue == maxValue;
	}

	@Override
	public String toString() {
		return String.format("<ContinuousDataModel: N=%d, mean=%f, sd=%f, maxValue=%f>", N, mean, getSd(), maxValue);
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
		return Math.max(continuousMaxSd, continuousSd);
	}

	@Override
	public double getMaxValue() {
		return maxValue;
	}
}
