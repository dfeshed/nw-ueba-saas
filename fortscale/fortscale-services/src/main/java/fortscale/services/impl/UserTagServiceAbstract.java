package fortscale.services.impl;

import fortscale.domain.core.Tag;
import fortscale.services.TagService;
import fortscale.services.UserService;
import fortscale.services.UserTagService;
import fortscale.services.UserTaggingService;
import fortscale.utils.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.util.*;

public abstract class UserTagServiceAbstract implements UserTagService, InitializingBean {

	private static Logger logger = Logger.getLogger(UserTagServiceAbstract.class);

	@Autowired
	protected UserService userService;

	@Autowired
	private UserTaggingService userTaggingService;
	@Autowired
	private TagService tagService;

	@Value("${user.tag.service.abstract.page.size:1000}")
	private int pageSize;

	@Value("${user.tag.service.abstract.lazy.upload:false}")
	private boolean isLazyUpload;

	private List<String> ousToTag;
	private Set<String> groupsToTag;
	private Set<String> taggedUsers = new HashSet<String>();

	@Autowired
	private ActiveDirectoryGroupsHelper adCacheHandler;

	// Getter and Setter are only for unit tests
	protected int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	// -------- abstract functions ---------
	/**
	 * This functions are being implemented in the child service
	 */
	protected abstract String getFilePath();

	@Override
	public void afterPropertiesSet()
		throws Exception {

		userTaggingService.putUserTagService(getTag().getId(), this);
		Tag tag = new Tag(getTag().getId(), getTag().getDisplayName(), true, true);
		tagService.addTag(tag);

		//In case that Lazy flag turned on the tags will be loaded from db during the tagging or querying process
		if (!isLazyUpload) {
			refresh();
		}

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

		// Get the file path
		String filePath = getFilePath();

		// Input check
		if (StringUtils.isEmpty(filePath)) {
			groupsToTag = new HashSet<String>();
			taggedUsers = new HashSet<String>();
			logger.info("[Users Tagging [{}] file path not configured", getTag());
			return;
		}

		File f = new File(filePath);
		if (!f.exists() || !f.isFile()) {
			groupsToTag = new  HashSet<String>();
			taggedUsers = new HashSet<String>();
			logger.warn("Users Tagging [{}] file not found in path: {}", getTag(), filePath);
			return;
		}

		// read requested group from file
		// Check it is not empty
		groupsToTag = new HashSet<String>(FileUtils.readLines(f));
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



		// find users matching to the groups
		// this solution might be problematic memory-wise in case there are many users
		taggedUsers = new HashSet<>();
		if (!groupsToTag.isEmpty()) {

			// Warm up the cache
			adCacheHandler.warmUpCache();

			// Extend the group list
			groupsToTag.addAll(updateGroupsList());

			Set<String> subset;
			Pageable pageable = new PageRequest(0, pageSize);
			do {
				subset = userService.findNamesInGroup(new ArrayList<String>(groupsToTag), pageable);
				taggedUsers.addAll(subset);
				pageable = pageable.next();
			} while (subset.size() == pageSize);
		}

		// Find users matching to the OUs
		if (!ousToTag.isEmpty()) {
			Set<String> subset;
			Pageable pageable = new PageRequest(0, pageSize);
			do {
				subset = userService.findNamesInOU(ousToTag, pageable);
				taggedUsers.addAll(subset);
				pageable = pageable.next();
			} while (subset.size() == pageSize);
		}

		// In case we didn't found users to tag
		if (taggedUsers.isEmpty()) {
			logger.warn(
					"Users Tagging [{}] no users found in the user repository for groups {}",
					getTag(), groupsToTag);
		}
	}

	/**
	 * this function run over all the users and check if they belong to the
	 * group to tag, if a user belong it flag his attribute as true, if it
	 * doesn't it flag it as false;
	 */
	private void updateAllUsersTags() {

		Set<String> taggedInDB = userService.findNamesByTag(getTag().getId());
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

	/**
	 * Add nested groups to 'groupsToTag' list
	 */
	private Set<String> updateGroupsList(){

		// Set to hold the groups need to be add
		Set<String> completeGroupList = new HashSet<String>();

		// Set to hold the group to be checked
		Queue<String> groupsToCheck = new LinkedList<String>();

		// Add all group to list to be checked
		groupsToCheck.addAll(groupsToTag);

		// Temp variable to hold the returned data from handler
		String tempGroup;

		while (!groupsToCheck.isEmpty()) {
			String groupToCheck = groupsToCheck.remove();

			// Get data from handler
			tempGroup = adCacheHandler.get(groupToCheck);

			// If we got value from cache and we didn't check this group before
			if (tempGroup != null && !tempGroup.isEmpty() && !completeGroupList.contains(groupToCheck)) {

				// Add the group to list
				completeGroupList.add(groupToCheck);

				// Add to list the group members
				groupsToCheck.addAll(Arrays.asList(tempGroup.split(";")));
			}
		}

		return completeGroupList;
	}

	public void updateUserTag(String username, boolean isTagTheUser){
		userService.updateUserTag(getTag().getId(), username, isTagTheUser);
	}

	@Override
	public void update()
		throws Exception {

		updateTaggedUsersList();
		updateAllUsersTags();
	}

	public boolean isUserTagged(String username, String tag) {

		if (taggedUsers != null) {
			return taggedUsers.contains(username);
		}
		else {
			return false;
		}
	}
	protected Set<String> findTaggedUsersFromDb(){
		return userService.findNamesByTag(getTag().getId());
	}	
	
	public Set<String> getTaggedUsers() {
		
		return taggedUsers;
	}

	@Override
	public void addUserTag(String userName, String tag) {
		userService.updateUserTag(getTag().getId(), userName, true);
	}

	@Override
	public void removeUserTag(String userName, String tag) {
		userService.updateUserTag(getTag().getId(), userName, false);
	}
	
	public void setTaggedUsers(Set<String> taggedUsers) {
	
		this.taggedUsers = taggedUsers;
	}
}
