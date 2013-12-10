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
		if(user.getFirstname() != null && user.getLastname() != null){
			return user.getFirstname() + " " + user.getLastname();
		} else if(user.getAdDisplayName() != null){
			return user.getAdDisplayName();
		} else{
			return user.getUsername();
		}
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
		
	public Boolean isAccountIsDisabled() {
		try {
			return user.getAdUserAccountControl() != null ? adUserParser.isAccountIsDisabled(user.getAdUserAccountControl()) : null;
		} catch (NumberFormatException e) {
			logger.warn("got NumberFormatException while trying to parse user account control.", user.getAdUserAccountControl());
		}
			
		return null;
	}
	
	public Boolean isLockout() {
		try{
			return user.getAdUserAccountControl() != null ? adUserParser.isLockout(user.getAdUserAccountControl()) : null;
		} catch (NumberFormatException e) {
			logger.warn("got NumberFormatException while trying to parse user account control.", user.getAdUserAccountControl());
		}
			
		return null;
	}
	
	public Boolean isWorkstationTrustAccount() {
		try{
			return user.getAdUserAccountControl() != null ? adUserParser.isWorkstationTrustAccount(user.getAdUserAccountControl()) : null;
		} catch (NumberFormatException e) {
			logger.warn("got NumberFormatException while trying to parse user account control.", user.getAdUserAccountControl());
		}
			
		return null;
	}
	
	
	public Boolean isServerTrustAccount() {
		try{
			return user.getAdUserAccountControl() != null ? adUserParser.isServerTrustAccount(user.getAdUserAccountControl()) : null;
		} catch (NumberFormatException e) {
			logger.warn("got NumberFormatException while trying to parse user account control.", user.getAdUserAccountControl());
		}
			
		return null;
	}
	
	public Boolean isSmartcardRequired() {
		try{
			return user.getAdUserAccountControl() != null ? adUserParser.isSmartcardRequired(user.getAdUserAccountControl()) : null;
		} catch (NumberFormatException e) {
			logger.warn("got NumberFormatException while trying to parse user account control.", user.getAdUserAccountControl());
		}
			
		return null;
	}
	
	
	public Boolean isTrustedForDelegation() {
		try{
			return user.getAdUserAccountControl() != null ? adUserParser.isTrustedForDelegation(user.getAdUserAccountControl()) : null;
		} catch (NumberFormatException e) {
			logger.warn("got NumberFormatException while trying to parse user account control.", user.getAdUserAccountControl());
		}
			
		return null;
	}
	
	public Boolean isNotDelegated() {
		try{
			return user.getAdUserAccountControl() != null ? adUserParser.isNotDelegated(user.getAdUserAccountControl()) : null;
		} catch (NumberFormatException e) {
			logger.warn("got NumberFormatException while trying to parse user account control.", user.getAdUserAccountControl());
		}
			
		return null;
	}
	
	public Boolean isPasswordExpired() {
		try{
			return user.getAdUserAccountControl() != null ? adUserParser.isPasswordExpired(user.getAdUserAccountControl()) : null;
		} catch (NumberFormatException e) {
			logger.warn("got NumberFormatException while trying to parse user account control.", user.getAdUserAccountControl());
		}
			
		return null;
	}
	
	public Boolean isTrustedToAuthForDelegation() {
		try{
			return user.getAdUserAccountControl() != null ? adUserParser.isTrustedToAuthForDelegation(user.getAdUserAccountControl()) : null;
		} catch (NumberFormatException e) {
			logger.warn("got NumberFormatException while trying to parse user account control.", user.getAdUserAccountControl());
		}
			
		return null;
	}

	
	public Boolean isNoPasswordRequiresValue() {
		try{
			return user.getAdUserAccountControl() != null ? adUserParser.isNoPasswordRequiresValue(user.getAdUserAccountControl()) : null;
		} catch (NumberFormatException e) {
			logger.warn("got NumberFormatException while trying to parse user account control.", user.getAdUserAccountControl());
		}
			
		return null;
	}

	
	public Boolean isNormalUserAccountValue() {
		try{
			return user.getAdUserAccountControl() != null ? adUserParser.isNormalUserAccountValue(user.getAdUserAccountControl()) : null;
		} catch (NumberFormatException e) {
			logger.warn("got NumberFormatException while trying to parse user account control.", user.getAdUserAccountControl());
		}
			
		return null;
	} 
	
	
	public Boolean isInterdomainTrustAccountValue() {
		try{
			return user.getAdUserAccountControl() != null ? adUserParser.isInterdomainTrustAccountValue(user.getAdUserAccountControl()) : null;
		} catch (NumberFormatException e) {
			logger.warn("got NumberFormatException while trying to parse user account control.", user.getAdUserAccountControl());
		}
			
		return null;
	}

	
	public Boolean isPasswordNeverExpiresValue() {
		try{
			return user.getAdUserAccountControl() != null ? adUserParser.isPasswordNeverExpiresValue(user.getAdUserAccountControl()) : null;
		} catch (NumberFormatException e) {
			logger.warn("got NumberFormatException while trying to parse user account control.", user.getAdUserAccountControl());
		}
			
		return null;
	}
	
	
	public Boolean isDesKeyOnlyValue() {
		try{
			return user.getAdUserAccountControl() != null ? adUserParser.isDesKeyOnlyValue(user.getAdUserAccountControl()) : null;
		} catch (NumberFormatException e) {
			logger.warn("got NumberFormatException while trying to parse user account control.", user.getAdUserAccountControl());
		}
			
		return null;
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
