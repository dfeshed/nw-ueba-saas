package fortscale.domain.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection=Computer.COLLECTION_NAME)
@TypeAlias(value="Computer")
public class Computer extends AbstractDocument {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3605143007220943382L;
	public static final String COLLECTION_NAME = "computer";
	public static final String OPERATING_SYSTEM_FIELD = "operatingSystem";
	public static final String OPERATING_SYSTEM_SERVICE_PACK_FIELD = "operatingSystemServicePack";
	public static final String OPERATING_SYSTEM_VERSION_FIELD = "operatingSystemVersion";
	public static final String NAME_FIELD = "name";
	public static final String DISTINGUISHED_NAME_FIELD = "distinguishedName";
	public static final String WHEN_CHANGED_FIELD = "whenChanged";
	public static final String WHEN_CREATED_FIELD = "whenCreated";
	public static final String USAGE_CLASSIFIERS_FIELD = "usageClassifiers";
	public static final String TIMESTAMP_FIELD = "timestamp";
	public static final String SENSITIVE_MACHINE_FIELD = "sensitive";
	
	@Field(OPERATING_SYSTEM_FIELD)
	private String operatingSystem;
	
	@Field(OPERATING_SYSTEM_SERVICE_PACK_FIELD)
	private String operatingSystemServicePack;
	
	@Field(OPERATING_SYSTEM_VERSION_FIELD)
	private String operatingSystemVersion;
	
	@Indexed(unique = true)
	@Field(NAME_FIELD)
	private String name;
	
	@Field(DISTINGUISHED_NAME_FIELD)
	private String distinguishedName;
	
	@Field(WHEN_CHANGED_FIELD)
	private Date whenChanged;
	
	@Indexed(direction=IndexDirection.DESCENDING)
	@Field(WHEN_CREATED_FIELD)
	private Date whenCreated;
	
	@Field(USAGE_CLASSIFIERS_FIELD)
	private List<ComputerUsageClassifier> usageClassifiers = new ArrayList<ComputerUsageClassifier>();
	
	@Field(TIMESTAMP_FIELD)
	private Date timestamp;
	
	@Field(SENSITIVE_MACHINE_FIELD)
	@Indexed
	private Boolean isSensitive;
	
	public Computer() {
		this.timestamp = new Date();
	}
	
	public Boolean getIsSensitive() {
		return isSensitive;
	}

	public String getOperatingSystem() {
		return operatingSystem;
	}
	public void setOperatingSystem(String operatingSystem) {
		this.operatingSystem = operatingSystem;
	}
	public String getOperatingSystemServicePack() {
		return operatingSystemServicePack;
	}
	public void setOperatingSystemServicePack(String operatingSystemServicePack) {
		this.operatingSystemServicePack = operatingSystemServicePack;
	}
	public String getOperatingSystemVersion() {
		return operatingSystemVersion;
	}
	public void setOperatingSystemVersion(String operatingSystemVersion) {
		this.operatingSystemVersion = operatingSystemVersion;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDistinguishedName() {
		return distinguishedName;
	}
	public void setDistinguishedName(String distinguishedName) {
		this.distinguishedName = distinguishedName;
	}
	public Date getWhenChanged() {
		return whenChanged;
	}
	public void setWhenChanged(Date whenChanged) {
		this.whenChanged = whenChanged;
	}
	public Date getWhenCreated() {
		return whenCreated;
	}
	public void setWhenCreated(Date whenCreated) {
		this.whenCreated = whenCreated;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	public ComputerUsageClassifier getUsageClassifier(String classifierName) {
		for (ComputerUsageClassifier compUsageClassifier : usageClassifiers){
			if(compUsageClassifier.getClassifierName().equals(classifierName)){
				return compUsageClassifier;
			}
		}
		return null;
	}
	public static String getUsageClassfierField (String classifierFieldName ) {
		return String.format("%s.%s", Computer.USAGE_CLASSIFIERS_FIELD, classifierFieldName);
	}
	public void putUsageClassifier(ComputerUsageClassifier classifier) {
		ComputerUsageClassifier prevClassifier = getUsageClassifier(classifier.getClassifierName());
		if(prevClassifier == null){
			this.usageClassifiers.add(classifier);
		} else{
			prevClassifier.setUsageType(classifier.getUsageType());
			prevClassifier.setWhenComputed(classifier.getWhenComputed());
		}
	}	
	public void removeUsageClassifier(String classifier) {
		this.usageClassifiers.remove(classifier);
	}	
	public void clearUsageClassifiers() {
		this.usageClassifiers.clear();
	}
	
	public ComputerUsageType getUsageType() {
		// go over the usage classifiers and return the first one that is not unknown
		for (ComputerUsageClassifier classifier : usageClassifiers)
			if (classifier.getUsageType()!=ComputerUsageType.Unknown)
				return classifier.getUsageType();
		return ComputerUsageType.Unknown;
	}
	
	public List<ComputerUsageClassifier> getUsageClassifiers() {
		return usageClassifiers;
	}
}
