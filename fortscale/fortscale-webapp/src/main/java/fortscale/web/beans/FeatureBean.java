package fortscale.web.beans;

import fortscale.domain.fe.IFeature;
import fortscale.domain.fe.IFeatureExplanation;

public class FeatureBean{
	
	private static final String SINGLE_VALUE_DESCRIPTION = "No one else has this property";
	
	
	
	
	private IFeature feature;
	
	public FeatureBean(IFeature feature){
		this.feature = feature;
	}

	public String getFeatureUniqueName() {
		return feature.getFeatureUniqueName();
	}

	public String getFeatureDisplayName() {
		return feature.getFeatureDisplayName();
	}

	public String getFeatureValue() {
		return feature.getFeatureValue().toString();
	}

	public int getFeatureScore() {
		return feature.getFeatureScore().intValue();
	}
	
	public FeatureExlanationBean getExplanation(){
		FeatureExlanationBean ret = null;
		if(feature.getFeatureExplanation() != null){
			ret = new FeatureExlanationBean(feature.getFeatureExplanation());
		}
		return ret;
	}
	
	


	private String generate_single_value_description() {
		return SINGLE_VALUE_DESCRIPTION;
	}
	
	
	class FeatureExlanationBean{
		private IFeatureExplanation explanation;
		private String description;
		
		public FeatureExlanationBean(IFeatureExplanation explanation){
			this.explanation = explanation;			
			this.description = feature.getFeatureScore() > 80 ? generate_description( ) : "";
		}
		
		public Double getFeatureDistribution(){
			return explanation.getFeatureDistribution();
		}
		public Integer getFeatureCount(){
			return explanation.getFeatureCount();
		}
		public String[] getFeatureReference(){
			return explanation.getFeatureReference();
		}
		
		public String getFeatureDescription() {
			return description;
		}
		
		
		
		private String generate_description() {
			return (1 == getFeatureCount() ) ?
					generate_single_value_description( ) :
					generate_general_case_description( ) ;			
		}
		
		private String generate_general_case_description() {
			return explanation.getFeatureDistribution() < 0.1 ?
					String.format( "Only %d users (less than %d\\%) share this property", explanation.getFeatureCount(), (int)(getFeatureDistribution()*100) ) :
					"" ;
		}
	}

}
