package fortscale.domain.fe;

import org.codehaus.jackson.annotate.JsonProperty;

import fortscale.utils.logging.Logger;

public class ADFeature implements IFeature{
	private static Logger logger = Logger.getLogger(ADFeature.class);
	public static final String UNIQUE_NAME_FIELD = "uniqueName";
	public static final String DISPLAY_NAME_FIELD = "displayName";
	public static final String FEATURE_VALUE_FIELD = "featureValue";
	public static final String FEATURE_SCORE_FIELD = "featureScore";
	public static final String FEATURE_AD_IS_GROUP_DISTRIBUTION_LIST_FIELD = "dist";
	public static final String FEATURE_EXPLANATION_FIELD = "explain";

	@JsonProperty
	private String featureUniqueName;
	@JsonProperty
	private String featureDisplayName;
	@JsonProperty
	private Double featureValue;
	@JsonProperty
	private Double featureScore;
	@JsonProperty
	private Boolean isGroupDistributionList;
	@JsonProperty
	private IFeatureExplanation featureExplanation;
	
	public ADFeature(String uniqueName, String displayName, Double featureValue, Double featureScore, Boolean isGroupDistributionList, IFeatureExplanation explanation){
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
		this.featureExplanation = explanation;
		this.isGroupDistributionList = isGroupDistributionList;
	}
	
	//Only for json serialization.
	public ADFeature() {}

	@Override
	public String getFeatureUniqueName() {
		return featureUniqueName;
	}

	@Override
	public String getFeatureDisplayName() {
		return featureDisplayName;
	}

	@Override
	public Double getFeatureValue() {
		return featureValue;
	}

	@Override
	public Double getFeatureScore() {
		return featureScore;
	}

	@Override
	public Boolean getIsGroupDistributionList() {
		return isGroupDistributionList;
	}

	@Override
	public IFeatureExplanation getFeatureExplanation() {
		return featureExplanation;
	}
}
