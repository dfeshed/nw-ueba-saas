package fortscale.monitor.domain;

/**
 * indicates the amount of data received by the job. Could hold values of 
 * various types.
 */
public class JobDataReceived {

	private String dataType;
	private int value;
	private String valueType;
	
	public JobDataReceived() {}
	
	public JobDataReceived(String dataType, int value, String valueType) {
		this.dataType = dataType;
		this.value = value;
		this.valueType = valueType;
	}
	
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
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
