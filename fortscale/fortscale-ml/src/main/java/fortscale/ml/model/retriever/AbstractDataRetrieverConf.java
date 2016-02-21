package fortscale.ml.model.retriever;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fortscale.ml.model.retriever.pattern.replacement.RetrieverPatternReplacementConf;
import fortscale.utils.factory.FactoryConfig;
import net.minidev.json.JSONObject;
import org.springframework.util.Assert;

import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value = ContextHistogramRetrieverConf.class, name = ContextHistogramRetrieverConf.CONTEXT_HISTOGRAM_RETRIEVER),
		@JsonSubTypes.Type(value = AggregatedFeatureValueRetrieverConf.class, name = AggregatedFeatureValueRetrieverConf.AGGREGATED_FEATURE_VALUE_RETRIEVER),
		@JsonSubTypes.Type(value = EntityEventValueRetrieverConf.class, name = EntityEventValueRetrieverConf.ENTITY_EVENT_VALUE_RETRIEVER)
})
public abstract class AbstractDataRetrieverConf implements FactoryConfig {
	private long timeRangeInSeconds;
	private List<JSONObject> functions;
	private RetrieverPatternReplacementConf retrieverPatternReplacementConf;

	public AbstractDataRetrieverConf(long timeRangeInSeconds, List<JSONObject> functions) {
		Assert.isTrue(timeRangeInSeconds > 0);
		Assert.notNull(functions);

		this.timeRangeInSeconds = timeRangeInSeconds;
		this.functions = functions;
		this.retrieverPatternReplacementConf = null;
	}

	public long getTimeRangeInSeconds() {
		return timeRangeInSeconds;
	}

	public List<JSONObject> getFunctionConfs() {
		return functions;
	}

	public RetrieverPatternReplacementConf getRetrieverPatternReplacementConf() {
		return retrieverPatternReplacementConf;
	}
}
