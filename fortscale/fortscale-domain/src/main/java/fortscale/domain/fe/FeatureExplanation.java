package fortscale.domain.fe;


import org.codehaus.jackson.annotate.JsonProperty;

public class FeatureExplanation implements IFeatureExplanation {
	public static final String FEATURE_DISTRIBUTION_FIELD = "f_dst";
	public static final String FEATURE_COUNT_FIELD = "f_cnt";
	public static final String FEATURE_REFERENCE_FIELD = "ref";
	private static final String SINGLE_VALUE_DESCRIPTION = "No one else has this property";
	
	
	@JsonProperty
	private Double featureDistribution;
	@JsonProperty
	private Integer featureCount;
	@JsonProperty
	private String[] featureReference;
	
	@JsonProperty
	private String featureDescription;
	
	public FeatureExplanation(Double featureDistribution, Integer featureCount, String[] featureReference, Double score){
		this.featureDistribution = featureDistribution;
		this.featureCount = featureCount;
		this.featureReference = featureReference;
		
		this.featureDescription = score > 80 ? generate_description( ) : "";		
	}
	

	@Override
	public Double getFeatureDistribution() {
		return featureDistribution;
	}

	@Override
	public Integer getFeatureCount() {
		return featureCount;
	}

	@Override
	public String[] getFeatureReference() {
		return featureReference;
	}
	
	
	
	private String generate_description() {
		return (1 == featureCount ) ?
				generate_single_value_description( ) :
				generate_general_case_description( ) ;			
	}


	private String generate_general_case_description() {
		return featureDistribution < 0.1 ?
				String.format( "Only %d users (less than %d\\%) share this property", getFeatureCount(), (int)(getFeatureDistribution()*100) ) :
				"" ;
	}


	private String generate_single_value_description() {
		return SINGLE_VALUE_DESCRIPTION;
	}


	@Override
	public String getFeatureDescription() {
		return featureDescription;
	}

}
