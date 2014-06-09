package fortscale.services.impl;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.services.AdministratorAccountService;

@Service("administratorAccountService")
public class AdministratorAccountServiceImpl implements AdministratorAccountService,InitializingBean{

	@Autowired
	private UserRepository userRepository;
	@Value("${user.list.admin_groups.path:}")
	public String filePath;

	private static Logger logger = LoggerFactory.getLogger(AdministratorAccountServiceImpl.class);

	private Set<String> adminUsers = null;
	private List<String> adminGroups = null;

	@Override
	public boolean isUserAdministrator(String username) {
		if (adminUsers !=  null) {
			return adminUsers.contains(username);
		}
		else{
			return false;
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if(!StringUtils.isEmpty(filePath)){
			File f = new File(filePath);
			if(f.exists() && !f.isDirectory()) {
				adminGroups = FileUtils.readLines(new File(filePath));
				List<User> adminUsersList = userRepository.findByUserInGroup(adminGroups);
				for (User user : adminUsersList) {
					adminUsers.add(user.getUsername());
				}
			}
			else {
				logger.warn("AdministratorGroups file not found in path: {}",filePath);
			}
		}
		else {
			logger.info("AdministratorGroups file path not configured");	
		}			
	}

}
