package fortscale.ml.model;


import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

//todo: add  numOfPartitions to ContinuousDataModel and remove PartitionedContinuousDataModel class and PartitionedContinuousHistogramModelBuilder class
public class PartitionedContinuousDataModel extends ContinuousDataModel implements PartitionedDataModel {
	private Long numOfPartitions;

	public PartitionedContinuousDataModel() {
		super();
		numOfPartitions = 0L;
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
	public PartitionedContinuousDataModel setParameters(long N, double mean, double sd, double maxValue, long numOfPartitions) {
		setParameters(N,mean,sd,maxValue);
		this.numOfPartitions = numOfPartitions;
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof PartitionedContinuousDataModel)) return false;
		PartitionedContinuousDataModel o = (PartitionedContinuousDataModel)obj;
		return (equals(obj)) &&  o.numOfPartitions.equals(numOfPartitions);
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + numOfPartitions.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

	@Override
	public long getNumOfPartitions() {
		return numOfPartitions;
	}
}
