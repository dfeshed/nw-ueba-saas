package fortscale.common.feature;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import fortscale.utils.ConversionUtils;
import org.apache.commons.lang.builder.EqualsBuilder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class AggrFeatureValue implements Serializable, FeatureValue {
	private static final long serialVersionUID = 1L;


	protected final static String AGGR_FEATURE_TOTAL_NUMBER_OF_EVENTS = "total";
	public static final String FEATURE_VALUE_TYPE = "aggr_feature_value";

	private Object value;
	private Map<String, Object> additionalInformationMap;

	public AggrFeatureValue(){}

	public AggrFeatureValue(Object value, Long total){
		this.value = value;
		setTotal(total);
	}

	public void setTotal(Long total){
		putAdditionalInformation(AGGR_FEATURE_TOTAL_NUMBER_OF_EVENTS, total);
	}

	public Long getTotal(){
		return ConversionUtils.convertToLong( additionalInformationMap.get(AGGR_FEATURE_TOTAL_NUMBER_OF_EVENTS) );
	}

	public void putAdditionalInformation (String key, Object value){
		if(additionalInformationMap == null){
			additionalInformationMap = new HashMap<String, Object>();
		}
		additionalInformationMap.put(key, value);
	}

	public Object getAdditionalInformation (String key){
		return additionalInformationMap == null ? null : additionalInformationMap.get(key);
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Map<String, Object> getAdditionalInformationMap() {
		return additionalInformationMap;
	}

	public void setAdditionalInformationMap(
			Map<String, Object> additionalInformationMap) {
		this.additionalInformationMap = additionalInformationMap;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		AggrFeatureValue other = (AggrFeatureValue) obj;
		return new EqualsBuilder().append(this.value, other.value).append(this.additionalInformationMap, other.additionalInformationMap).isEquals();
	}
}
