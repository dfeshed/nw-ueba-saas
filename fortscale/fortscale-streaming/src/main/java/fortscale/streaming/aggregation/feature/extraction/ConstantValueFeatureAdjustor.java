package fortscale.streaming.aggregation.feature.extraction;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fortscale.streaming.aggregation.feature.Feature;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

@JsonTypeName(ConstantValueFeatureAdjustor.CONSTANT_VALUE_FEATURE_ADJUSTOR_TYPE)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class ConstantValueFeatureAdjustor implements FeatureAdjustor {
	protected static final String CONSTANT_VALUE_FEATURE_ADJUSTOR_TYPE = "const_val_feature_adjustor";

	private String constantValue;

	public ConstantValueFeatureAdjustor(@JsonProperty("constantValue") String constantValue) {
		setConstantValue(constantValue);
	}

	@Override
	public Object adjust(Object value, JSONObject message) {
		return  constantValue;
	}

	public String getConstantValue() {
		return constantValue;
	}

	public void setConstantValue(String constantValue) {
		Assert.isTrue(StringUtils.isNotBlank(constantValue));
		this.constantValue = constantValue;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		ConstantValueFeatureAdjustor that = (ConstantValueFeatureAdjustor)o;
		if(!constantValue.equals(that.constantValue))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		return constantValue.hashCode();
	}
}
