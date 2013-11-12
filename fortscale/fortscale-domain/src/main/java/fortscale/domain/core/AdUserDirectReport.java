package fortscale.domain.core;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.codehaus.jackson.annotate.JsonCreator;

import fortscale.domain.ad.AdUserRelatedObject;

public class AdUserDirectReport extends AdUserRelatedObject{
	private String userId;
	private String firstname, lastname;
	private String username;
	
	@JsonCreator
	public AdUserDirectReport(String dn, String name) {
		super(dn, name);
	}
	
	

	public String getUserId() {
		return userId;
	}



	public void setUserId(String userId) {
		this.userId = userId;
	}



	public String getFirstname() {
		return firstname;
	}



	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}



	public String getLastname() {
		return lastname;
	}



	public void setLastname(String lastname) {
		this.lastname = lastname;
	}



	public String getUsername() {
		return username;
	}



	public void setUsername(String username) {
		this.username = username;
	}



	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(obj == this) return true;
		if(!(obj instanceof AdUserDirectReport)) return false;
		AdUserDirectReport adUserDirectReport = (AdUserDirectReport)obj;
		return new EqualsBuilder().append(adUserDirectReport.getDn(), getDn()).isEquals();
	}
}
