package fortscale.services.impl;
import java.util.List;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.services.ExecutiveAccountService;

@Service("executiveAccountService")
public class ExecutiveAccountServiceImpl extends UserTaggingServiceAbstract implements ExecutiveAccountService,InitializingBean{
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
	public Boolean isUserTagged(User user){
		return user.getExecutiveAccount();
	}
	
	@Override
	public void updateUserTag(User user, boolean isTagTheUser){
		userRepository.updateExecutiveAccount(user, isTagTheUser);
	}
	
	public void refresh() {
		List<User> taggedUsersList= userRepository.findByExecutiveAccount(true);
		refreshTaggedUsers(taggedUsersList);
	}
	
}