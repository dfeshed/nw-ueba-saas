package fortscale.domain.core;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection=Computer.COLLECTION_NAME)
@TypeAlias(value="Computer")
@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY, getterVisibility= JsonAutoDetect.Visibility.NONE, setterVisibility= JsonAutoDetect.Visibility.NONE)
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
	public static final String OU_FIELD = "ou";
	public static final String DOMAIN_FIELD = "domain";
	
	@Field(OPERATING_SYSTEM_FIELD)
	private String operatingSystem;

	@Field(DOMAIN_FIELD)
	private String domain;

	@Field(OPERATING_SYSTEM_SERVICE_PACK_FIELD)
	private String operatingSystemServicePack;
	
	@Field(OPERATING_SYSTEM_VERSION_FIELD)
	private String operatingSystemVersion;

	@Field(NAME_FIELD)
	@Indexed
	private String name;

	@Indexed(unique = true)
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
	
	@Field(OU_FIELD)
	private String ou;
	
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
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
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
	public String getOU() {
		return ou;
	}
	public void setOU(String ou) {
		this.ou = ou;
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

	@JsonIgnore
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
	
	public Object getPropertyValue(String propertyName) {
		switch (propertyName) {
		case Computer.DISTINGUISHED_NAME_FIELD : return getDistinguishedName();
		case Computer.NAME_FIELD : return getName();
		case Computer.OPERATING_SYSTEM_FIELD : return getOperatingSystem();
		case Computer.DOMAIN_FIELD : return getDomain();
		case Computer.OPERATING_SYSTEM_SERVICE_PACK_FIELD : return getOperatingSystemServicePack();
		case Computer.OPERATING_SYSTEM_VERSION_FIELD : return getOperatingSystemVersion();
		case Computer.SENSITIVE_MACHINE_FIELD : return getIsSensitive();
		case Computer.TIMESTAMP_FIELD : return getTimestamp();
		case Computer.USAGE_CLASSIFIERS_FIELD : return getUsageClassifiers();
		case Computer.WHEN_CHANGED_FIELD : return getWhenChanged();
		case Computer.WHEN_CREATED_FIELD : return getWhenCreated();
		case Computer.ID_FIELD : return getId();
		case Computer.OU_FIELD : return getOU();
		}
		throw new IllegalArgumentException(propertyName);
	}
}
