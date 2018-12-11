package fortscale.ml.model.retriever;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.minidev.json.JSONObject;
import org.springframework.util.Assert;

import java.util.List;

public class JoinPartitionsHistogramModelsRetrieverConf extends AbstractDataRetrieverConf {
	public static final String JOIN_PARTITIONS_HISTOGRAM_MODELS_RETRIEVER = "join_partitions_histogram_models_retriever";
	public static final int MIN_NUM_OF_MAX_VALUES_SAMPLES = 20;
	public static final long DEFAULT_RESOLUTION = 86400;
	public static final int DEFAULT_RESOLUTION_STEP = 2;
	public static final int NUM_OF_MAX_VALUES_SAMPLES = 90;
	public static final int DEFAULT_MIN_RESOLUTION = 1;
	public static final int PRIOR_MODEL_PAGINATION_PAGE_SIZE = 200000;
	public static final int PRIOR_MODEL_PAGINATION_MAX_GROUP_SIZE = 1000;

	private String modelConfName;
	private String secondaryModelConfName;

	@JsonProperty("numOfMaxValuesSamples")
	private int numOfMaxValuesSamples = NUM_OF_MAX_VALUES_SAMPLES;
	@JsonProperty("minNumOfMaxValuesSamples")
	private int minNumOfMaxValuesSamples = MIN_NUM_OF_MAX_VALUES_SAMPLES;
	@JsonProperty("partitionsResolutionInSeconds")
	private long partitionsResolutionInSeconds = DEFAULT_RESOLUTION;
	@JsonProperty("resolutionStep")
	private int resolutionStep = DEFAULT_RESOLUTION_STEP;
	@JsonProperty("pageSize")
	private int pageSize = PRIOR_MODEL_PAGINATION_PAGE_SIZE;
	@JsonProperty("maxGroupSize")
	private int maxGroupSize = PRIOR_MODEL_PAGINATION_MAX_GROUP_SIZE;
	@JsonProperty("minResolution")
	private int minResolution = DEFAULT_MIN_RESOLUTION;

	@JsonCreator
	public JoinPartitionsHistogramModelsRetrieverConf(
			@JsonProperty("timeRangeInSeconds") long timeRangeInSeconds,
			@JsonProperty("functions") List<JSONObject> functions,
			@JsonProperty("modelConfName") String modelConfName,
			@JsonProperty("secondaryModelConfName") String secondaryModelConfName) {
		super(timeRangeInSeconds, functions);
		Assert.hasText(modelConfName);
		this.modelConfName = modelConfName;
		this.secondaryModelConfName = secondaryModelConfName;
	}

	@Override
	public String getFactoryName() {
		return JOIN_PARTITIONS_HISTOGRAM_MODELS_RETRIEVER;
	}

	public String getModelConfName() {
		return modelConfName;
	}

	public String getSecondaryModelConfName() {
		return secondaryModelConfName;
	}

	public int getNumOfMaxValuesSamples() {
		return numOfMaxValuesSamples;
	}

	public int getMinNumOfMaxValuesSamples() {
		return minNumOfMaxValuesSamples;
	}

	public long getPartitionsResolutionInSeconds() {
		return partitionsResolutionInSeconds;
	}

	public int getResolutionStep() {
		return resolutionStep;
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getMaxGroupSize() {
		return maxGroupSize;
	}

	public int getMinResolution() {
		return minResolution;
	}
}
