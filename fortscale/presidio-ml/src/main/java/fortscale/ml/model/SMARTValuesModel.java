package fortscale.ml.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import java.time.Instant;

@JsonAutoDetect(
		fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE,
		setterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE)
public class SMARTValuesModel implements PartitionedDataModel {
	private long numOfZeroValues;
	private long numOfPositiveValues;
	private double sumOfValues;
	private long numOfPartitions;
	private Instant weightsModelEndTime;

	public void init(long numOfZeroValues, long numOfPositiveValues, double sumOfValues, long numOfPartitions, Instant weightsModelEndTime) {
		this.numOfZeroValues = numOfZeroValues;
		this.numOfPositiveValues = numOfPositiveValues;
		this.sumOfValues = sumOfValues;
		this.numOfPartitions = numOfPartitions;
		this.weightsModelEndTime = weightsModelEndTime;
	}

	@Override
	public String toString() {
		return String.format("<SMARTValuesModel: numOfZeroValues=%d, sumOfValues=%d, sumOfValues=%f>", numOfZeroValues, numOfPositiveValues, sumOfValues);
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

	public long getNumOfZeroValues() {
		return numOfZeroValues;
	}

	@Override
	public long getNumOfPartitions() {
		return numOfPartitions;
	}

	public void setNumOfPartitions(long numOfPartitions) {
		this.numOfPartitions = numOfPartitions;
	}

	public Instant getWeightsModelEndTime() {
		return weightsModelEndTime;
	}
}
