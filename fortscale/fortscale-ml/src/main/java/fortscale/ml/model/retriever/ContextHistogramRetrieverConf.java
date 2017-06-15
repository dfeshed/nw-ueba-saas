package fortscale.ml.model.retriever;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.minidev.json.JSONObject;
import org.springframework.util.Assert;

import java.util.List;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class ContextHistogramRetrieverConf extends AbstractDataRetrieverConf {
	public static final String CONTEXT_HISTOGRAM_RETRIEVER = "context_histogram_retriever";

	private String featureBucketConfName;
	private String featureName;

	@JsonCreator
	public ContextHistogramRetrieverConf(
			@JsonProperty("timeRangeInSeconds") long timeRangeInSeconds,
			@JsonProperty("functions") List<JSONObject> functions,
			@JsonProperty("featureBucketConfName") String featureBucketConfName,
			@JsonProperty("featureName") String featureName) {

		super(timeRangeInSeconds, functions);

		Assert.hasText(featureBucketConfName);
		Assert.hasText(featureName);

		this.featureBucketConfName = featureBucketConfName;
		this.featureName = featureName;
	}

	@Override
	public String getFactoryName() {
		return CONTEXT_HISTOGRAM_RETRIEVER;
	}

	public String getFeatureBucketConfName() {
		return featureBucketConfName;
	}

	public String getFeatureName() {
		return featureName;
	}
}
