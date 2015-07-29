package fortscale.aggregation.feature.functions;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class AggrFeatureValue {
	private Object value;
	private Map<String, Object> additionalInformationMap;
	
	public AggrFeatureValue(Object value){
		this.value = value;
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

	public void setValue(Object key) {
		this.value = key;
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
