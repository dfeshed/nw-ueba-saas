package fortscale.streaming.feature.extractor;

import static fortscale.utils.ConversionUtils.convertToDouble;
import net.minidev.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonTypeName;



@JsonTypeName(InverseValueFeatureAdjustor.INVERSE_VALUE_FEATURE_ADJUSTOR_TYPE)
public class InverseValueFeatureAdjustor implements FeatureAdjustor {
	protected static final String INVERSE_VALUE_FEATURE_ADJUSTOR_TYPE = "inv_val_feature_adjustor";
	private static final int INVERSE_VALUE_FEATURE_ADJUSTOR_TYPE_HASH_CODE = INVERSE_VALUE_FEATURE_ADJUSTOR_TYPE.hashCode();
	
	private double denominator;
	
	public InverseValueFeatureAdjustor(){}
	
	public InverseValueFeatureAdjustor(double denominator){
		this.denominator = denominator;
	}

	@Override
	public Object adjust(Object feature, JSONObject message) {
		Double originalFieldValue = convertToDouble(feature);
		Double invOriginalFieldValue = (originalFieldValue==null || originalFieldValue==0)? null : 1.0/(originalFieldValue+denominator);
		
		return invOriginalFieldValue;
	}

	
	public double getDenominator() {
		return denominator;
	}

	public void setDenominator(double denominator) {
		this.denominator = denominator;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		InverseValueFeatureAdjustor that = (InverseValueFeatureAdjustor) o;
		if(denominator != that.denominator)
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		return INVERSE_VALUE_FEATURE_ADJUSTOR_TYPE_HASH_CODE;
	}
}
