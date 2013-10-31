package fortscale.domain.ad;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.codehaus.jackson.annotate.JsonCreator;

public class AdUserGroup extends AdUserRelatedObject{
	
	
	@JsonCreator
	public AdUserGroup(String dn, String name) {
		super(dn, name);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(obj == this) return true;
		if(!(obj instanceof AdUserGroup)) return false;
		AdUserGroup adUserGroup = (AdUserGroup)obj;
		return new EqualsBuilder().append(adUserGroup.getDn(), getDn()).isEquals();
	}
}
