package fortscale.web.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import fortscale.domain.ad.AdUserGroup;
import fortscale.domain.core.AdUserDirectReport;
import fortscale.domain.core.ApplicationUserDetails;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.utils.actdir.ADUserParser;

@Configurable(preConstruction = true, autowire=Autowire.BY_NAME, dependencyCheck=true)
public class UserDetailsBean implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Autowired
	private UserRepository userRepository;

	private User user;
	private User manager;
	private ADUserParser adUserParser;
	
	public UserDetailsBean(User user, User manager){
		this.user = user;
		this.manager = manager;
		this.adUserParser = new ADUserParser();
	}
	
	public String getUsername() {
		return user.getUsername();
	}
	
	public Map<String, ApplicationUserDetails> getApplicationUserDetails() {
		return user.getApplicationUserDetails();
	}


	public String getName() {
		return user.getFirstname() + " " + user.getLastname();
	}

	public String getJobTitle() {
		return user.getPosition();
	}

	public String getAdEmployeeID() {
		return user.getAdEmployeeID();
	}
	
	public String getAdEmployeeNumber() {
		return user.getAdEmployeeNumber();
	}
	
	public String getAdDisplayName(){
		return user.getAdDisplayName();
	}
	
	public String getOu(){
		return adUserParser.parseOUFromDN(user.getAdDn());
	}
	
	public String getAdUserPrincipalName(){
		return user.getAdUserPrincipalName();
	}
	
	public String getSAMAcountName(){
		return user.getAdSAMAccountName();
	}
	
	public Long getAdAcountExpires(){
		return user.getAccountExpires() != null ? user.getAccountExpires().getTime() : null;
	}
	
	public String getLogonHours(){
		return user.getAdLogonHours();
	}
	
	public Long getAdWhenChanged(){
		return user.getAdWhenChanged() != null ? user.getAdWhenChanged().getTime() : null;
	}
	
	public Long getAdWhenCreated(){
		return user.getAdWhenCreated() != null ? user.getAdWhenCreated().getTime() : null;
	}
	
	public String getDescription(){
		return user.getAdDescription();
	}
	
	public String getStreetAddress(){
		return user.getAdStreetAddress();
	}
	
	public String getCompany(){
		return user.getAdCompany();
	}
	
	public String getAdC(){
		return user.getAdC();
	}
	
	public String getDivision(){
		return user.getAdDivision();
	}
	
	public String getAdL(){
		return user.getAdL();
	}
	
	public String getAdO(){
		return user.getAdO();
	}
	
	public String getRoomNumber(){
		return user.getAdRoomNumber();
	}
		
	public boolean isAccountIsDisabled() {
		return adUserParser.isAccountIsDisabled(user.getAdUserAccountControl());
	}
	
	public boolean isLockout() {
		return adUserParser.isLockout(user.getAdUserAccountControl());
	}
	
	public boolean isWorkstationTrustAccount() {
		return adUserParser.isWorkstationTrustAccount(user.getAdUserAccountControl());
	}
	
	
	public boolean isServerTrustAccount() {
		return adUserParser.isServerTrustAccount(user.getAdUserAccountControl());
	}
	
	public boolean isSmartcardRequired() {
		return adUserParser.isSmartcardRequired(user.getAdUserAccountControl());
	}
	
	
	public boolean isTrustedForDelegation() {
		return adUserParser.isTrustedForDelegation(user.getAdUserAccountControl());
	}
	
	public boolean isNotDelegated() {
		return adUserParser.isNotDelegated(user.getAdUserAccountControl());
	}
	
	public boolean isPasswordExpired() {
		return adUserParser.isPasswordExpired(user.getAdUserAccountControl());
	}
	
	public boolean isTrustedToAuthForDelegation() {
		return adUserParser.isTrustedToAuthForDelegation(user.getAdUserAccountControl());
	}

	
	public boolean isNoPasswordRequiresValue() {
		return adUserParser.isNoPasswordRequiresValue(user.getAdUserAccountControl());
	}

	
	public boolean isNormalUserAccountValue() {
		return adUserParser.isNormalUserAccountValue(user.getAdUserAccountControl());
	} 
	
	
	public boolean isInterdomainTrustAccountValue() {
		return adUserParser.isInterdomainTrustAccountValue(user.getAdUserAccountControl());
	}

	
	public boolean isPasswordNeverExpiresValue() {
		return adUserParser.isPasswordNeverExpiresValue(user.getAdUserAccountControl());
	}
	
	
	public boolean isDesKeyOnlyValue() {
		return adUserParser.isDesKeyOnlyValue(user.getAdUserAccountControl());
	}
	
	public List<UserDirectReportBean> getUserDirectReports() {
		List<UserDirectReportBean> ret = new ArrayList<>();
		for(AdUserDirectReport adUserDirectReport: user.getAdDirectReports()){
			ret.add(new UserDirectReportBean(adUserDirectReport));
		}
		return ret;
	}

	public List<UserGroupBean> getGroups() {
		List<UserGroupBean> ret = new ArrayList<>();
		for(AdUserGroup adUserGroup: user.getGroups()){
			ret.add(new UserGroupBean(adUserGroup));
		}
		return ret;
	}

	public String getDepartment() {
		return user.getDepartment();
	}
	
	public String getEmail(){
		if(user.getEmailAddress() == null){
			return null;
		}
		return user.getEmailAddress().toString();
	}
	
	public String getPhone(){
		return user.getTelephoneNumber();
	}
	
	public String getMobilePhone(){
		return user.getMobile();
	}
	
	public String getOtherFacsimileTelephoneNumber() {
		return user.getOtherFacsimileTelephoneNumber();
	}

	public String getOtherHomePhone() {
		return user.getOtherHomePhone();
	}


	public String getHomePhone() {
		return user.getHomePhone();
	}

	public String getOtherMobile() {
		return user.getOtherMobile();
	}


	public String getOtherTelephone() {
		return user.getOtherTelephone();
	}
	
	public String getImage() {
		return user.getThumbnailPhoto();
	}

	public UserManagerBean getManager() {
		UserManagerBean ret = null;
		if(manager != null){
			ret = new UserManagerBean(manager);
		}
//		if(user.getManagerDN() != null){
//			User manager = userRepository.findByAdDn(user.getManagerDN());
//			
//			if(manager != null){
//				ret.setId(manager.getEmployeeID());
//				ret.setName(manager.getFirstname() + " " + manager.getLastname());
//			}
//		}
		return ret;
	}

	public class UserManagerBean{
		private User managerUser;
		
		public UserManagerBean(User manager) {
			this.managerUser = manager;
		}
		
		public String getId() {
			return managerUser.getId();
		}
		
		public String getName() {
			return String.format("%s %s", managerUser.getFirstname(), managerUser.getLastname());
		}
		public String getWorkerId() {
			return managerUser.getAdEmployeeID();
		}
		public String getUsername() {
			return managerUser.getUsername();
		}
	}
	
	public class UserDirectReportBean{
		private AdUserDirectReport adUserDirectReport;
		
		public UserDirectReportBean(AdUserDirectReport adUserDirectReport) {
			this.adUserDirectReport = adUserDirectReport;
		}
		
		public String getId() {
			return adUserDirectReport.getUserId();
		}
		
		public String getName() {
			return adUserDirectReport.getName();
		}
		public String getUsername() {
			return adUserDirectReport.getUsername();
		}
		public String getFirstname() {
			return adUserDirectReport.getFirstname();
		}
		public String getLastname() {
			return adUserDirectReport.getLastname();
		}
	}
	
	public class UserGroupBean{
		private AdUserGroup adUserGroup;
		
		public UserGroupBean(AdUserGroup adUserGroup) {
			this.adUserGroup = adUserGroup;
		}
		
		public String getDn() {
			return adUserGroup.getDn();
		}
		public String getName() {
			return adUserGroup.getName();
		}
	}
}
