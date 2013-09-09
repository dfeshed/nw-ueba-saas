package fortscale.domain.fe;

public class ADFeature implements IFeature{
	public static final String UNIQUE_NAME_FIELD = "uniqueName";
	public static final String DISPLAY_NAME_FIELD = "displayName";
	public static final String FEATURE_VALUE_FIELD = "featureValue";
	public static final String FEATURE_SCORE_FIELD = "featureScore";

	private String featureUniqueName;
	private String featureDisplayName;
	private Double featureValue;
	private Double featureScore;
	
	public ADFeature(String uniqueName, String displayName, Double featureValue, Double featureScore){
		this.featureUniqueName = uniqueName;
		this.featureDisplayName = displayName;
		this.featureValue = featureValue;
		this.featureScore = featureScore;
	}

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
