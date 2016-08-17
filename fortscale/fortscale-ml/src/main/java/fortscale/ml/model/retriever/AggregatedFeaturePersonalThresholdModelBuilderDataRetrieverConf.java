package fortscale.ml.model.retriever;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.minidev.json.JSONObject;
import org.springframework.util.Assert;

import java.util.List;

public class AggregatedFeaturePersonalThresholdModelBuilderDataRetrieverConf extends AbstractDataRetrieverConf {
	public static final String AGGREGATED_FEATURE_PERSONAL_THRESHOLD_MODEL_BUILDER_DATA_RETRIEVER = "aggregated_feature_personal_threshold_model_builder_data_retriever";

	private String aggregatedFeatureEventConfNameToCalibrate;
	private int desiredNumberOfIndicators;
	private String scoreNameToCalibrate;

	@JsonCreator
	public AggregatedFeaturePersonalThresholdModelBuilderDataRetrieverConf(
			@JsonProperty("timeRangeInSeconds") long timeRangeInSeconds,
			@JsonProperty("functions") List<JSONObject> functions,
			@JsonProperty("aggregatedFeatureEventConfNameToCalibrate") String aggregatedFeatureEventConfNameToCalibrate,
			@JsonProperty("scoreNameToCalibrate") String scoreNameToCalibrate,
			@JsonProperty("desiredNumberOfIndicators") int desiredNumberOfIndicators) {
		super(timeRangeInSeconds, functions);
		Assert.hasText(aggregatedFeatureEventConfNameToCalibrate);
		Assert.isTrue(desiredNumberOfIndicators > 0);
		Assert.notNull(scoreNameToCalibrate);
		this.aggregatedFeatureEventConfNameToCalibrate = aggregatedFeatureEventConfNameToCalibrate;
		this.desiredNumberOfIndicators = desiredNumberOfIndicators;
		this.scoreNameToCalibrate = scoreNameToCalibrate;
	}

	@Override
	public String getFactoryName() {
		return AGGREGATED_FEATURE_PERSONAL_THRESHOLD_MODEL_BUILDER_DATA_RETRIEVER;
	}

	public String getAggregatedFeatureEventConfNameToCalibrate() {
		return aggregatedFeatureEventConfNameToCalibrate;
	}

	public int getDesiredNumberOfIndicators() {
		return desiredNumberOfIndicators;
	}

	public String getScoreNameToCalibrate() {
		return scoreNameToCalibrate;
	}
}
