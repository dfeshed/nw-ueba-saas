package fortscale.ml.model.selector;

import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonTypeName(FeatureBucketContextSelectorConf.FEATURE_BUCKET_CONTEXT_SELECTOR_CONF)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class FeatureBucketContextSelectorConf implements ContextSelectorConf {
	public static final String FEATURE_BUCKET_CONTEXT_SELECTOR_CONF = "feature_bucket_context_selector_conf";
	public static final String FEATURE_BUCKET_CONF_NAME_PROPERTY = "featureBucketConfName";

	@JsonProperty(FEATURE_BUCKET_CONF_NAME_PROPERTY)
	private String featureBucketConfName;
	
	public FeatureBucketContextSelectorConf(@JsonProperty(FEATURE_BUCKET_CONF_NAME_PROPERTY) String featureBucketConfName){
		Assert.hasText(featureBucketConfName);
		
		this.featureBucketConfName = featureBucketConfName;
	}

	public String getFeatureBucketConfName() {
		return featureBucketConfName;
	}
}
