package fortscale.services.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.services.ExecutiveAccountService;

@Service("executiveAccountService")
public class ExecutiveAccountServiceImpl extends HigePrivilegedServiceAbstract implements ExecutiveAccountService,InitializingBean{

	@Autowired
	private UserRepository userRepository;
	private Set<String> executiveUsers = new HashSet<String>();
	
	@Value("${user.list.executive_groups.path:}")
	private String filePath;
	private String higePrivilegedType = "executive";
	private Logger logger = LoggerFactory.getLogger(ExecutiveAccountServiceImpl.class); 

	@Override 
	public boolean isUserExecutive(String username) {
		if (executiveUsers !=  null) {
			return executiveUsers.contains(username);
		}
		else{
			return false;
		}
	}

	
	@Override
	public void afterPropertiesSet() throws Exception {
		super.setVariables(higePrivilegedType, userRepository, filePath, logger);
		super.updateHigePrivilegedListsAndUserTag();
	}
	

	@Override
	public void update() {
		try {
			afterPropertiesSet();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}
	}
	

	@Override
	public void refresh() {
		List<User> executiveUsersList= userRepository.findByExecutiveAccount(true);
		super.refreshHigePrivilegedList(executiveUsersList);
	}
	
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

}