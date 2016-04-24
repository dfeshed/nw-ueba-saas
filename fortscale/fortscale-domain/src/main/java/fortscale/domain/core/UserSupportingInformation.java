package fortscale.domain.core;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fortscale.domain.ad.AdUserGroup;

import java.util.HashSet;
import java.util.Set;

/**
 * supporting information for tag evidences - the state of the user in the time of the evidence creation.
 * Created by galiar on 12/08/2015.
 */

@JsonTypeName("userSupportingInformation")
public class UserSupportingInformation extends EntitySupportingInformation {

	public UserSupportingInformation(){};


	private String username;
	private String SAMAccountName;
	private String adUserName;
	private String title;
	private String department;
	private String manager;
	private Set<AdUserDirectReport> directReports = new HashSet<AdUserDirectReport>();

	private Boolean normalUserAccount;
	private Boolean noPasswordRequired;
	private Boolean passwordNeverExpire;

	private String ou;
	private Set<AdUserGroup> AdminGroups;
	private Set<AdUserGroup> NonAdminGroups;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getSAMAccountName() {
		return SAMAccountName;
	}

	public void setSAMAccountName(String SAMAccountName) {
		this.SAMAccountName = SAMAccountName;
	}

	public String getAdUserName() {
		return adUserName;
	}

	public void setAdUserName(String adUserName) {
		this.adUserName = adUserName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getManager() {
		return manager;
	}

	public void setManager(String manager) {
		this.manager = manager;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public Set<AdUserDirectReport> getDirectReports() {
		return directReports;
	}

	public void setDirectReports(Set<AdUserDirectReport> directReports) {
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

	public Set<AdUserGroup> getAdminGroups() {
		return AdminGroups;
	}

	public void setAdminGroups(Set<AdUserGroup> adminGroups) {
		AdminGroups = adminGroups;
	}

	public String getOu() {
		return ou;
	}

	public void setOu(String ou) {
		this.ou = ou;
	}

	public Set<AdUserGroup> getNonAdminGroups() {
		return NonAdminGroups;
	}

	public void setNonAdminGroups(Set<AdUserGroup> nonAdminGroups) {
		NonAdminGroups = nonAdminGroups;
	}
}
