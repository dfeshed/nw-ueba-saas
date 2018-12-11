package fortscale.ml.model.retriever;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fortscale.utils.factory.FactoryConfig;
import net.minidev.json.JSONObject;
import org.springframework.util.Assert;

import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value = ContextHistogramRetrieverConf.class, name = ContextHistogramRetrieverConf.CONTEXT_HISTOGRAM_RETRIEVER),
		@JsonSubTypes.Type(value = CategoricalFeatureValueRetrieverConf.class, name = CategoricalFeatureValueRetrieverConf.FACTORY_NAME),
		@JsonSubTypes.Type(value = AccumulatedAggregatedFeatureValueRetrieverConf.class, name = AccumulatedAggregatedFeatureValueRetrieverConf.ACCUMULATED_AGGREGATED_FEATURE_VALUE_RETRIEVER),
		@JsonSubTypes.Type(value = ModelRetrieverConf.class, name = ModelRetrieverConf.MODEL_RETRIEVER),
		@JsonSubTypes.Type(value = AccumulatedSmartDataRetrieverConf.class, name = AccumulatedSmartDataRetrieverConf.ACCUMULATED_SMART_DATA_RETRIEVER_FACTORY_NAME),
		@JsonSubTypes.Type(value = AccumulatedSmartValueRetrieverConf.class, name = AccumulatedSmartValueRetrieverConf.ACCUMULATED_SMART_VALUE_RETRIEVER_FACTORY_NAME),
		@JsonSubTypes.Type(value = AccumulatedContextSmartValueRetrieverConf.class, name = AccumulatedContextSmartValueRetrieverConf.ACCUMULATED_CONTEXT_SMART_VALUE_RETRIEVER_FACTORY_NAME),
		@JsonSubTypes.Type(value = EpochtimeToHighestDoubleMapRetrieverConf.class, name = EpochtimeToHighestDoubleMapRetrieverConf.EPOCHTIME_TO_HIGHEST_DOUBLE_MAP_RETRIEVER),
		@JsonSubTypes.Type(value = JoinPartitionsHistogramModelsRetrieverConf.class, name = JoinPartitionsHistogramModelsRetrieverConf.JOIN_PARTITIONS_HISTOGRAM_MODELS_RETRIEVER)
})
@JsonAutoDetect(
		fieldVisibility = JsonAutoDetect.Visibility.ANY,
		getterVisibility = JsonAutoDetect.Visibility.NONE,
		isGetterVisibility = JsonAutoDetect.Visibility.NONE,
		setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class AbstractDataRetrieverConf implements FactoryConfig {
	private long timeRangeInSeconds;
	private List<JSONObject> functions;

	public AbstractDataRetrieverConf(long timeRangeInSeconds, List<JSONObject> functions) {
		Assert.isTrue(timeRangeInSeconds > 0, "timeRangeInSeconds must be greater than 0.");
		Assert.notNull(functions, "functions cannot be null.");

		this.timeRangeInSeconds = timeRangeInSeconds;
		this.functions = functions;
	}

	public long getTimeRangeInSeconds() {
		return timeRangeInSeconds;
	}

	public List<JSONObject> getFunctionConfs() {
		return functions;
	}
}
