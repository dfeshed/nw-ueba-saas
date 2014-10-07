
package fortscale.collection.tagging.service.impl;

import java.io.File;
import java.util.*;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import fortscale.collection.tagging.service.UserTagEnum;
import fortscale.collection.tagging.service.UserTagService;
import fortscale.collection.tagging.service.UserTaggingService;
import fortscale.domain.core.dao.UserRepository;
import fortscale.utils.logging.Logger;

public abstract class UserTagServiceAbstract implements UserTagService, InitializingBean {

	private static Logger logger = Logger.getLogger(UserTagServiceAbstract.class);
	@Autowired
	protected UserRepository userRepository;
	@Autowired
	private UserTaggingService userTaggingService;

	private List<String> ousToTag;
	private List<String> groupsToTag;
	private Set<String> taggedUsers = new HashSet<String>();

	

	// -------- abstract functions ---------
	/**
	 * This functions are being implemented in the child service
	 */
	protected abstract String getFilePath();

	protected abstract void updateUserTag(String username, boolean isTagTheUser);

	public abstract UserTagEnum getTag();

	@Override
	public void afterPropertiesSet()
		throws Exception {

		userTaggingService.putUserTagService(getTag().getId(), this);
		refresh();
	}

	public void refresh() {
		taggedUsers = findTaggedUsersFromDb();
	}

	/**
	 * This function run over the file groups and if it runs with a group with
	 * no name (e.g: empty group - when the user just clicked ENTER in the vi)
	 * it removes it, the it return if the group is empty or not after the
	 * process
	 * 
	 * @return
	 */
	private boolean isFileEmptyFromGroups() {

		if (groupsToTag.contains("")) {
			groupsToTag.remove("");
		}
		if (groupsToTag.size() > 0) {
			return false; // file is not empty
		}
		return true; // file is empty
	}

	/**
	 * This function read the file from it's destination which set in the
	 * configuration file and - stores all the groups in: this.groupsToTag; -
	 * stores all taggedUsers in: this.taggedUsers
	 * 
	 * @throws Exception
	 */
	private void updateTaggedUsersList()
		throws Exception {

		String filePath = getFilePath();
		if (!StringUtils.isEmpty(filePath)) {
			File f = new File(filePath);
			if (f.exists() && f.isFile()) {
				// read requested group from file
				groupsToTag = FileUtils.readLines(new File(filePath));
				if (isFileEmptyFromGroups()) {
					logger.warn(
						"Users Tagging [{}] file is Empty: {}", getTag(),
						filePath);
					taggedUsers = new HashSet<String>();
					return;
				}
				// take OUs from groups
				ousToTag = new LinkedList<>();
				for (String group : groupsToTag) {
					if (group.toLowerCase().startsWith("ou=")) {
						// this is not a group but OU
						ousToTag.add(group);
					}
				}
				groupsToTag.removeAll(ousToTag);
				// find users matching to the groups and the OUs (this solution might be problematic memory-wise in case there are many users)
				taggedUsers = new HashSet<>();
				if (!groupsToTag.isEmpty()) {
					taggedUsers.addAll(userRepository.findByUserInGroup(groupsToTag));
				}
				if (!ousToTag.isEmpty()) {
					taggedUsers.addAll(userRepository.findByUserInOU(ousToTag));
				}
				if (taggedUsers.isEmpty()) {
					logger.warn(
						"Users Tagging [{}] no users found in the user repository for groups {}",
						getTag(), groupsToTag);
				}
			}
			else {
				groupsToTag = new ArrayList<String>();
				taggedUsers = new HashSet<String>();
				logger.warn(
					"Users Tagging [{}] file not found in path: {}", getTag(),
					filePath);
			}
		}
		else {
			groupsToTag = new ArrayList<String>();
			taggedUsers = new HashSet<String>();
			logger.info(
				"[Users Tagging [{}] file path not configured", getTag());
		}
	}

	/**
	 * this function run over all the users and check if they belong to the
	 * group to tag, if a user belong it flag his attribute as true, if it
	 * doesn't it flag it as false;
	 */
	private void updateAllUsersTags() {

		Set<String> taggedInDB = userRepository.findNameByTag(getTagMongoField(), true);
		for (String user : taggedUsers) {
			if (!taggedInDB.contains(user)) {
				updateUserTag(user, true);
			}
		}
		for (String user : taggedInDB) {
			if (!taggedUsers.contains(user)) {
				updateUserTag(user, false);
			}
		}
	}


	@Override
	public void update()
		throws Exception {

		updateTaggedUsersList();
		updateAllUsersTags();
	}

	public boolean isUserTagged(String username) {

		if (taggedUsers != null) {
			return taggedUsers.contains(username);
		}
		else {
			return false;
		}
	}
	protected Set<String> findTaggedUsersFromDb(){
		return userRepository.findNameByTag(getTagMongoField(), true);
	}	
	
	public Set<String> getTaggedUsers() {
		
		return taggedUsers;
	}

	
	public void setTaggedUsers(Set<String> taggedUsers) {
	
		this.taggedUsers = taggedUsers;
	}
}
