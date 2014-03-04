package fortscale.domain.ad;

import java.util.Date;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import fortscale.domain.core.AbstractDocument;

public class AdObject extends AbstractDocument{
	public static final String dnField = "distinguishedName";
	public static final String objectGUIDField = "objectGUID";
	public static final String timestampepochField = "timestampepoch";
	public static final String lastModifiedField = "lastModified";
	
	@Indexed
	@Field(objectGUIDField)
	private String objectGUID;
	
	private String objectSid;
	
	@Indexed
	@Field(dnField)
	private String distinguishedName;
	@Indexed()
	@Field(timestampepochField)
	private Long timestampepoch;
	
	@Indexed(unique = false, expireAfterSeconds=60*60*24*20)
	@Field(lastModifiedField)
	private Date lastModified;
		
	private String runtime;
	
//	public AdObject(String distinguishedName){
//		Assert.hasText(distinguishedName);
//		this.distinguishedName = distinguishedName;
//	}
	
	public String getObjectGUID() {
		return objectGUID;
	}

	public void setObjectGUID(String objectGUID) {
		this.objectGUID = objectGUID;
	}
	
	public String getDistinguishedName() {
		return distinguishedName;
	}

	public void setDistinguishedName(String distinguishedName) {
		this.distinguishedName = distinguishedName;
	}
	
	public Long getTimestampepoch() {
		return timestampepoch;
	}

	public void setTimestampepoch(Long timestampepoch) {
		this.timestampepoch = timestampepoch;
	}
	
	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public String getObjectSid() {
		return objectSid;
	}

	public void setObjectSid(String objectSid) {
		this.objectSid = objectSid;
	}
	
	@Deprecated
	//This is used for old documents which contains timestamp field instead of runtime
	public void setTimestamp(String timestamp) {
		this.runtime = timestamp;
	}

	public String getRuntime() {
		return runtime;
	}

	public void setRuntime(String runtime) {
		this.runtime = runtime;
	}
	
	
}
