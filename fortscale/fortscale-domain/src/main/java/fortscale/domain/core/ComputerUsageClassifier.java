package fortscale.domain.core;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Field;

public class ComputerUsageClassifier {

	public static final String CLASSIFIER_NAME_FIELD = "classifierName";
	public static final String USAGE_TYPE_FIELD = "usageType";
	public static final String WHEN_COMPUTED_FIELD = "whenComputed";
	
	@Field(CLASSIFIER_NAME_FIELD)
	private String classifierName;
	@Field(USAGE_TYPE_FIELD)
	private ComputerUsageType usageType;
	@Field(WHEN_COMPUTED_FIELD)
	private Date whenComputed;
	
	public ComputerUsageClassifier() {
		this.whenComputed = new Date();
	}
	
	public ComputerUsageClassifier(String classifierName, ComputerUsageType usageType) {
		this(classifierName, usageType, new Date());
	}
	
	public ComputerUsageClassifier(String classifierName, ComputerUsageType usageType, Date whenComputed) {
		this.classifierName = classifierName;
		this.usageType = usageType;
		this.whenComputed = whenComputed;
	}
	
	public String getClassifierName() {
		return classifierName;
	}
	public void setClassifierName(String classifierName) {
		this.classifierName = classifierName;
	}
	public void setUsageType(ComputerUsageType usageType) {
		this.usageType = usageType;
	}
	public ComputerUsageType getUsageType() {
		return (this.usageType==null)? ComputerUsageType.Unknown : this.usageType;
	}
	public void setWhenComputed(Date whenComputed) {
		this.whenComputed = whenComputed;
	}
	public Date getWhenComputed() {
		return whenComputed;
	}
}
