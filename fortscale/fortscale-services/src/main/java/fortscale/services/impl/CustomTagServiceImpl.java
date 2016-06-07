package fortscale.services.impl;

import fortscale.domain.core.Tag;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.services.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

@Service("customTag")
public class CustomTagServiceImpl implements UserTagService, InitializingBean {

	private static Logger logger = LoggerFactory.getLogger(CustomTagServiceImpl.class);

	private static final String CSV_DELIMITER = ",";
	private static final String VALUE_DELIMITER = "\\|";

	private static final UserTagEnum tag = UserTagEnum.custom;

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserTaggingService userTaggingService;
	@Autowired
	private UserService userService;
	@Autowired
	private TagService tagService;
	@Autowired
	private ActiveDirectoryGroupsHelper adCacheHandler;

	@Value("${user.tag.service.abstract.page.size:1000}")
	private int pageSize;
	@Value("${user.tag.service.abstract.lazy.upload:false}")
	private boolean isLazyUpload;
	@Value("${user.list.user_custom_tags.path:}")
	private String filePath;
	@Value("${user.list.custom_tags.deletion_symbol:-}")
	private String deletionSymbol;

	private Set<String> taggedUsers = new HashSet();

	@Override
	public void update() throws Exception {
		//read the custom tag list file and update mongodb with all users on that list
		if (StringUtils.isEmpty(filePath)) {
			logger.warn("No tag user list file configured, skipping tagging users");
			return;
		}
		File tagsFile = new File(filePath);
		if (tagsFile.exists() && tagsFile.isFile() && tagsFile.canRead()) {
			Map<Set<String>, Set<String>> tagsToUsers = new HashMap();
			//read all users from the file
			for (String line : FileUtils.readLines(tagsFile)) {
				boolean removeFlag = line.startsWith(deletionSymbol);
				Set<String> users = new HashSet();
				Set<String> tags = new HashSet(Arrays.asList(line.split(CSV_DELIMITER)[1].split(VALUE_DELIMITER)));
				String regex = removeFlag ? line.substring(1).split(CSV_DELIMITER)[0] : line.split(CSV_DELIMITER)[0];
				//if group
				if (regex.toLowerCase().startsWith("cn=")) {
					// Warm up the cache
					adCacheHandler.warmUpCache();
					Set<String> groupsToTag = new HashSet(Arrays.asList(regex));
					// Extend the group list
					groupsToTag.addAll(updateGroupsList(groupsToTag));
					Set<String> subset;
					Pageable pageable = new PageRequest(0, pageSize);
					do {
						subset = userService.findNamesInGroup(new ArrayList(groupsToTag), pageable);
						users.addAll(subset);
						pageable = pageable.next();
					} while (subset.size() == pageSize);
					//if ou
				} else if (regex.toLowerCase().startsWith("ou=")) {
					Set<String> subset;
					Pageable pageable = new PageRequest(0, pageSize);
					do {
						subset = userService.findNamesInOU(Arrays.asList(new String[] { regex }), pageable);
						users.addAll(subset);
						pageable = pageable.next();
					} while (subset.size() == pageSize);
					//if regex
				} else {
					users.addAll(userRepository.findByUsernameRegex(regex));
				}
				tagsToUsers.put(users, tags);
			}
			for (Map.Entry<Set<String>, Set<String>> entry: tagsToUsers.entrySet()) {
				updateAllUsersTags(entry.getKey(), entry.getValue());
			}
		} else {
			logger.warn("Custom user tag list file not accessible in path {}", filePath);
		}
	}

	/**
	 * this function run over all the users and check if they belong to the
	 * group to tag, if a user belong it flag his attribute as true, if it
	 * doesn't it flag it as false;
	 */
	private void updateAllUsersTags(Set<String> tags, Set<String> users) {
		List<Tag> availableTags = tagService.getAllTags();
		for (String tag: tags) {
			if (!availableTags.contains(tag)) {
				logger.warn("the tag " + tag + " doesn't exist");
				continue;
			}
			Set<String> taggedInDB = userService.findNamesByTag(tag, true);
			//add tag
			users.stream().filter(user -> !taggedInDB.contains(user)).forEach(user -> updateUserTag(user, tag, true));
			//remove tag
			taggedInDB.stream().filter(user -> !taggedUsers.contains(user)).forEach(user -> updateUserTag(user, tag, false));
		}
	}

	private void updateUserTag(String username, String tag, boolean isTagTheUser) {
		userService.updateUserTag(getTagMongoField(), tag, username, isTagTheUser);
	}

	/**
	 * Add nested groups to 'groupsToTag' list
	 */
	private Set<String> updateGroupsList(Set<String> groupsToTag) {
		// Set to hold the groups need to be add
		Set<String> completeGroupList = new HashSet();
		// Set to hold the group to be checked
		Queue<String> groupsToCheck = new LinkedList();
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

	public void refresh() {
		taggedUsers = userService.findAllTaggedUsers();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		//register the custom tag service with the user tagging service
		userTaggingService.putUserTagService(UserTagEnum.custom.getId(), this);
		//In case that Lazy flag turned on the tags will be loaded from db during the tagging or querying process
		if (!isLazyUpload) {
			refresh();
		}
	}

	@Override
	public boolean isUserTagged(String username) {
		if (taggedUsers != null) {
			return taggedUsers.contains(username);
		}
		else {
			return false;
		}
	}

	@Override
	public String getTagMongoField() {
		return User.tagsField;
	}

	@Override
	public void addUserTag(String userName, String tag) {
		taggedUsers.add(userName);
		userService.updateUserTagList(Arrays.asList(new String[] { tag }) , null, userName);
	}

	@Override
	public void removeUserTag(String userName, String tag) {
		userService.updateUserTagList(null, Arrays.asList(new String[] { tag }), userName);
	}

	@Override
	public UserTagEnum getTag() {
		return tag;
	}

}