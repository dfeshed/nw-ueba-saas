package fortscale.domain.fe;

import java.util.Date;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import fortscale.domain.core.AbstractDocument;

public class AbstractFEDocument extends AbstractDocument {
	public static final String timestampField = "timestamp";

	@Field(timestampField)
	private Date timestamp;
	
	@Indexed(unique = false, expireAfterSeconds=60*60*24*30)
	private Date lastModified;

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}
	
	
}
