package fortscale.services.impl;
import java.util.List;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.services.AdministratorAccountService;

@Service("administratorAccountService")
public class AdministratorAccountServiceImpl extends UserTaggingServiceAbstract implements AdministratorAccountService,InitializingBean{
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
	public Boolean isUserTagged(User user){
		return user.getAdministratorAccount();
	}
	
	public void refresh() {
		List<User> taggedUsersList= userRepository.findByAdministratorAccount(true);
		refreshTaggedUsers(taggedUsersList);
	}
	
}
