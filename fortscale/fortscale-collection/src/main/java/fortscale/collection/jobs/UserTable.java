package fortscale.collection.jobs;

import org.joda.time.DateTime;

import fortscale.domain.ad.AdUserGroup;
import fortscale.domain.core.AdUserDirectReport;
import fortscale.domain.core.ApplicationUserDetails;
import fortscale.domain.core.ClassifierScore;
import fortscale.domain.core.IUserTable;
import fortscale.domain.core.User;
import fortscale.domain.events.LogEventsEnum;
import fortscale.services.UserApplication;
import fortscale.services.fe.Classifier;
import fortscale.utils.TimestampUtils;
import fortscale.utils.actdir.ADParser;
import fortscale.utils.logging.Logger;

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
	public Double getGroupMemebershipScore() {
		return getScore(Classifier.groups);
	}
	
	private Double getScore(Classifier classifier){
		Double ret = null;
		ClassifierScore classifierScore = user.getScore(classifier.getId());
		if(classifierScore != null){
			ret = classifierScore.getScore();
			if(ret != null){
				ret = new Double(Math.round(ret));
			}
		}
		
		return ret;
	}

	@Override
	public Double getSecScore() {
		return getScore(Classifier.auth);
	}

	@Override
	public Double getSshScore() {
		return getScore(Classifier.ssh);
	}

	@Override
	public Double getVpnScore() {
		return getScore(Classifier.vpn);
	}

	@Override
	public Double getTotalScore() {
		return getScore(Classifier.total);
	}

	@Override
	public String getDisplayName() {
		return user.getAdInfo().getDisplayName();
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
		return user.getAdInfo().getDisableAccountTime().getTime();
	}

	@Override
	public Long getLastActivityTime() {
		Long ret = null;
		for(DateTime dateTime: user.getLogLastActivityMap().values()){
			if(ret == null || ret < dateTime.getMillis()){
				ret = dateTime.getMillis();
			}
		}
		return TimestampUtils.convertToSeconds(ret);
	}

	@Override
	public Long getSshLastActivityTime() {
		return TimestampUtils.convertToSeconds(user.getLogLastActivity(LogEventsEnum.ssh).getMillis());
	}

	@Override
	public Long getVpnLastActivityTime() {
		return TimestampUtils.convertToSeconds(user.getLogLastActivity(LogEventsEnum.vpn).getMillis());
	}

	@Override
	public Long getLoginLastActivityTime() {
		return TimestampUtils.convertToSeconds(user.getLogLastActivity(LogEventsEnum.login).getMillis());
	}

}
