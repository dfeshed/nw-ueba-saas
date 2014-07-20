package fortscale.collection.tagging.service.impl;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import fortscale.collection.tagging.service.AdministratorAccountService;
import fortscale.domain.core.User;

@Service("administratorAccountService")
public class AdministratorAccountServiceImpl extends UserTaggingServiceAbstract implements AdministratorAccountService{
	@Value("${user.list.admin_groups.path:}")
	private String filePath;
	private String tagName = "administrator";
	
	@Override
	public String getFilePath(){
		return filePath;
	}
	
	@Override
	public String getTagName(){
		return tagName;
	}
	
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public boolean isUserAdministrator(String username) {
		return isUserTagged(username);
	}
	
	@Override
	public void updateUserTag(User user, boolean isTagTheUser){
		userRepository.updateAdministratorAccount(user, isTagTheUser);
	}
	
	@Override
	public boolean isUserTagged(User user){
		return user.getAdministratorAccount() != null ? user.getAdministratorAccount() : false;
	}
	
	@Override
	protected List<User> findTaggedUsersFromDb(){
		return userRepository.findByAdministratorAccount(true);
	}	
}
