package fortscale.domain.core;

public interface IUserTable {

	public String getId();
	public Boolean getFollowed();
	public String getUsername();
	public String getAdUsernames();
	public String getSecUsernames();
	public String getSshUsernames();
	public String getVpnUsernames();
	public Double getSecScore();
	public Double getSshScore();
	public Double getVpnScore();
	public Double getTotalScore();
	public String getDisplayName();
	public String getJobTitle();
	public String getEmployeeId();
	public String getEmployeeNumber();
	public String getDn();
	public String getOu();
	public String getUserPrincipalName();
	public String getSamAcountName();
	public Long getAccountExpires();
	public Long getWhenChanged();
	public Long getWhenCreated();
	public String getStreetAddress();
	public String getCompany();
	public String getDivision();
	public String getRoomNumber();
	public Boolean getAccountIsDisabled();
	public Boolean getPasswordExpired();
	public Boolean getNoPasswordRequire();
	public Boolean getPasswordNeverExpires();
	public String getDirectReportsDn();
	public String getGroupsDn();
	public String getDepartment();
	public String getEmail();
	public String getPhone();
	public String getMobile();
	public String getHomePhone();
	public String getManagerId();
	public String getManagerUsername();
	public String getManagerName();
	public String getManagerEmployeeId();
	public Long getDisableAccountTime();
	public Long getLastActivityTime();
	public Long getSshLastActivityTime();
	public Long getVpnLastActivityTime();
	public Long getLoginLastActivityTime();
	public Boolean getIsUserAccountService();
	public Boolean getIsUserAdministrator();
	public Boolean getIsUserExecutive();
}
