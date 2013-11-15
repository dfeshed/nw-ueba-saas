package fortscale.activedirectory.featureextraction;

import fortscale.domain.fe.IFeature;
import fortscale.domain.fe.IFeatureExplanation;

public class Feature implements IFeature {

	private String featureUniqueName;
	private String featureDisplayName;
	private Double featureValue;
	private Double featureScore;
	private int    featureType;
	private Double featureDefaultValue;	

	public static final Double POSITIVE_STATUS = 1.0;
	public static final Double NEGATIVE_STATUS = 0.0;
	public static final Double MISSING_VALUE = null;
	
	public static final int FEATURE_TYPE_UNSET   = 0 ;
	public static final int FEATURE_TYPE_BOOLEAN = 1 ;
	public static final int FEATURE_TYPE_NOMINAL = 2 ;
	public static final int FEATURE_TYPE_STRING  = 3 ;
	public static final int FEATURE_TYPE_NUMERIC = 4 ;
	
	
	public Feature(String featureUniqueName, String featureDisplayName, int featureType, Double featureDefaultValue, Double featureValue) {
		this.featureUniqueName = featureUniqueName;
		this.featureDisplayName = featureDisplayName;
		this.featureType = featureType;
		this.featureDefaultValue = featureDefaultValue;
		this.featureValue = featureValue;
	}
	
	
	public String getFeatureUniqueName() {
		return this.featureUniqueName;
	}
	
	@Override
	public String getFeatureDisplayName() {
		return this.featureDisplayName;
	}
	
	public Double getFeatureValue() {
		return this.featureValue;
	}
	
	public void setFeatureValue(Double featureValue) {
		this.featureValue = featureValue;
	}
	
	public Double getFeatureDefaultValue() {
		return this.featureDefaultValue;
	}
	
	public void setFeatureDefaultValue(Double defaultValue) {
		this.featureDefaultValue = defaultValue;
	}
	
	public Double getFeatureScore() {
		return this.featureScore;
	}
	
	public void setFeatureScore(Double featureScore) {
		this.featureScore = featureScore; 
	}
	
	public int getFeatureType() {
		return this.featureType;
	}

	public boolean isBoolean() {
		return (this.featureType == FEATURE_TYPE_BOOLEAN) ? true : false;
	}
	
	public boolean isNumeric() {
		return (this.featureType == FEATURE_TYPE_NUMERIC) ? true : false;
	}


	@Override
	public IFeatureExplanation getFeatureExplanation() {
		return null;
	}



}
