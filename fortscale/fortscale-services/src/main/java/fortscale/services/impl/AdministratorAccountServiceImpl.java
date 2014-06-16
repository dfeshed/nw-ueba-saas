package fortscale.services.impl;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.services.AdministratorAccountService;

@Service("administratorAccountService")
public class AdministratorAccountServiceImpl implements AdministratorAccountService,InitializingBean{

	@Autowired
	private UserRepository userRepository;
	@Value("${user.list.admin_groups.path:}")
	private String filePath;

	private static Logger logger = LoggerFactory.getLogger(AdministratorAccountServiceImpl.class);

	private Set<String> adminUsers = new HashSet<String>();
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
		updateAdminList();
		updateUserTag();
	}

	private void updateAdminList() throws Exception {
		if(!StringUtils.isEmpty(getFilePath())){
			File f = new File(getFilePath());
			if(f.exists() && !f.isDirectory()) {
				adminGroups = FileUtils.readLines(new File(getFilePath()));
				List<User> adminUsersList = userRepository.findByUserInGroup(adminGroups);
				adminUsers = getUsernameList(adminUsersList);				
				if (adminUsersList ==null) {
					logger.warn("AdministratorGroups no users found in the user repository for groups {}",adminGroups);
				}
			}
			else {
				logger.warn("AdministratorGroups file not found in path: {}",getFilePath());
			}
		}
		else {
			logger.info("AdministratorGroups file path not configured");	
		}
	}

	private void updateUserTag() {
		if (adminUsers != null && adminUsers.size() !=0) {
			int pageSize = 100;		
			int numOfPages = (int) (((userRepository.count() -1) / pageSize) + 1); 
			for(int i = 0; i < numOfPages; i++){
				PageRequest pageRequest = new PageRequest(i, pageSize);
				for(User user: userRepository.findAll(pageRequest).getContent()){
					if (adminUsers.contains(user.getUsername())) {
						userRepository.updateAdministratorAccount(user, true);
					}
					else if (user.getAdministratorAccount() == null || user.getAdministratorAccount()) {
						userRepository.updateAdministratorAccount(user, false);
					}
				}
			}
		}
	}
	
	private void refreshAdminList() {
		List<User> adminUsersList = userRepository.findByAdministratorAccount(true);
		adminUsers = getUsernameList(adminUsersList);
	}
	
	private Set<String> getUsernameList(List<User> users) {
		Set<String> result = new HashSet<String>();
		if (users !=null) {
			result.clear();
			for (User user : users) {
				result.add(user.getUsername());
			}
		}
		return result;
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

	public void refresh() {
		refreshAdminList();
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

}
