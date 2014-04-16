package fortscale.web.beans;

import fortscale.domain.fe.IFeature;
import fortscale.domain.fe.IFeatureExplanation;

public class FeatureBean{
	
	
	
	
	
	
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
	
	public boolean getIsDistList() {
		return feature.getIsGroupDistributionList() != null ? feature.getIsGroupDistributionList() : false;
	}
	
	public FeatureExlanationBean getExplanation(){
		FeatureExlanationBean ret = null;
		if(feature.getFeatureExplanation() != null){
			ret = new FeatureExlanationBean(feature.getFeatureExplanation());
		}
		return ret;
	}
	
	


	
	
	
	class FeatureExlanationBean{
		private IFeatureExplanation explanation;
//		private String description;
		
		public FeatureExlanationBean(IFeatureExplanation explanation){
			this.explanation = explanation;			
//			this.description = feature.getFeatureScore() > 80 ? generate_description( ) : "";
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
			return explanation.getDescription();
		}
		
		
		
		
	}

}
