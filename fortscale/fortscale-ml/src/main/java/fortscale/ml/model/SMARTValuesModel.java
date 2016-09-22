package fortscale.ml.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(
		fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE,
		setterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE)
public class SMARTValuesModel implements Model {
	private long numOfZeroValues;
	private long numOfPositiveValues;
	private double sumOfValues;

	public void init(long numOfZeroValues, long numOfPositiveValues, double sumOfValues) {
		this.numOfZeroValues = numOfZeroValues;
		this.numOfPositiveValues = numOfPositiveValues;
		this.sumOfValues = sumOfValues;
	}

	@Override
	public long getNumOfSamples() {
		return numOfZeroValues + numOfPositiveValues;
	}

	public long getNumOfPositiveValues() {
		return numOfPositiveValues;
	}

	public double getSumOfValues() {
		return sumOfValues;
	}
}
