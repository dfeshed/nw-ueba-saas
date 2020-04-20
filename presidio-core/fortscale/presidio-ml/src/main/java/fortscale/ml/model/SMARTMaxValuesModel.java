package fortscale.ml.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import java.time.Instant;
import java.util.Map;

@JsonAutoDetect(
		fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE,
		setterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE)
public class SMARTMaxValuesModel implements PartitionedDataModel {
	private Map<Long, Double> startInstantToMaxSmartValue;
	private Long numOfPartitions;
	private Instant weightsModelEndTime;

	public void init(Map<Long, Double> startInstantToMaxSmartValue, long numOfPartitions, Instant weightsModelEndTime) {
		this.startInstantToMaxSmartValue = startInstantToMaxSmartValue;
		this.numOfPartitions = numOfPartitions;
		this.weightsModelEndTime = weightsModelEndTime;
	}

	public Map<Long, Double> getStartInstantToMaxSmartValue() {
		return startInstantToMaxSmartValue;
	}

	public void setStartInstantToMaxSmartValue(Map<Long, Double> startInstantToMaxSmartValue) {
		this.startInstantToMaxSmartValue = startInstantToMaxSmartValue;
	}

	public void setWeightsModelEndTime(Instant weightsModelEndTime) {
		this.weightsModelEndTime = weightsModelEndTime;
	}

	@Override
	public long getNumOfSamples() {
		return startInstantToMaxSmartValue.size();
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
