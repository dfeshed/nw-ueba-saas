package fortscale.ml.model.selector;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fortscale.utils.factory.FactoryConfig;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value = FeatureBucketContextSelectorConf.class, name = FeatureBucketContextSelectorConf.FEATURE_BUCKET_CONTEXT_SELECTOR),
		@JsonSubTypes.Type(value = AggregatedEventContextSelectorConf.class, name = AggregatedEventContextSelectorConf.AGGREGATED_EVENT_CONTEXT_SELECTOR),
		@JsonSubTypes.Type(value = AccumulatedSmartContextSelectorConf.class, name = AccumulatedSmartContextSelectorConf.ACCUMULATED_SMART_CONTEXT_SELECTOR_FACTORY_NAME)
})
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public interface IContextSelectorConf extends FactoryConfig {}
