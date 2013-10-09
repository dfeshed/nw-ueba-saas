package fortscale.domain.fe;

import org.codehaus.jackson.annotate.JsonProperty;

import fortscale.utils.logging.Logger;

public class ADFeature implements IFeature{
	private static Logger logger = Logger.getLogger(ADFeature.class);
	public static final String UNIQUE_NAME_FIELD = "uniqueName";
	public static final String DISPLAY_NAME_FIELD = "displayName";
	public static final String FEATURE_VALUE_FIELD = "featureValue";
	public static final String FEATURE_SCORE_FIELD = "featureScore";

	@JsonProperty
	private String featureUniqueName;
	@JsonProperty
	private String featureDisplayName;
	@JsonProperty
	private Double featureValue;
	@JsonProperty
	private Double featureScore;
	
	public ADFeature(String uniqueName, String displayName, Double featureValue, Double featureScore){
		this.featureUniqueName = uniqueName;
		this.featureDisplayName = displayName;
		this.featureValue = featureValue;
		if(this.featureValue == null) {
			this.featureValue = 0d;
			logger.error("Got Null value in the featureValue!!! feature unique name: {}, feature display name: {}", this.featureUniqueName, this.featureDisplayName);
		}
		this.featureScore = featureScore;
		if (this.featureScore == null) {
			this.featureScore = 0d;
			logger.error("Got Null value in the featureScore!!! feature unique name: {}, feature display name: {}", this.featureUniqueName, this.featureDisplayName);
		}
	}
	
	//Only for json serialization.
	public ADFeature() {}

	public String getFeatureUniqueName() {
		return featureUniqueName;
	}

	public String getFeatureDisplayName() {
		return featureDisplayName;
	}

	public Double getFeatureValue() {
		return featureValue;
	}

	public Double getFeatureScore() {
		return featureScore;
	}
}
