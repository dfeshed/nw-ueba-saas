package fortscale.ml.model.selector;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fortscale.utils.factory.FactoryConfig;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value = FeatureBucketContextSelectorConf.class, name = FeatureBucketContextSelectorConf.FEATURE_BUCKET_CONTEXT_SELECTOR_CONF)
})
public interface ContextSelectorConf extends FactoryConfig {}
