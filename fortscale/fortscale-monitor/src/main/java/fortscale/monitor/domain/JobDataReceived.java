package fortscale.monitor.domain;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

/**
 * indicates the amount of data received by the job. Could hold values of 
 * various types.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
//NON-NULL tells Jackson to ignore fields which their value is null.
public class JobDataReceived {


	private String dataType;
	private Integer value;
	private String valueType;

	
	public JobDataReceived() {}
	
	public JobDataReceived(String dataType, Integer value, String valueType) {
		this.dataType = dataType;
		this.value = value;
		this.valueType = valueType;
		normalizeValue();
	}
	
	private void normalizeValue() {
		if (valueType!=null && value !=null && value>1024) {
			if (valueType.equalsIgnoreCase("KB")) {
				valueType = "MB";
				value = value / 1024;
			} else if (valueType.equalsIgnoreCase("MB")) {
				valueType = "GB";
				value = value / 1024;
			}
		}
	}

	
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
		
		normalizeValue();
	}
	public String getValueType() {
		return valueType;
	}
	public void setValueType(String valueType) {
		this.valueType = valueType;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JobDataReceived other = (JobDataReceived) obj;
		if (dataType == null) {
			if (other.dataType != null)
				return false;
		} else if (!dataType.equals(other.dataType))
			return false;
		if (value != other.value)
			return false;
		if (valueType == null) {
			if (other.valueType != null)
				return false;
		} else if (!valueType.equals(other.valueType))
			return false;
		return true;
	}
	
}
