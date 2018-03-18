package fortscale.web.beans;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import fortscale.domain.core.Alert;
import fortscale.domain.core.User;
import fortscale.services.UserServiceFacade;
import fortscale.utils.logging.Logger;

import java.io.Serializable;
import java.util.List;

public class UserDetailsBean implements Serializable{
	private static Logger logger = Logger.getLogger(UserDetailsBean.class);
	
	private static final long serialVersionUID = 1L;

    @JsonUnwrapped
	private User user;


	private User manager;
	private List<User> directReports;
	private String thumbnailPhoto;
	private UserServiceFacade userServiceFacade;

	private List<Alert> alerts;
//	private List<UserActivityData.DeviceEntry> devices;

	public UserDetailsBean(User user, User manager, List<User> directReports, UserServiceFacade userServiceFacade){
		this.user = user;
		this.manager = manager;
		this.directReports = directReports;
		this.userServiceFacade = userServiceFacade;
	}

	public List<Alert> getAlerts() {
		return alerts;
	}

	public void setAlerts(List<Alert> alerts) {
		this.alerts = alerts;
	}

	public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


		
	public void setThumbnailPhoto(String thumbnailPhoto) {
		this.thumbnailPhoto = thumbnailPhoto;
	}
	

	public String getOu(){
	    return userServiceFacade.getOu(user);
	}

//	public String getAdUserPrincipalName(){
//		return user.getAdInfo().getUserPrincipalName();
//	}
//
//	public String getSAMAcountName(){
//		return user.getAdInfo().getsAMAccountName();
//	}
//
//	public Long getAdAcountExpires(){
//		return user.getAdInfo().getAccountExpires() != null ? user.getAdInfo().getAccountExpires().getTime() : null;
//	}

//	public Long getAdDisabledOn(){
//		return user.getAdInfo().getDisableAccountTime() != null ? user.getAdInfo().getDisableAccountTime().toDate().getTime() : null;
//	}
//
//	public String getLogonHours(){
//		return user.getAdInfo().getLogonHours();
//	}
//
//	public Long getAdWhenChanged(){
//		return user.getAdInfo().getWhenChanged() != null ? user.getAdInfo().getWhenChanged().getTime() : null;
//	}
//
//	public Long getAdWhenCreated(){
//		return user.getAdInfo().getWhenCreated() != null ? user.getAdInfo().getWhenCreated().getTime() : null;
//	}
//
//	public String getDescription(){
//		return user.getAdInfo().getDescription();
//	}
//
//	public String getStreetAddress(){
//		return user.getAdInfo().getStreetAddress();
//	}
//
//	public String getCompany(){
//		return user.getAdInfo().getCompany();
//	}
//
//	public String getAdC(){
//		return user.getAdInfo().getC();
//	}
//
//	public String getDivision(){
//		return user.getAdInfo().getDivision();
//	}
//
//	public String getAdL(){
//		return user.getAdInfo().getL();
//	}
//
//	public String getAdO(){
//		return user.getAdInfo().getO();
//	}
//
//	public String getRoomNumber(){
//		return user.getAdInfo().getRoomNumber();
//	}
//
//	public Boolean isAccountIsDisabled() {
//		return user.getAdInfo().getIsAccountDisabled();
//	}
//
//	public Boolean isLockout() {
//		try{
//			return user.getAdInfo().getUserAccountControl() != null ? adUserParser.isLockout(user.getAdInfo().getUserAccountControl()) : null;
//		} catch (NumberFormatException e) {
//			logger.warn("got NumberFormatException while trying to parse user account control.", user.getAdInfo().getUserAccountControl());
//		}
//
//		return null;
//	}
//
//	public Boolean isWorkstationTrustAccount() {
//		try{
//			return user.getAdInfo().getUserAccountControl() != null ? adUserParser.isWorkstationTrustAccount(user.getAdInfo().getUserAccountControl()) : null;
//		} catch (NumberFormatException e) {
//			logger.warn("got NumberFormatException while trying to parse user account control.", user.getAdInfo().getUserAccountControl());
//		}
//
//		return null;
//	}
//
//
//	public Boolean isServerTrustAccount() {
//		try{
//			return user.getAdInfo().getUserAccountControl() != null ? adUserParser.isServerTrustAccount(user.getAdInfo().getUserAccountControl()) : null;
//		} catch (NumberFormatException e) {
//			logger.warn("got NumberFormatException while trying to parse user account control.", user.getAdInfo().getUserAccountControl());
//		}
//
//		return null;
//	}
//
//	public Boolean isSmartcardRequired() {
//		try{
//			return user.getAdInfo().getUserAccountControl() != null ? adUserParser.isSmartcardRequired(user.getAdInfo().getUserAccountControl()) : null;
//		} catch (NumberFormatException e) {
//			logger.warn("got NumberFormatException while trying to parse user account control.", user.getAdInfo().getUserAccountControl());
//		}
//
//		return null;
//	}
//
//
//	public Boolean isTrustedForDelegation() {
//		try{
//			return user.getAdInfo().getUserAccountControl() != null ? adUserParser.isTrustedForDelegation(user.getAdInfo().getUserAccountControl()) : null;
//		} catch (NumberFormatException e) {
//			logger.warn("got NumberFormatException while trying to parse user account control.", user.getAdInfo().getUserAccountControl());
//		}
//
//		return null;
//	}
//
//	public Boolean isNotDelegated() {
//		try{
//			return user.getAdInfo().getUserAccountControl() != null ? adUserParser.isNotDelegated(user.getAdInfo().getUserAccountControl()) : null;
//		} catch (NumberFormatException e) {
//			logger.warn("got NumberFormatException while trying to parse user account control.", user.getAdInfo().getUserAccountControl());
//		}
//
//		return null;
//	}
//
//	public Boolean isPasswordExpired() {
//		return userServiceFacade.isPasswordExpired(user);
//	}
//
//	public Boolean isTrustedToAuthForDelegation() {
//		try{
//			return user.getAdInfo().getUserAccountControl() != null ? adUserParser.isTrustedToAuthForDelegation(user.getAdInfo().getUserAccountControl()) : null;
//		} catch (NumberFormatException e) {
//			logger.warn("got NumberFormatException while trying to parse user account control.", user.getAdInfo().getUserAccountControl());
//		}
//
//		return null;
//	}

	
	public Boolean isNoPasswordRequiresValue() {
		return userServiceFacade.isNoPasswordRequiresValue(user);
	}

	
	public Boolean isNormalUserAccountValue() {
		return userServiceFacade.isNormalUserAccountValue(user);
	} 
	
	
//	public Boolean isInterdomainTrustAccountValue() {
//		try{
//			return user.getAdInfo().getUserAccountControl() != null ? adUserParser.isInterdomainTrustAccountValue(user.getAdInfo().getUserAccountControl()) : null;
//		} catch (NumberFormatException e) {
//			logger.warn("got NumberFormatException while trying to parse user account control.", user.getAdInfo().getUserAccountControl());
//		}
//
//		return null;
//	}

	
	public Boolean isPasswordNeverExpiresValue() {
		return userServiceFacade.isPasswordNeverExpiresValue(user);
	}
	
	
//	public Boolean isDesKeyOnlyValue() {
//		try{
//			return user.getAdInfo().getUserAccountControl() != null ? adUserParser.isDesKeyOnlyValue(user.getAdInfo().getUserAccountControl()) : null;
//		} catch (NumberFormatException e) {
//			logger.warn("got NumberFormatException while trying to parse user account control.", user.getAdInfo().getUserAccountControl());
//		}
//
//		return null;
//	}
	
//	public List<UserDirectReportBean> getUserDirectReports() {
//		List<UserDirectReportBean> ret = new ArrayList<>();
//		for(User user: directReports){
//			ret.add(new UserDirectReportBean(user));
//		}
//		return ret;
//	}

//	public List<UserGroupBean> getGroups() {
//		List<UserGroupBean> ret = new ArrayList<>();
//		for(AdUserGroup adUserGroup: user.getAdInfo().getGroups()){
//			ret.add(new UserGroupBean(adUserGroup));
//		}
//		return ret;
//	}
//
//	public String getDepartment() {
//		return user.getAdInfo().getDepartment();
//	}
//
//	public String getEmail(){
//		if(user.getAdInfo().getEmailAddress() == null){
//			return null;
//		}
//		return user.getAdInfo().getEmailAddress().toString();
//	}
//
//	public String getPhone(){
//		return user.getAdInfo().getTelephoneNumber();
//	}
//
//	public String getMobilePhone(){
//		return user.getAdInfo().getMobile();
//	}
//
//	public String getOtherFacsimileTelephoneNumber() {
//		return user.getAdInfo().getOtherFacsimileTelephoneNumber();
//	}
//
//	public String getOtherHomePhone() {
//		return user.getAdInfo().getOtherHomePhone();
//	}
//
//
//	public String getHomePhone() {
//		return user.getAdInfo().getHomePhone();
//	}
//
//	public String getOtherMobile() {
//		return user.getAdInfo().getOtherMobile();
//	}
//
//
//	public String getOtherTelephone() {
//		return user.getAdInfo().getOtherTelephone();
//	}
	
	public String getImage() {
		return thumbnailPhoto;
	}



	

}
