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
import fortscale.utils.logging.Logger;

@Configurable(preConstruction = true, autowire=Autowire.BY_NAME, dependencyCheck=true)
public class UserDetailsBean implements Serializable{
	private static Logger logger = Logger.getLogger(UserDetailsBean.class);
	
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
	
	public String getId(){
		return user.getId();
	}
	
	public boolean getFollowed(){
		return user.getFollowed();
	}
	
	public String getUsername() {
		return user.getUsername();
	}
	
	public Map<String, ApplicationUserDetails> getApplicationUserDetails() {
		return user.getApplicationUserDetails();
	}


	public String getName() {
		if(user.getAdInfo().getFirstname() != null && user.getAdInfo().getLastname() != null){
			return user.getAdInfo().getFirstname() + " " + user.getAdInfo().getLastname();
		} else if(user.getAdInfo().getDisplayName() != null){
			return user.getAdInfo().getDisplayName();
		} else{
			return user.getUsername();
		}
	}

	public String getJobTitle() {
		return user.getAdInfo().getPosition();
	}

	public String getAdEmployeeID() {
		return user.getAdInfo().getEmployeeID();
	}
	
	public String getAdEmployeeNumber() {
		return user.getAdInfo().getEmployeeNumber();
	}
	
	public String getAdDisplayName(){
		return user.getAdInfo().getDisplayName();
	}
	
	public String getOu(){
		return adUserParser.parseOUFromDN(user.getAdInfo().getDn());
	}
	
	public String getAdUserPrincipalName(){
		return user.getAdInfo().getUserPrincipalName();
	}
	
	public String getSAMAcountName(){
		return user.getAdInfo().getsAMAccountName();
	}
	
	public Long getAdAcountExpires(){
		return user.getAdInfo().getAccountExpires() != null ? user.getAdInfo().getAccountExpires().getTime() : null;
	}
	
	public String getLogonHours(){
		return user.getAdInfo().getLogonHours();
	}
	
	public Long getAdWhenChanged(){
		return user.getAdInfo().getWhenChanged() != null ? user.getAdInfo().getWhenChanged().getTime() : null;
	}
	
	public Long getAdWhenCreated(){
		return user.getAdInfo().getWhenCreated() != null ? user.getAdInfo().getWhenCreated().getTime() : null;
	}
	
	public String getDescription(){
		return user.getAdInfo().getDescription();
	}
	
	public String getStreetAddress(){
		return user.getAdInfo().getStreetAddress();
	}
	
	public String getCompany(){
		return user.getAdInfo().getCompany();
	}
	
	public String getAdC(){
		return user.getAdInfo().getC();
	}
	
	public String getDivision(){
		return user.getAdInfo().getDivision();
	}
	
	public String getAdL(){
		return user.getAdInfo().getL();
	}
	
	public String getAdO(){
		return user.getAdInfo().getO();
	}
	
	public String getRoomNumber(){
		return user.getAdInfo().getRoomNumber();
	}
		
	public Boolean isAccountIsDisabled() {
		try {
			return user.getAdInfo().getUserAccountControl() != null ? adUserParser.isAccountIsDisabled(user.getAdInfo().getUserAccountControl()) : null;
		} catch (NumberFormatException e) {
			logger.warn("got NumberFormatException while trying to parse user account control.", user.getAdInfo().getUserAccountControl());
		}
			
		return null;
	}
	
	public Boolean isLockout() {
		try{
			return user.getAdInfo().getUserAccountControl() != null ? adUserParser.isLockout(user.getAdInfo().getUserAccountControl()) : null;
		} catch (NumberFormatException e) {
			logger.warn("got NumberFormatException while trying to parse user account control.", user.getAdInfo().getUserAccountControl());
		}
			
		return null;
	}
	
	public Boolean isWorkstationTrustAccount() {
		try{
			return user.getAdInfo().getUserAccountControl() != null ? adUserParser.isWorkstationTrustAccount(user.getAdInfo().getUserAccountControl()) : null;
		} catch (NumberFormatException e) {
			logger.warn("got NumberFormatException while trying to parse user account control.", user.getAdInfo().getUserAccountControl());
		}
			
		return null;
	}
	
	
	public Boolean isServerTrustAccount() {
		try{
			return user.getAdInfo().getUserAccountControl() != null ? adUserParser.isServerTrustAccount(user.getAdInfo().getUserAccountControl()) : null;
		} catch (NumberFormatException e) {
			logger.warn("got NumberFormatException while trying to parse user account control.", user.getAdInfo().getUserAccountControl());
		}
			
		return null;
	}
	
	public Boolean isSmartcardRequired() {
		try{
			return user.getAdInfo().getUserAccountControl() != null ? adUserParser.isSmartcardRequired(user.getAdInfo().getUserAccountControl()) : null;
		} catch (NumberFormatException e) {
			logger.warn("got NumberFormatException while trying to parse user account control.", user.getAdInfo().getUserAccountControl());
		}
			
		return null;
	}
	
	
	public Boolean isTrustedForDelegation() {
		try{
			return user.getAdInfo().getUserAccountControl() != null ? adUserParser.isTrustedForDelegation(user.getAdInfo().getUserAccountControl()) : null;
		} catch (NumberFormatException e) {
			logger.warn("got NumberFormatException while trying to parse user account control.", user.getAdInfo().getUserAccountControl());
		}
			
		return null;
	}
	
	public Boolean isNotDelegated() {
		try{
			return user.getAdInfo().getUserAccountControl() != null ? adUserParser.isNotDelegated(user.getAdInfo().getUserAccountControl()) : null;
		} catch (NumberFormatException e) {
			logger.warn("got NumberFormatException while trying to parse user account control.", user.getAdInfo().getUserAccountControl());
		}
			
		return null;
	}
	
	public Boolean isPasswordExpired() {
		try{
			return user.getAdInfo().getUserAccountControl() != null ? adUserParser.isPasswordExpired(user.getAdInfo().getUserAccountControl()) : null;
		} catch (NumberFormatException e) {
			logger.warn("got NumberFormatException while trying to parse user account control.", user.getAdInfo().getUserAccountControl());
		}
			
		return null;
	}
	
	public Boolean isTrustedToAuthForDelegation() {
		try{
			return user.getAdInfo().getUserAccountControl() != null ? adUserParser.isTrustedToAuthForDelegation(user.getAdInfo().getUserAccountControl()) : null;
		} catch (NumberFormatException e) {
			logger.warn("got NumberFormatException while trying to parse user account control.", user.getAdInfo().getUserAccountControl());
		}
			
		return null;
	}

	
	public Boolean isNoPasswordRequiresValue() {
		try{
			return user.getAdInfo().getUserAccountControl() != null ? adUserParser.isNoPasswordRequiresValue(user.getAdInfo().getUserAccountControl()) : null;
		} catch (NumberFormatException e) {
			logger.warn("got NumberFormatException while trying to parse user account control.", user.getAdInfo().getUserAccountControl());
		}
			
		return null;
	}

	
	public Boolean isNormalUserAccountValue() {
		try{
			return user.getAdInfo().getUserAccountControl() != null ? adUserParser.isNormalUserAccountValue(user.getAdInfo().getUserAccountControl()) : null;
		} catch (NumberFormatException e) {
			logger.warn("got NumberFormatException while trying to parse user account control.", user.getAdInfo().getUserAccountControl());
		}
			
		return null;
	} 
	
	
	public Boolean isInterdomainTrustAccountValue() {
		try{
			return user.getAdInfo().getUserAccountControl() != null ? adUserParser.isInterdomainTrustAccountValue(user.getAdInfo().getUserAccountControl()) : null;
		} catch (NumberFormatException e) {
			logger.warn("got NumberFormatException while trying to parse user account control.", user.getAdInfo().getUserAccountControl());
		}
			
		return null;
	}

	
	public Boolean isPasswordNeverExpiresValue() {
		try{
			return user.getAdInfo().getUserAccountControl() != null ? adUserParser.isPasswordNeverExpiresValue(user.getAdInfo().getUserAccountControl()) : null;
		} catch (NumberFormatException e) {
			logger.warn("got NumberFormatException while trying to parse user account control.", user.getAdInfo().getUserAccountControl());
		}
			
		return null;
	}
	
	
	public Boolean isDesKeyOnlyValue() {
		try{
			return user.getAdInfo().getUserAccountControl() != null ? adUserParser.isDesKeyOnlyValue(user.getAdInfo().getUserAccountControl()) : null;
		} catch (NumberFormatException e) {
			logger.warn("got NumberFormatException while trying to parse user account control.", user.getAdInfo().getUserAccountControl());
		}
			
		return null;
	}
	
	public List<UserDirectReportBean> getUserDirectReports() {
		List<UserDirectReportBean> ret = new ArrayList<>();
		for(AdUserDirectReport adUserDirectReport: user.getAdInfo().getDirectReports()){
			ret.add(new UserDirectReportBean(adUserDirectReport));
		}
		return ret;
	}

	public List<UserGroupBean> getGroups() {
		List<UserGroupBean> ret = new ArrayList<>();
		for(AdUserGroup adUserGroup: user.getAdInfo().getGroups()){
			ret.add(new UserGroupBean(adUserGroup));
		}
		return ret;
	}

	public String getDepartment() {
		return user.getAdInfo().getDepartment();
	}
	
	public String getEmail(){
		if(user.getAdInfo().getEmailAddress() == null){
			return null;
		}
		return user.getAdInfo().getEmailAddress().toString();
	}
	
	public String getPhone(){
		return user.getAdInfo().getTelephoneNumber();
	}
	
	public String getMobilePhone(){
		return user.getAdInfo().getMobile();
	}
	
	public String getOtherFacsimileTelephoneNumber() {
		return user.getAdInfo().getOtherFacsimileTelephoneNumber();
	}

	public String getOtherHomePhone() {
		return user.getAdInfo().getOtherHomePhone();
	}


	public String getHomePhone() {
		return user.getAdInfo().getHomePhone();
	}

	public String getOtherMobile() {
		return user.getAdInfo().getOtherMobile();
	}


	public String getOtherTelephone() {
		return user.getAdInfo().getOtherTelephone();
	}
	
	public String getImage() {
		return user.getAdInfo().getThumbnailPhoto();
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
			if(managerUser.getAdInfo().getFirstname() != null && managerUser.getAdInfo().getLastname() != null){
				return managerUser.getAdInfo().getFirstname() + " " + managerUser.getAdInfo().getLastname();
			} else if(managerUser.getAdInfo().getDisplayName() != null){
				return managerUser.getAdInfo().getDisplayName();
			} else{
				return managerUser.getUsername();
			}
		}
		public String getWorkerId() {
			return managerUser.getAdInfo().getEmployeeID();
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
