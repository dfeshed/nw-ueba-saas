package fortscale.domain.ad;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.util.Assert;

import fortscale.domain.core.AbstractDocument;

public class AdObject extends AbstractDocument{
	public static final String dnField = "dn";
	
	@Indexed(unique = true)
	@Field(dnField)
	private String distinguishedName;
	
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
	
}
