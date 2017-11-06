package fortscale.ml.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import java.time.Instant;

@JsonAutoDetect(
		fieldVisibility = Visibility.ANY,
		getterVisibility = Visibility.NONE,
		setterVisibility = Visibility.NONE,
		isGetterVisibility = Visibility.NONE
)
public class SMARTValuesPriorModel implements Model {
	private double prior;
	private long numOfPartitions;
	private Instant weightsModelEndTime;

	public SMARTValuesPriorModel init(double prior) {
		this.prior = prior;
		return this;
	}

	@Override
	public long getNumOfSamples() {
		//TODO: getNumOfSamples should be mandatory only for models which aren't used as additionalModels
		throw new UnsupportedOperationException();
	}

	public double getPrior() {
		return prior;
	}

	public void setNumOfPartitions(long numOfPartitions) {
		this.numOfPartitions = numOfPartitions;
	}

	public void setWeightsModelEndTime(Instant weightsModelEndTime) {
		this.weightsModelEndTime = weightsModelEndTime;
	}

	public long getNumOfPartitions() {
		return numOfPartitions;
	}
}
