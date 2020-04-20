package fortscale.domain.core;

import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Field;






public class AbstractAuditableDocument extends AbstractDocument {
	private static final long serialVersionUID = -4585812347688862037L;

	public static final String VERSION_FIELD_NAME = "version";
	public static final String LAST_MODIFIED_FIELD_NAME = "lastModified";
	public static final String CREATED_AT_FIELD_NAME = "createdAt";
	

	@Version
	@Field(VERSION_FIELD_NAME)
    private Long version;
	
    @CreatedDate
    @Field(CREATED_AT_FIELD_NAME)
    private DateTime createdAt;
    
	@LastModifiedDate
	@Field(LAST_MODIFIED_FIELD_NAME)
    private DateTime lastModified;

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString()).append(", ");
		sb.append("CreatedAt: ").append(getCreatedAt()).append(", ");
		sb.append("Version: ").append(getVersion()).append(", ");
		sb.append("LastModified: ").append(getLastModified());
		return sb.toString();
	}
	
	
	public Long getVersion() {
		return version;
	}

	public DateTime getCreatedAt() {
		return createdAt;
	}

	public DateTime getLastModified() {
		return lastModified;
	}
     
}
