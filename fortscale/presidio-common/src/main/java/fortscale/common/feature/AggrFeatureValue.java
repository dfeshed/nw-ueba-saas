package fortscale.common.feature;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.apache.commons.lang.builder.EqualsBuilder;

import java.io.Serializable;

@JsonAutoDetect(
		fieldVisibility = JsonAutoDetect.Visibility.ANY,
		getterVisibility = JsonAutoDetect.Visibility.NONE,
		isGetterVisibility = JsonAutoDetect.Visibility.NONE,
		setterVisibility = JsonAutoDetect.Visibility.NONE
)
public class AggrFeatureValue implements Serializable, FeatureValue {
	private static final long serialVersionUID = 1L;
	public static final String FEATURE_VALUE_TYPE = "aggr_feature_value";

	private Object value;
	private Long total;

	public AggrFeatureValue() {
	}

	public AggrFeatureValue(Object value, Long total) {
		this.value = value;
		this.total = total;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj.getClass() != getClass()) return false;
		AggrFeatureValue other = (AggrFeatureValue)obj;
		return new EqualsBuilder().append(this.value, other.value).append(this.total, other.total).isEquals();
	}
}
