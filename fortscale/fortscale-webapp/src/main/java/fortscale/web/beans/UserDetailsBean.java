package fortscale.web.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import fortscale.domain.ad.AdUserGroup;
import fortscale.domain.core.ApplicationUserDetails;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;

@Configurable
public class UserDetailsBean implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Autowired
	private UserRepository userRepository;

	private User user;
	private User manager;
	
	public UserDetailsBean(User user, User manager){
		this.user = user;
		this.manager = manager;
	}
	
	public String getUsername() {
		return user.getAdUserPrincipalName();
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

	public String getWorkerId() {
		return user.getEmployeeID();
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
		
		
		public String getName() {
			return String.format("%s %s", managerUser.getFirstname(), managerUser.getLastname());
		}
		public String getWorkerId() {
			return managerUser.getEmployeeID();
		}
		public String getUsername() {
			return managerUser.getAdUserPrincipalName();
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
