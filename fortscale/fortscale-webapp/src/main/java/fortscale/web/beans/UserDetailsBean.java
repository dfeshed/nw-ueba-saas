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

	public List<String> getGroups() {
		List<String> ret = new ArrayList<>();
		for(AdUserGroup adUserGroup: user.getGroups()){
			ret.add(adUserGroup.getName());
		}
		return ret;
	}

	public String getDepartment() {
		return user.getDepartment();
	}
		
	public String getMobilePhone(){
		return user.getMobile();
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

	public UserManagerBean getManager() {
		UserManagerBean ret = null;
		if(manager != null){
			ret = new UserManagerBean();
			ret.setId(manager.getEmployeeID());
			ret.setName(manager.getFirstname() + " " + manager.getLastname());
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
		private String name = null;
		private String id = null;
		
		
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
	}
}
