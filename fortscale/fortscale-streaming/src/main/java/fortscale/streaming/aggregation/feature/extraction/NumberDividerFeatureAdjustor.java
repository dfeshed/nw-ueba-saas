package fortscale.streaming.aggregation.feature.extraction;

import static fortscale.utils.ConversionUtils.convertToDouble;
import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.internal.core.Assert;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(NumberDividerFeatureAdjustor.NUMBER_DIVIDER_FEATURE_ADJUSTOR)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class NumberDividerFeatureAdjustor implements FeatureAdjustor {
	protected static final String NUMBER_DIVIDER_FEATURE_ADJUSTOR = "number_divider_feature_adjustor";

	private double additionToDenominator;
	private String denominatorFieldName;

	public NumberDividerFeatureAdjustor(@JsonProperty("additionToDenominator") double additionToDenominator, @JsonProperty("denominatorFieldName") String denominatorFieldName) {
		setAdditionToDenominator(additionToDenominator);
		setDenominatorFieldName(denominatorFieldName);
	}

	@Override
	public Object adjust(Object value, JSONObject message) {
		Double originalValue = convertToDouble(value);
		Double denominator = convertToDouble(message.get(denominatorFieldName));

		Double dividedValue = null;
		if (originalValue != null && denominator != null) {
			denominator += additionToDenominator;
			if(denominator != 0){
				dividedValue = originalValue / denominator;
			}
		}

		return dividedValue;
	}

	public double getAdditionToDenominator() {
		return additionToDenominator;
	}

	public void setAdditionToDenominator(double additionToDenominator) {
		this.additionToDenominator = additionToDenominator;
	}

	public String getDenominatorFieldName() {
		return denominatorFieldName;
	}

	public void setDenominatorFieldName(String denominatorFieldName) {
		Assert.isTrue(StringUtils.isNotBlank(denominatorFieldName));
		this.denominatorFieldName = denominatorFieldName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		NumberDividerFeatureAdjustor that = (NumberDividerFeatureAdjustor)o;
		if (additionToDenominator != that.additionToDenominator)
			return false;
		if (!denominatorFieldName.equals(that.denominatorFieldName))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return denominatorFieldName.hashCode();
	}
}
