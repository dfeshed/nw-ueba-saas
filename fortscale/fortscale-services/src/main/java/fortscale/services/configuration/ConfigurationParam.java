package fortscale.services.configuration;

/**
 * Created by idanp on 12/20/2015.
 */
public class ConfigurationParam {

	private String paramName;
	private Boolean paramFlag;
	private String paramValue;


	public ConfigurationParam(String paramName, Boolean paramFlag, String paramValue) {
		this.paramName = paramName;
		this.paramFlag = paramFlag;
		this.paramValue = paramValue;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public Boolean getParamFlag() {
		return paramFlag;
	}

	public void setParamFlag(Boolean paramFlag) {
		this.paramFlag = paramFlag;
	}

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}
}
