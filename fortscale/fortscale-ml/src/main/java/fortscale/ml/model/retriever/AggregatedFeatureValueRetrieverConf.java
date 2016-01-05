package fortscale.ml.model.retriever;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.minidev.json.JSONObject;
import org.springframework.util.Assert;

import java.util.List;

public class AggregatedFeatureValueRetrieverConf extends AbstractDataRetrieverConf {
	public static final String AGGREGATED_FEATURE_VALUE_RETRIEVER = "aggregated_feature_value_retriever";

	private String aggregatedFeatureEventConfName;

	@JsonCreator
	public AggregatedFeatureValueRetrieverConf(
			@JsonProperty("timeRangeInSeconds") long timeRangeInSeconds,
			@JsonProperty("functions") List<JSONObject> functions,
			@JsonProperty("aggregatedFeatureEventConfName") String aggregatedFeatureEventConfName) {

		super(timeRangeInSeconds, functions);

		Assert.hasText(aggregatedFeatureEventConfName);
		this.aggregatedFeatureEventConfName = aggregatedFeatureEventConfName;
	}

	@Override
	public String getFactoryName() {
		return AGGREGATED_FEATURE_VALUE_RETRIEVER;
	}

	public String getAggregatedFeatureEventConfName() {
		return aggregatedFeatureEventConfName;
	}
}
