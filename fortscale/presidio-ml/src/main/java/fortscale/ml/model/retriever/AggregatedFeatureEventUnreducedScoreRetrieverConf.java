package fortscale.ml.model.retriever;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.minidev.json.JSONObject;
import org.springframework.util.Assert;

import java.util.List;

public class AggregatedFeatureEventUnreducedScoreRetrieverConf extends AbstractDataRetrieverConf {
	public static final String AGGREGATED_FEATURE_EVENT_UNREDUCED_SCORE_RETRIEVER = "aggregated_feature_event_unreduced_score_retriever";

	private String scoreNameToCalibrate;
	private int numOfIndicatorsPerDay;
	private String aggregatedFeatureEventToCalibrateConfName;
	private int numOfDays;

	@JsonCreator
	public AggregatedFeatureEventUnreducedScoreRetrieverConf(
			@JsonProperty("functions") List<JSONObject> functions,
			@JsonProperty("aggregatedFeatureEventToCalibrateConfName") String aggregatedFeatureEventToCalibrateConfName,
			@JsonProperty("scoreNameToCalibrate") String scoreNameToCalibrate,
			@JsonProperty("numOfDays") Integer numOfDays,
			@JsonProperty("numOfIndicatorsPerDay") int numOfIndicatorsPerDay) {
		super(numOfDays * 60 * 60 * 24, functions);
		Assert.hasText(aggregatedFeatureEventToCalibrateConfName);
		Assert.isTrue(numOfIndicatorsPerDay > 0);
		Assert.notNull(scoreNameToCalibrate);
		this.aggregatedFeatureEventToCalibrateConfName = aggregatedFeatureEventToCalibrateConfName;
		this.scoreNameToCalibrate = scoreNameToCalibrate;
		this.numOfDays = numOfDays;
		this.numOfIndicatorsPerDay = numOfIndicatorsPerDay;
	}

	@Override
	public String getFactoryName() {
		return AGGREGATED_FEATURE_EVENT_UNREDUCED_SCORE_RETRIEVER;
	}

	public String getAggregatedFeatureEventToCalibrateConfName() {
		return aggregatedFeatureEventToCalibrateConfName;
	}

	public int getNumOfIndicatorsPerDay() {
		return numOfIndicatorsPerDay;
	}

	public String getScoreNameToCalibrate() {
		return scoreNameToCalibrate;
	}

	public int getNumOfDays() {
		return numOfDays;
	}
}
