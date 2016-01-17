package fortscale.ml.model.builder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.Assert;

public class TimeModelBuilderConf implements IModelBuilderConf {
	public static final String TIME_MODEL_BUILDER = "time_model_builder";

	private int timeResolution;
	private int bucketSize;
	private int minEvents;
	private int maxRareTimestampCount;
	private int maxNumOfRareTimestamps;

	@JsonCreator
	public TimeModelBuilderConf(
			@JsonProperty("timeResolution") int timeResolution,
			@JsonProperty("bucketSize") int bucketSize,
			@JsonProperty("minEvents") int minEvents,
			@JsonProperty("maxRareTimestampCount") int maxRareTimestampCount,
			@JsonProperty("maxNumOfRareTimestamps") int maxNumOfRareTimestamps) {

		Assert.isTrue(timeResolution > 0);
		Assert.isTrue(bucketSize > 0);
		Assert.isTrue(minEvents > 0);
		Assert.isTrue(maxRareTimestampCount > 0);
		Assert.isTrue(maxNumOfRareTimestamps > 0);

		this.timeResolution = timeResolution;
		this.bucketSize = bucketSize;
		this.minEvents = minEvents;
		this.maxRareTimestampCount = maxRareTimestampCount;
		this.maxNumOfRareTimestamps = maxNumOfRareTimestamps;
	}

	@Override
	public String getFactoryName() {
		return TIME_MODEL_BUILDER;
	}

	public int getTimeResolution() {
		return timeResolution;
	}

	public int getBucketSize() {
		return bucketSize;
	}

	public int getMinEvents() {
		return minEvents;
	}

	public int getMaxRareTimestampCount() {
		return maxRareTimestampCount;
	}

	public int getMaxNumOfRareTimestamps() {
		return maxNumOfRareTimestamps;
	}
}
