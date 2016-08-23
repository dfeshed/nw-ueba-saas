package fortscale.ml.model.retriever;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fortscale.ml.model.retriever.pattern.replacement.PatternReplacementConf;
import fortscale.utils.factory.FactoryConfig;
import net.minidev.json.JSONObject;
import org.springframework.util.Assert;

import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value = ContextHistogramRetrieverConf.class, name = ContextHistogramRetrieverConf.CONTEXT_HISTOGRAM_RETRIEVER),
		@JsonSubTypes.Type(value = AggregatedFeatureValueRetrieverConf.class, name = AggregatedFeatureValueRetrieverConf.AGGREGATED_FEATURE_VALUE_RETRIEVER),
		@JsonSubTypes.Type(value = EntityEventValueRetrieverConf.class, name = EntityEventValueRetrieverConf.ENTITY_EVENT_VALUE_RETRIEVER),
		@JsonSubTypes.Type(value = EntityEventUnreducedScoreRetrieverConf.class, name = EntityEventUnreducedScoreRetrieverConf.ENTITY_EVENT_UNREDUCED_SCORE_RETRIEVER),
		@JsonSubTypes.Type(value = AggregatedFeatureEventUnreducedScoreRetrieverConf.class, name = AggregatedFeatureEventUnreducedScoreRetrieverConf.AGGREGATED_FEATURE_EVENT_UNREDUCED_SCORE_RETRIEVER),
		@JsonSubTypes.Type(value = ModelRetrieverConf.class, name = ModelRetrieverConf.MODEL_RETRIEVER),
		@JsonSubTypes.Type(value = AggregatedFeaturePersonalThresholdModelBuilderDataRetrieverConf.class, name = AggregatedFeaturePersonalThresholdModelBuilderDataRetrieverConf.AGGREGATED_FEATURE_PERSONAL_THRESHOLD_MODEL_BUILDER_DATA_RETRIEVER)
})
public abstract class AbstractDataRetrieverConf implements FactoryConfig {
	private long timeRangeInSeconds;
	private List<JSONObject> functions;
	private PatternReplacementConf patternReplacementConf;

	public AbstractDataRetrieverConf(long timeRangeInSeconds, List<JSONObject> functions) {
		Assert.isTrue(timeRangeInSeconds > 0);
		Assert.notNull(functions);

		this.timeRangeInSeconds = timeRangeInSeconds;
		this.functions = functions;
		this.patternReplacementConf = null;
	}

	public long getTimeRangeInSeconds() {
		return timeRangeInSeconds;
	}

	public List<JSONObject> getFunctionConfs() {
		return functions;
	}

	public PatternReplacementConf getPatternReplacementConf() {
		return patternReplacementConf;
	}
}
