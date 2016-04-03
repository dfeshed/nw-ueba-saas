package fortscale.utils.qradar.utility;

public class ParameterDefinition {

	// Name of the parameter
	String paramName;
	// Value of the parameter
	String paramValue;
	// If the parameter is a part of URL(path) = true or a parameter = false
	boolean inPath;
	// If the parameter is required = true
	boolean required;
	// if the parameter is a string value that requires encoding = true
	boolean encoded;

	public ParameterDefinition(String paramName, String paramValue, boolean inPath,
			boolean required, boolean encoded) {
		super();
		this.paramName = paramName;
		this.paramValue = paramValue;
		this.inPath = inPath;
		this.required = required;
		this.encoded = encoded;
	}

	public String getParamName() {
		if (paramName == null || paramName.trim().equalsIgnoreCase("")
				|| paramName.trim().length() < 1) {
			return null;
		}
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public String getParamValue() {
		if (paramValue == null || paramValue.trim().equalsIgnoreCase("")
				|| paramValue.trim().length() < 1) {
			return null;
		}
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}

	public boolean isInPath() {
		return inPath;
	}

	public void setInPath(boolean isInPath) {
		this.inPath = isInPath;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean isRequired) {
		this.required = isRequired;
	}

	public boolean isencoded() {
		return encoded;
	}

	public void setencoded(boolean isencoded) {
		this.encoded = isencoded;
	}

}