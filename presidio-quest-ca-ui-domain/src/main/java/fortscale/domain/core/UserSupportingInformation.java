package fortscale.domain.core;

import com.fasterxml.jackson.annotation.JsonTypeName;

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


	private Boolean normalUserAccount;
	private Boolean noPasswordRequired;
	private Boolean passwordNeverExpire;

	private String ou;

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


	public Boolean isNormalUserAccount() {
		return normalUserAccount;
	}

	public void setNormalUserAccount(Boolean normalUserAccount) {
		this.normalUserAccount = normalUserAccount;
	}

	public Boolean isNoPasswordRequired() {
		return noPasswordRequired;
	}

	public void setNoPasswordRequired(Boolean noPasswordRequired) {
		this.noPasswordRequired = noPasswordRequired;
	}

	public Boolean isPasswordNeverExpire() {
		return passwordNeverExpire;
	}

	public void setPasswordNeverExpire(Boolean passwordNeverExpire) {
		this.passwordNeverExpire = passwordNeverExpire;
	}


	public String getOu() {
		return ou;
	}

	public void setOu(String ou) {
		this.ou = ou;
	}

}
