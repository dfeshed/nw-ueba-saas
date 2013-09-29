package fortscale.domain.ad;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.util.Assert;

import fortscale.domain.core.AbstractDocument;

public class AdObject extends AbstractDocument{
	public static final String dnField = "distinguishedName";
	public static final String timestampField = "timestamp";
	
	@Indexed
	@Field(dnField)
	private String distinguishedName;
	@Indexed(expireAfterSeconds=60*60*24*4)
	@Field(timestampField)
	private String timestamp;
	
	public AdObject(String distinguishedName){
		Assert.hasText(distinguishedName);
		this.distinguishedName = distinguishedName;
	}

	public String getDistinguishedName() {
		return distinguishedName;
	}

	public void setDistinguishedName(String distinguishedName) {
		this.distinguishedName = distinguishedName;
	}
	
	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
}
