package fortscale.web.beans;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

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


	public String getName() {
		return user.getFirstname() + " " + user.getLastname();
	}

	public String getPosition() {
		return null;
	}

	public String getId() {
		return user.getEmployeeID();
	}

	public String getGroups() {
		return null;
	}

	public String getDepartment() {
		return null;
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
