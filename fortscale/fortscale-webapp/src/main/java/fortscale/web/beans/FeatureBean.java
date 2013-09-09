package fortscale.web.beans;

import fortscale.domain.fe.IFeature;

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

}
