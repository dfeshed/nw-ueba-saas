package fortscale.web.beans;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import fortscale.domain.analyst.Analyst;
import fortscale.domain.analyst.AnalystAuth;

public class AnalystBean {

	private Analyst analyst;
	private UserDetails analystAuth;
	
	public AnalystBean(Analyst analyst, UserDetails analystAuth) {
		this.analyst = analyst;
		this.analystAuth = analystAuth;
	}
	
	public String getFirstName() {
		return analyst.getFirstName();
	}
	public String getLastName() {
		return analyst.getLastName();
	}
	public String getUserName() {
		return analyst.getUserName();
	}
	public String getEmailAddress() {
		return analyst.getEmailAddress().toString();
	}

	public boolean isAdmin(){
		boolean ret = false;
		for(GrantedAuthority authority: analystAuth.getAuthorities()){
			if(authority.getAuthority().equals(AnalystAuth.ROLE_ADMIN)){
				ret = true;
				break;
			}
		}
		return ret;
	}
	
	public boolean isDisabled(){
		return analyst.isDisabled();
	}
}
