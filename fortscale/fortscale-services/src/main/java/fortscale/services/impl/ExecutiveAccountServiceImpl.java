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
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.services.ExecutiveAccountService;

@Service("executiveAccountService")
public class ExecutiveAccountServiceImpl implements ExecutiveAccountService,InitializingBean{

	@Autowired
	private UserRepository userRepository;
	@Value("${user.list.executive_groups.path:}")
	private String filePath;

	private static Logger logger = LoggerFactory.getLogger(ExecutiveAccountServiceImpl.class);

	private Set<String> executiveUsers = new HashSet<String>();
	private List<String> executiveGroups = null;

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
		updateExsecutvieList();
		updateUserTag();
	}

	private void updateExsecutvieList() throws Exception {
		if(!StringUtils.isEmpty(getFilePath())){
			File f = new File(getFilePath());
			if(f.exists() && !f.isDirectory()) {
				executiveGroups = FileUtils.readLines(new File(getFilePath()));
				if(isFileEmptyFromGroups()){
					logger.warn("ExecutiveGroups file is Empty: {}",getFilePath());
				}
				List<User> executiveUsersList = userRepository.findByUserInGroup(executiveGroups);
				executiveUsers = getUsernameList(executiveUsersList);				
				if (executiveUsersList ==null) {
					logger.warn("ExecutiveGroups no users found in the user repository for groups {}",executiveGroups);
				}
			}
			else {
				logger.warn("ExecutiveGroups file not found in path: {}",getFilePath());
				
			}
		}
		else {
			logger.info("ExecutiveGroups file path not configured");	
		}
	}
	
	private boolean isFileEmptyFromGroups(){
		if(executiveGroups.contains("")){
			executiveGroups.remove("");
		}
		if(executiveGroups.size() > 0){
			return false; // file is not empty
		}
		return true; // file is empty
	}
	
	private void resetAllUsers(){
		int pageSize = 100;		
		int numOfPages = (int) (((userRepository.count() -1) / pageSize) + 1); 
		for(int i = 0; i < numOfPages; i++){
			PageRequest pageRequest = new PageRequest(i, pageSize);
			for(User user: userRepository.findAll(pageRequest).getContent()){
					userRepository.updateExecutiveAccount(user, false);
			}
		}
	}

	private void updateUserTag() {
		if (executiveUsers != null && executiveUsers.size() !=0) {
			int pageSize = 100;		
			int numOfPages = (int) (((userRepository.count() -1) / pageSize) + 1); 
			for(int i = 0; i < numOfPages; i++){
				PageRequest pageRequest = new PageRequest(i, pageSize);
				for(User user: userRepository.findAll(pageRequest).getContent()){
					if (executiveUsers.contains(user.getUsername())) {
						userRepository.updateExecutiveAccount(user, true);
					}
					else if (user.getExecutiveAccount() == null || user.getExecutiveAccount()) {
						userRepository.updateExecutiveAccount(user, false);
					}
				}
			}
		}else{
			resetAllUsers();
		}
	}
	
	private void refreshExecutiveList() {
		List<User> executiveUsersList = userRepository.findByExecutiveAccount(true);
		executiveUsers = getUsernameList(executiveUsersList);
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

	@Override
	public void refresh() {
		refreshExecutiveList();
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

}