package fortscale.services.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import fortscale.services.UserTagEnum;
import fortscale.domain.core.User;

@Service("administratorAccountService")
public class AdministratorAccountServiceImpl extends UserTagServiceAbstract{
	@Value("${user.list.admin_groups.path:}")
	private String filePath;
	private UserTagEnum tag = UserTagEnum.admin;
	
	@Override
	public String getFilePath(){
		return filePath;
	}
	
	@Override
	public UserTagEnum getTag(){
		return tag;
	}
	
	@Override
	public String getTagMongoField(){
		return User.administratorAccountField;
	}
	
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public boolean isUserAdministrator(String username) {
		return isUserTagged(username, tag.getId());
	}

}
