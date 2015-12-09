package fortscale.ml.model.selector;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.Assert;

public class FeatureBucketContextSelectorConf implements IContextSelectorConf {
	public static final String FEATURE_BUCKET_CONTEXT_SELECTOR = "feature_bucket_context_selector";
	public static final String FEATURE_BUCKET_CONF_NAME_PROPERTY = "featureBucketConfName";

	private String featureBucketConfName;

	@JsonCreator
	public FeatureBucketContextSelectorConf(
			@JsonProperty(FEATURE_BUCKET_CONF_NAME_PROPERTY) String featureBucketConfName) {

		Assert.hasText(featureBucketConfName);
		this.featureBucketConfName = featureBucketConfName;
	}

	@Override
	public String getFactoryName() {
		return FEATURE_BUCKET_CONTEXT_SELECTOR;
	}

	public String getFeatureBucketConfName() {
		return featureBucketConfName;
	}
}
