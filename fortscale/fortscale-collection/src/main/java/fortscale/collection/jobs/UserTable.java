package fortscale.collection.jobs;

import fortscale.domain.ad.AdUserGroup;
import fortscale.domain.core.*;
import fortscale.domain.events.LogEventsEnum;
import fortscale.services.UserApplication;
import fortscale.services.classifier.Classifier;
import fortscale.utils.actdir.ADParser;
import fortscale.utils.logging.Logger;
import org.joda.time.DateTime;

public class UserTable implements IUserTable {
	private static Logger logger = Logger.getLogger(UserTable.class);
	
	private User user;
	private User manager;
	private ADParser adUserParser;
	
	private String secUsernames;
	private String sshUsernames;
	private String vpnUsernames;
	
	
	public UserTable(User user, User manager){
		this.user = user;
		this.manager = manager;
		this.adUserParser = new ADParser();
	}

	@Override
	public Boolean getIs_user_executive() {
		return user.getExecutiveAccount();
	}
	
	@Override
	public Boolean getIs_user_administrator() {
		return user.getAdministratorAccount();
	}
	
	@Override
	public Boolean getIs_user_service_account() {
		return user.getUserServiceAccount();
	}
	
	@Override
	public String getId() {
		return user.getId();
	}

	@Override
	public Boolean getFollowed() {
		return user.getFollowed();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

	@Override
	public String getAdUsernames() {
		String ret = null;
		ApplicationUserDetails applicationUserDetails = user.getApplicationUserDetails(UserApplication.active_directory.getId());
		if(applicationUserDetails != null){
			ret = applicationUserDetails.getUserName();
		}
		
		return ret;
	}
	
	

	public void setSecUsernames(String secUsernames) {
		this.secUsernames = secUsernames;
	}

	public void setSshUsernames(String sshUsernames) {
		this.sshUsernames = sshUsernames;
	}

	public void setVpnUsernames(String vpnUsernames) {
		this.vpnUsernames = vpnUsernames;
	}



	@Override
	public String getSecUsernames() {
		return secUsernames;
	}

	@Override
	public String getSshUsernames() {
		return sshUsernames;
	}

	@Override
	public String getVpnUsernames() {
		return vpnUsernames;
	}


	@Override
	public String getDisplayName() {
		return user.getDisplayName();
	}

	@Override
	public String getJobTitle() {
		return user.getAdInfo().getPosition();
	}

	@Override
	public String getEmployeeId() {
		return user.getAdInfo().getEmployeeID();
	}

	@Override
	public String getEmployeeNumber() {
		return user.getAdInfo().getEmployeeNumber();
	}

	@Override
	public String getDn() {
		return user.getAdInfo().getDn();
	}

	@Override
	public String getOu() {
		String dn = user.getAdInfo().getDn();
		return dn != null ? adUserParser.parseOUFromDN(dn) : null;	
	}

	@Override
	public String getUserPrincipalName() {
		return user.getAdInfo().getUserPrincipalName();
	}

	@Override
	public String getSamAcountName() {
		return user.getAdInfo().getsAMAccountName();
	}

	@Override
	public Long getAccountExpires() {
		return user.getAdInfo().getAccountExpires() != null ? user.getAdInfo().getAccountExpires().getTime() : null;
	}

	@Override
	public Long getWhenChanged() {
		return user.getAdInfo().getWhenChanged() != null ? user.getAdInfo().getWhenChanged().getTime() : null;
	}

	@Override
	public Long getWhenCreated() {
		return user.getAdInfo().getWhenCreated() != null ? user.getAdInfo().getWhenCreated().getTime() : null;
	}

	@Override
	public String getStreetAddress() {
		return user.getAdInfo().getStreetAddress();
	}

	@Override
	public String getCompany() {
		return user.getAdInfo().getCompany();
	}

	@Override
	public String getDivision() {
		return user.getAdInfo().getDivision();
	}

	@Override
	public String getRoomNumber() {
		return user.getAdInfo().getRoomNumber();
	}

	@Override
	public Boolean getAccountIsDisabled() {			
		return user.getAdInfo().getIsAccountDisabled();
	}

	@Override
	public Boolean getPasswordExpired() {
		try{
			return user.getAdInfo().getUserAccountControl() != null ? adUserParser.isTrustedToAuthForDelegation(user.getAdInfo().getUserAccountControl()) : null;
		} catch (NumberFormatException e) {
			logger.warn("got NumberFormatException while trying to parse user account control.", user.getAdInfo().getUserAccountControl());
		}
			
		return null;
	}

	@Override
	public Boolean getNoPasswordRequire() {
		try{
			return user.getAdInfo().getUserAccountControl() != null ? adUserParser.isNoPasswordRequiresValue(user.getAdInfo().getUserAccountControl()) : null;
		} catch (NumberFormatException e) {
			logger.warn("got NumberFormatException while trying to parse user account control.", user.getAdInfo().getUserAccountControl());
		}
			
		return null;
	}

	@Override
	public Boolean getPasswordNeverExpires() {
		try{
			return user.getAdInfo().getUserAccountControl() != null ? adUserParser.isPasswordNeverExpiresValue(user.getAdInfo().getUserAccountControl()) : null;
		} catch (NumberFormatException e) {
			logger.warn("got NumberFormatException while trying to parse user account control.", user.getAdInfo().getUserAccountControl());
		}
			
		return null;
	}

	@Override
	public String getDirectReportsDn() {
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for(AdUserDirectReport directReport: user.getAdInfo().getDirectReports()){
			if(first){
				first = false;
			} else{
				builder.append(",");
			}
			builder.append(directReport.getDn());
		}
		return builder.toString();
	}

	@Override
	public String getGroupsDn() {
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for(AdUserGroup adUserGroup: user.getAdInfo().getGroups()){
			if(first){
				first = false;
			} else{
				builder.append(",");
			}
			builder.append(adUserGroup.getDn());
		}
		return builder.toString();
	}

	@Override
	public String getDepartment() {
		return user.getAdInfo().getDepartment();
	}

	@Override
	public String getEmail() {
		if(user.getAdInfo().getEmailAddress() == null){
			return null;
		}
		return user.getAdInfo().getEmailAddress().toString();
	}

	@Override
	public String getPhone() {
		return user.getAdInfo().getTelephoneNumber();
	}

	@Override
	public String getMobile() {
		return user.getAdInfo().getMobile();
	}

	@Override
	public String getHomePhone() {
		return user.getAdInfo().getHomePhone();
	}

	@Override
	public String getManagerName() {
		return manager != null ? manager.getAdInfo().getDisplayName() : null;
	}

	@Override
	public String getManagerEmployeeId() {
		return manager != null ? manager.getAdInfo().getEmployeeID() : null;
	}

	@Override
	public String getManagerUsername() {
		return manager != null ? manager.getUsername() : null;
	}

	@Override
	public String getManagerId() {
		return manager != null ? manager.getId() : null;
	}

	@Override
	public Long getDisableAccountTime() {
		return convertToEpochTime(user.getAdInfo().getDisableAccountTime());
	}

	@Override
	public Long getLastActivityTime() {
		DateTime ret = null;
		for(DateTime dateTime: user.getLogLastActivityMap().values()){
			if(ret == null || ret.isBefore(dateTime)){
				ret = dateTime;
			}
		}
		
		return convertToEpochTime(ret);
	}
	
	private Long convertToEpochTime(DateTime time){
		if(time != null){
			return time.getMillis();
		} else{
			return null;
		}
	}

	@Override
	public Long getSshLastActivityTime() {
		return convertToEpochTime(user.getLogLastActivity(LogEventsEnum.ssh.name()));
	}

	@Override
	public Long getVpnLastActivityTime() {
		return convertToEpochTime(user.getLogLastActivity(LogEventsEnum.vpn.name()));
	}

	@Override
	public Long getLoginLastActivityTime() {
		return convertToEpochTime(user.getLogLastActivity(LogEventsEnum.login.name()));
	}



}
