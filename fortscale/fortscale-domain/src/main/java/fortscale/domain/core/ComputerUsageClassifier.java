package fortscale.domain.core;

import org.springframework.data.mongodb.core.mapping.Field;

public class ComputerUsageClassifier {

	public static final String CLASSIFIER_NAME_FIELD = "classifierName";
	public static final String IS_SERVER_FIELD = "isServer";
	public static final String IS_ENDPOINT_FIELD = "isEndpoint"; 
	
	@Field(CLASSIFIER_NAME_FIELD)
	private String classifierName;
	@Field(IS_SERVER_FIELD)
	private Boolean isServer;
	@Field(IS_ENDPOINT_FIELD)
	private Boolean isEndpoint;
	
	public ComputerUsageClassifier() {}
	
	public ComputerUsageClassifier(String classifierName, Boolean isServer, Boolean isEndpoint) {
		this.classifierName = classifierName;
		this.isServer = isServer;
		this.isEndpoint = isEndpoint;
	}
	
	public String getClassifierName() {
		return classifierName;
	}
	public void setClassifierName(String classifierName) {
		this.classifierName = classifierName;
	}
	public Boolean getIsServer() {
		return isServer;
	}
	public void setIsServer(Boolean isServer) {
		this.isServer = isServer;
	}
	public Boolean getIsEndpoint() {
		return isEndpoint;
	}
	public void setIsEndpoint(Boolean isEndpoint) {
		this.isEndpoint = isEndpoint;
	}
	
}
