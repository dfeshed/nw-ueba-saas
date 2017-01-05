package fortscale.domain.ad;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;

public class AdObject implements Serializable {
	private static final long serialVersionUID = 4373713742111586501L;

	public static final String dnField = "distinguishedName";
	public static final String memberField = "member";
	public static final String nameField = "name";
	public static final String objectGUIDField = "objectGUID";
	public static final String timestampepochField = "timestampepoch";
	public static final String lastModifiedField = "lastModified";
	public static final String runTimeField = "runtime";

	@Indexed
	@Field(objectGUIDField)
	@Id
	/**
	 * The GuId field is the id for the collection - in mongo saved as "_id" field
	 */
	private String objectGUID;
	
	private String objectSid;
	
	@Indexed
	@Field(dnField)
	private String distinguishedName;

	@Indexed()
	@Field(timestampepochField)
	private Long timestampepoch;


	@Indexed(unique = false)
	@Field(lastModifiedField)
	private Date lastModified;
	
	@Field(runTimeField)
	private String runtime;

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


	public enum AdObjectType {
		GROUP("Group"), OU("OU"), USER("User"), COMPUTER("Computer");

		private final String displayName;

		AdObjectType(String displayName) {
			this.displayName = displayName;
		}

		@Override
		public String toString() {
			return displayName;
		}
	}
	
}
