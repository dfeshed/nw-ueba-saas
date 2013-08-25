package fortscale.domain.fe;

public class ADFeature {
	
	public static final String DISPLAY_NAME_FIELD = "displayName";
	public static final String FEATURE_VALUE_FIELD = "featureValue";
	public static final String FEATURE_SCORE_FIELD = "featureScore";

	private String uniqueName;
	private String displayName;
	private Double featureValue;
	private Double featureScore;
	
	public ADFeature(String uniqueName, String displayName, Double featureValue, Double featureScore){
		this.uniqueName = uniqueName;
		this.displayName = displayName;
		this.featureValue = featureValue;
		this.featureScore = featureScore;
	}

	public String getUniqueName() {
		return uniqueName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public Double getFeatureValue() {
		return featureValue;
	}

	public Double getFeatureScore() {
		return featureScore;
	}
}
