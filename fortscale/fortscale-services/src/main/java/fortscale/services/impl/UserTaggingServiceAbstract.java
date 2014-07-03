package fortscale.services.impl;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.PageRequest;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.utils.logging.Logger;

public abstract class UserTaggingServiceAbstract {
	private static Logger logger = Logger.getLogger(UserTaggingServiceAbstract.class);
	
	private List<String> groupsToTag;
	private Set<String> taggedUsers = new HashSet<String>();
	
	
	// -------- abstract functions ---------
	/**
	 * This functions are being implemented in the child service 
	 */
	protected abstract String getFilePath();
	protected abstract void updateUserTag(User user, boolean isTagTheUser);
	protected abstract String getTagName();
	protected abstract UserRepository getUserRepository();
	protected abstract Boolean isUserTagged(User user);
	
	
	/**
	 * This function run over the file groups and 
	 * if it runs with a group with no name (e.g: empty group - when the user just clicked ENTER in the vi)
	 * it removes it, the it return if the group is empty or not after the process
	 * @return
	 */
	private boolean isFileEmptyFromGroups(){
		if(groupsToTag.contains("")){
			groupsToTag.remove("");
		}
		if(groupsToTag.size() > 0){
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
	 * - stores all the groups in: this.groupsToTag; 
	 * - stores all taggedUsers in: this.taggedUsers
	 * @throws Exception
	 */
	private void updateTaggedUsersList() throws Exception {
		String filePath =  getFilePath();
		if(!StringUtils.isEmpty(filePath)){
			File f = new File(filePath);
			if(f.exists() && !f.isDirectory()) {
				groupsToTag = FileUtils.readLines(new File(filePath));
				if(isFileEmptyFromGroups()){
					logger.warn("Users Tagging [{}] file is Empty: {}",getTagName(), filePath);
				}
				List<User> taggedUsersList = getUserRepository().findByUserInGroup(groupsToTag);
				taggedUsers = getUsernameList(taggedUsersList);				
				if (taggedUsersList ==null) {
					logger.warn("Users Tagging [{}] no users found in the user repository for groups {}",getTagName() ,groupsToTag);
				}
			}
			else {
				logger.warn("Users Tagging [{}] file not found in path: {}",getTagName(),filePath);
			}
		}
		else {
			logger.info("[Users Tagging [{}] file path not configured",getTagName());	
		}
	}
	
	
	/**
	 * this function run over all the users and check if they belong to the 
	 * group to tag, if a user belong it flag his attribute as true,
	 * if it doesn't it flag it as false; 
	 */
	private void updateAllUsersTags() {
		if (taggedUsers != null && taggedUsers.size() !=0) {
			int pageSize = 100;
			int numOfPages = (int) (((getUserRepository().count() -1) / pageSize) + 1); 
			for(int i = 0; i < numOfPages; i++){
				PageRequest pageRequest = new PageRequest(i, pageSize);
				for(User user: getUserRepository().findAll(pageRequest).getContent()){
					if (taggedUsers.contains(user.getUsername())) {
						updateUserTag(user, true);
					}
					else if (isUserTagged(user) == null || isUserTagged(user)) {
						updateUserTag(user, false);
					}
				}
			}
		}else{
			resetAllUsers();
		}
	}
	

	/**
	 * This function reset all users to false 
	 * Only if the user tag is true. The updateUserTag is being implemented in the child class
	 */
	private void resetAllUsers(){
		int pageSize = 100;		
		int numOfPages = (int) (((getUserRepository().count() -1) / pageSize) + 1); 
		for(int i = 0; i < numOfPages; i++){
			PageRequest pageRequest = new PageRequest(i, pageSize);
			for(User user: getUserRepository().findAll(pageRequest).getContent()){
				if (isUserTagged(user) == null || isUserTagged(user)) {
					updateUserTag(user, false);
				}
			}
		}
	}
	
	
	public void afterPropertiesSet() throws Exception {
		updateTaggedUsersList();
		updateAllUsersTags();
	}
	
	
	public void update() {
		try {
			afterPropertiesSet();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}
	}
	
	
	public void refreshTaggedUsers(List<User> taggedUsersList){
		taggedUsers = getUsernameList(taggedUsersList);
	}
	
	
	public boolean isUserTagged(String username) {
		if (taggedUsers !=  null) {
			return taggedUsers.contains(username);
		}
		else{
			return false;
		}
	}
}


