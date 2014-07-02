package fortscale.services.impl;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;

public abstract class HigePrivilegedServiceAbstract {
	private UserRepository userRepository;
	private Set<String> higePrivilegedUsers;
	private List<String> higePrivilegedGroups;
	private String higePrivilegedType;
	private String filePath;
	private Logger logger;
	

	// ------- Setters ----------
	public void setVariables(String higePrivilegedType, UserRepository userRepository,String filePath, Logger logger ){
		this.higePrivilegedType = higePrivilegedType;
		this.userRepository = userRepository;
		this.filePath = filePath;
		this.logger = logger;
	}

	// -------- Private functions ---------

	private String getFilePath() {
		return filePath;
	}
	
	
	/**
	 * This function is a mediator between the current high privileged generic account to the 
	 * real account according to it's type
	 */
	private void updateHigePrivilegedAccount(User user, boolean isHigePrivilegedAccount){
		switch (higePrivilegedType){
			case "administrator":
				userRepository.updateAdministratorAccount(user, isHigePrivilegedAccount);
				break;
			case "executive":
				userRepository.updateExecutiveAccount(user, isHigePrivilegedAccount);					
				break;
		}
	}
	
	
	
	/**
	 * This function run over the file groups and 
	 * if it runs with a group with no name (e.g: empty group - when the user just clicked ENTER in the vi)
	 * it removes it, the it return if the group is empty or not after the process
	 * @return
	 */
	private boolean isFileEmptyFromGroups(){
		if(higePrivilegedGroups.contains("")){
			higePrivilegedGroups.remove("");
		}
		if(higePrivilegedGroups.size() > 0){
			return false; // file is not empty
		}
		return true; // file is empty
	}
	
	
	/**
	 * This function creates a Set of user-names by a giving list of users.
	 * @param users
	 * @return
	 */
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

	/**
	 * This function read the file from it's destination which set in the configuration file and 
	 * - stores all the groups in: this.higePrivilegedGroups; 
	 * - stores all higePrivilegedUsers in: this.higePrivilegedUsers
	 * @throws Exception
	 */
	private void updateHigePrivilegedList() throws Exception {
		if(!StringUtils.isEmpty(getFilePath())){
			File f = new File(getFilePath());
			if(f.exists() && !f.isDirectory()) {
				this.higePrivilegedGroups = FileUtils.readLines(new File(getFilePath()));
				if(isFileEmptyFromGroups()){
					logger.warn("[Hige privileged groups ({})] file is Empty: {}",higePrivilegedType, getFilePath());
				}
				List<User> higePrivilegedUsersList = userRepository.findByUserInGroup(higePrivilegedGroups);
				higePrivilegedUsers = getUsernameList(higePrivilegedUsersList);				
				if (higePrivilegedUsersList ==null) {
					logger.warn("[Hige privileged groups ({})] no users found in the user repository for groups {}",higePrivilegedType ,higePrivilegedGroups);
				}
			}
			else {
				logger.warn("[Hige privileged groups ({})]  file not found in path: {}",higePrivilegedType,getFilePath());
			}
		}
		else {
			logger.info("[Hige privileged groups ({})] file path not configured",higePrivilegedType);	
		}
	}
	
	
	/**
	 * this function run over all the users and check if they belong to the 
	 * privileged group, if a user belong it flag his attribute as true,
	 * if it doesn't it flag it as false; 
	 */
	private void updateUserTag() {
		if (higePrivilegedUsers != null && higePrivilegedUsers.size() !=0) {
			int pageSize = 100;		
			int numOfPages = (int) (((userRepository.count() -1) / pageSize) + 1); 
			for(int i = 0; i < numOfPages; i++){
				PageRequest pageRequest = new PageRequest(i, pageSize);
				for(User user: userRepository.findAll(pageRequest).getContent()){
					if (higePrivilegedUsers.contains(user.getUsername())) {
						updateHigePrivilegedAccount(user, true);
					}
					else if (user.getHigePrivilegedAccount(higePrivilegedType) == null || user.getHigePrivilegedAccount(higePrivilegedType)) {
						updateHigePrivilegedAccount(user, false);
					}
				}
			}
		}else{
			resetAllUsers(higePrivilegedType);
		}
	}
	

	/**
	 * This function reset all users to false according to the higePrivileged type (administrator or executive)
	 * @param privilegedType
	 */
	private void resetAllUsers(String privilegedType){
		int pageSize = 100;		
		int numOfPages = (int) (((userRepository.count() -1) / pageSize) + 1); 
		for(int i = 0; i < numOfPages; i++){
			PageRequest pageRequest = new PageRequest(i, pageSize);
			for(User user: userRepository.findAll(pageRequest).getContent()){
				updateHigePrivilegedAccount(user,false);
			}
		}
	}
	
	
	// ---------- Public functions ----------
	public void refreshHigePrivilegedList(List<User> higePrivilegedUsersList) {
		higePrivilegedUsers = getUsernameList(higePrivilegedUsersList);
	}
	
	
	public void updateHigePrivilegedListsAndUserTag() throws Exception{
		updateHigePrivilegedList();
		updateUserTag();
	}
	
}


