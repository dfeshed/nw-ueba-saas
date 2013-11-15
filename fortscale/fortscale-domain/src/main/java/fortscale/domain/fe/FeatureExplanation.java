package fortscale.domain.fe;

import org.codehaus.jackson.annotate.JsonProperty;

public class FeatureExplanation implements IFeatureExplanation {
	public static final String FEATURE_DISTRIBUTION_FIELD = "f_dst";
	public static final String FEATURE_COUNT_FIELD = "f_cnt";
	public static final String FEATURE_REFERENCE_FIELD = "ref";
	
	@JsonProperty
	private Double featureDistribution;
	@JsonProperty
	private Integer featureCount;
	@JsonProperty
	private String featureReference;
	
	public FeatureExplanation(Double featureDistribution, Integer featureCount, String featureReference){
		this.featureDistribution = featureDistribution;
		this.featureCount = featureCount;
		this.featureReference = featureReference;
	}

	@Override
	public Double getFeatureDistribution() {
		return featureDistribution;
	}

	@Override
	public Integer getFeatrueCount() {
		return featureCount;
	}

	@Override
	public String getFeatureReference() {
		return featureReference;
	}

}
