package fortscale.collection.tagging.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import fortscale.collection.tagging.service.UserTagEnum;
import fortscale.domain.core.User;

@Service("executiveAccountService")
public class ExecutiveAccountServiceImpl extends UserTagServiceAbstract{
	@Value("${user.list.executive_groups.path:}")
	private String filePath;
	
	private UserTagEnum tag = UserTagEnum.executive;
	
	@Override
	public String getFilePath(){
		return filePath;
	}
	
	@Override
	public UserTagEnum getTag(){
		return tag;
	}
	
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public boolean isUserExecutive(String username) {
		return isUserTagged(username);
	}
	
	@Override
	public void updateUserTag(String username, boolean isTagTheUser){
		userRepository.updateUserTag(User.executiveAccountField, username, isTagTheUser);
	}

	@Override
	public String getTagMongoField() {
		return User.executiveAccountField;
	}
	
}