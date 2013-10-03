package fortscale.domain.ad;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.util.Assert;

public class AdUserGroup {
	@JsonProperty
	private final String dn;
	@JsonProperty
	private final String name;
	
	@JsonCreator
	public AdUserGroup(@JsonProperty("dn") String dn, @JsonProperty("name") String name){
		Assert.hasText(dn, "dn must not be null or empty!");
		Assert.hasText(name, "name must not be null or empty!");
		this.dn = dn;
		this.name = name;
	}
	
	@JsonIgnore
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
