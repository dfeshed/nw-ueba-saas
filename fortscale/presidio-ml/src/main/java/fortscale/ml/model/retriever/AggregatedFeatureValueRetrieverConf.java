package fortscale.ml.model.retriever;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.minidev.json.JSONObject;

import java.util.List;

public class AggregatedFeatureValueRetrieverConf extends AbstractAggregatedFeatureValueRetrieverConf {
	public static final String AGGREGATED_FEATURE_VALUE_RETRIEVER = "aggregated_feature_value_retriever";

	@JsonCreator
	public AggregatedFeatureValueRetrieverConf(
			@JsonProperty("timeRangeInSeconds") long timeRangeInSeconds,
			@JsonProperty("functions") List<JSONObject> functions,
			@JsonProperty("aggregatedFeatureEventConfName") String aggregatedFeatureEventConfName) {
		super(timeRangeInSeconds, functions, aggregatedFeatureEventConfName);
	}

	@Override
	public String getFactoryName() {
		return AGGREGATED_FEATURE_VALUE_RETRIEVER;
	}
}
