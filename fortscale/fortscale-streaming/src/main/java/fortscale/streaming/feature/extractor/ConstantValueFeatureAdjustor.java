package fortscale.streaming.feature.extractor;

import com.fasterxml.jackson.annotation.JsonTypeName;

import net.minidev.json.JSONObject;

@JsonTypeName(ConstantValueFeatureAdjustor.CONSTANT_VALUE_FEATURE_ADJUSTOR_TYPE)
public class ConstantValueFeatureAdjustor implements FeatureAdjustor {
	protected static final String CONSTANT_VALUE_FEATURE_ADJUSTOR_TYPE = "const_val_feature_adjustor";
	
	
	private String constantValue;
	
	public ConstantValueFeatureAdjustor(){}
	
	public ConstantValueFeatureAdjustor(String constantValue){
		this.constantValue = constantValue;
	}

	@Override
	public Object adjust(Object feature, JSONObject message) {
		return constantValue;
	}

	
	
	public String getConstantValue() {
		return constantValue;
	}

	public void setConstantValue(String constantValue) {
		this.constantValue = constantValue;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		ConstantValueFeatureAdjustor that = (ConstantValueFeatureAdjustor) o;
		if(!constantValue.equals(that.constantValue))
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		return constantValue.hashCode();
	}
}
