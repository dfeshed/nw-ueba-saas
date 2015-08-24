package fortscale.domain.core;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.io.Serializable;
import java.util.*;

/**
 * supporting information for tag evidences - the state of the user in the time of the evidence creation.
 * Created by galiar on 12/08/2015.
 */

@JsonTypeName("userSupportingInformation")
public class UserSupportingInformation extends EntitySupportingInformation implements Serializable {

	public UserSupportingInformation(){};

	private String username;
	private String title;
	private String department;
	private User manager;
	private List<User> directReports;

	private boolean normalUserAccount;
	private boolean noPasswordRequired;
	private boolean passwordNeverExpire;

	private String ou;
	List<String> membershipGroups;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public User getManager() {
		return manager;
	}

	public void setManager(User manager) {
		this.manager = manager;
	}

	public List<User> getDirectReports() {
		return directReports;
	}

	public void setDirectReports(List<User> directReports) {
		this.directReports = directReports;
	}

	public boolean isNormalUserAccount() {
		return normalUserAccount;
	}

	public void setNormalUserAccount(boolean normalUserAccount) {
		this.normalUserAccount = normalUserAccount;
	}

	public boolean isNoPasswordRequired() {
		return noPasswordRequired;
	}

	public void setNoPasswordRequired(boolean noPasswordRequired) {
		this.noPasswordRequired = noPasswordRequired;
	}

	public boolean isPasswordNeverExpire() {
		return passwordNeverExpire;
	}

	public void setPasswordNeverExpire(boolean passwordNeverExpire) {
		this.passwordNeverExpire = passwordNeverExpire;
	}

	public String getOu() {
		return ou;
	}

	public void setOu(String ou) {
		this.ou = ou;
	}


	public List<String> getMembershipGroups() {
		return membershipGroups;
	}

	public void setMembershipGroups(List<String> membershipGroups) {
		this.membershipGroups = membershipGroups;
	}

	@Override
	public UserSupportingInformation getSupportingInformation(){
		return this;

	}



}
