package fortscale.ml.model.retriever;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.minidev.json.JSONObject;

import java.util.List;

public class AccumulatedAggregatedFeatureValueGlobalRetrieverConf extends AbstractAggregatedFeatureValueRetrieverConf {
    public static final String ACCUMULATED_AGGREGATED_FEATURE_VALUE_GLOBAL_RETRIEVER = "accumulated_aggregated_feature_value_global_retriever";

    @JsonCreator
    public AccumulatedAggregatedFeatureValueGlobalRetrieverConf(
            @JsonProperty("timeRangeInSeconds") long timeRangeInSeconds,
            @JsonProperty("functions") List<JSONObject> functions,
            @JsonProperty("aggregatedFeatureEventConfName") String aggregatedFeatureEventConfName) {
        super(timeRangeInSeconds, functions, aggregatedFeatureEventConfName);
    }

    @Override
    public String getFactoryName() {
        return ACCUMULATED_AGGREGATED_FEATURE_VALUE_GLOBAL_RETRIEVER;
    }
}
