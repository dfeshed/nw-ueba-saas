package fortscale.domain.ad;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.util.Assert;

public class AdUserGroup {
	private final String dn;
	private final String name;
	
	public AdUserGroup(String dn, String name){
		Assert.hasText(dn, "dn must not be null or empty!");
		Assert.hasText(name, "name must not be null or empty!");
		this.dn = dn;
		this.name = name;
	}
	
	public AdUserGroup getCopy() {
		return new AdUserGroup(this.dn, this.name);
	}
	
	public String getDn() {
		return dn;
	}
	public String getName() {
		return name;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(obj == this) return true;
		if(!(obj instanceof AdUserGroup)) return false;
		AdUserGroup adUserGroup = (AdUserGroup)obj;
		return new EqualsBuilder().append(adUserGroup.getDn(), getDn()).isEquals();
	}
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getDn()).toHashCode();
	}
}
