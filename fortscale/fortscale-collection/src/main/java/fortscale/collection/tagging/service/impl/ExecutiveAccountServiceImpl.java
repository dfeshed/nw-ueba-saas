package fortscale.collection.tagging.service.impl;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import fortscale.collection.tagging.service.ExecutiveAccountService;
import fortscale.domain.core.User;

@Service("executiveAccountService")
public class ExecutiveAccountServiceImpl extends UserTaggingServiceAbstract implements ExecutiveAccountService{
	@Value("${user.list.executive_groups.path:}")
	private String filePath;
	
	private String tagName = "executive";
	
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

	public boolean isUserExecutive(String username) {
		return isUserTagged(username);
	}
	
	@Override
	public boolean isUserTagged(User user){
		return user.getExecutiveAccount() != null ? user.getExecutiveAccount() : false;
	}
	
	@Override
	public void updateUserTag(User user, boolean isTagTheUser){
		userRepository.updateExecutiveAccount(user, isTagTheUser);
	}
	
	@Override
	protected List<User> findTaggedUsersFromDb(){
		return userRepository.findByExecutiveAccount(true);
	}
	
}