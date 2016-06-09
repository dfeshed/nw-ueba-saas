package fortscale.services.impl;

import fortscale.domain.core.Tag;
import fortscale.domain.core.UserTagEnum;
import fortscale.domain.core.dao.UserRepository;
import fortscale.services.TagService;
import fortscale.services.UserService;
import fortscale.services.UserTagService;
import fortscale.services.UserTaggingService;
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
import java.util.stream.Collectors;

@Service("customTag")
public class CustomTagServiceImpl implements UserTagService, InitializingBean {

	private static Logger logger = LoggerFactory.getLogger(CustomTagServiceImpl.class);

	private static final String CSV_DELIMITER = ",";
	private static final String VALUE_DELIMITER = "\\|";

	private static final UserTagEnum tag = UserTagEnum.custom;

	@Value("${user.tag.service.abstract.page.size:1000}")
	private int pageSize;
	@Value("${user.tag.service.abstract.lazy.upload:false}")
	private boolean isLazyUpload;
	@Value("${user.list.user_custom_tags.path:}")
	private String filePath;
	@Value("${user.list.custom_tags.deletion_symbol:-}")
	private String deletionSymbol;

	private UserRepository userRepository;
	private UserTaggingService userTaggingService;
	private UserService userService;
	private TagService tagService;
	private ActiveDirectoryGroupsHelper activeDirectoryGroupsHelper;

	private Map<String, Set<String>> taggedUsers = new HashMap();

	@Autowired
	public CustomTagServiceImpl(UserRepository userRepository, UserTaggingService userTaggingService,
								UserService userService, TagService tagService,
								ActiveDirectoryGroupsHelper activeDirectoryGroupsHelper) {
		this.userRepository = userRepository;
		this.userTaggingService = userTaggingService;
		this.userService = userService;
		this.tagService = tagService;
		this.activeDirectoryGroupsHelper = activeDirectoryGroupsHelper;
	}

	@Override
	public void update() throws Exception {
		//read the custom tag list file and update mongodb with all users on that list
		if (StringUtils.isEmpty(filePath)) {
			logger.warn("No tag user list file configured, skipping tagging users");
			return;
		}
		File tagsFile = new File(filePath);
		if (tagsFile.exists() && tagsFile.isFile() && tagsFile.canRead()) {
			List<String> availableTags = tagService.getAllTags().stream().map(Tag::getName).
					collect(Collectors.toList());
			Map<Set<String>, Set<String>> tagsToAddToUsers = new HashMap();
			Map<Set<String>, Set<String>> tagsToRemoveFromUsers = new HashMap();
			taggedUsers = new HashMap();
			boolean warmedUpCache = false;
			//read all users from the file
			for (String line : FileUtils.readLines(tagsFile)) {
				boolean removeFlag = line.startsWith(deletionSymbol);
				Set<String> users = new HashSet();
				Set<String> tags = new HashSet(Arrays.asList(line.split(VALUE_DELIMITER)[1].split(CSV_DELIMITER)));
				tags.retainAll(availableTags);
				String searchTerm = removeFlag ? line.substring(1).split(VALUE_DELIMITER)[0] :
						line.split(VALUE_DELIMITER)[0];
				//if group
				if (searchTerm.toLowerCase().startsWith("cn=")) {
					if (!warmedUpCache) {
						// Warm up the cache
						activeDirectoryGroupsHelper.warmUpCache();
						warmedUpCache = true;
					}
					Set<String> groupsToTag = new HashSet(Arrays.asList(searchTerm));
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
				} else if (searchTerm.toLowerCase().startsWith("ou=")) {
					Set<String> subset;
					Pageable pageable = new PageRequest(0, pageSize);
					do {
						subset = userService.findNamesInOU(Arrays.asList(searchTerm), pageable);
						users.addAll(subset);
						pageable = pageable.next();
					} while (subset.size() == pageSize);
					//if regex
				} else {
					users.addAll(userRepository.findByUsernameRegex(searchTerm));
				}
				if (!users.isEmpty()) {
					if (removeFlag) {
						tagsToRemoveFromUsers.put(users, tags);
					} else {
						tagsToAddToUsers.put(users, tags);
					}
				}
			}
			updateAllUsersTags(tagsToAddToUsers, tagsToRemoveFromUsers);
		} else {
			logger.warn("Custom user tag list file not accessible in path {}", filePath);
		}
	}

	private void updateAllUsersTags(Map<Set<String>, Set<String>> tagsToAddToUsers,
									Map<Set<String>, Set<String>> tagsToRemoveFromUsers) {
		//add tags
		for (Map.Entry<Set<String>, Set<String>> entry: tagsToAddToUsers.entrySet()) {
			for (String username: entry.getKey()) {
				Set<String> tags = taggedUsers.get(username);
				if (tags == null) {
					tags = new HashSet();
				}
				tags.addAll(entry.getValue());
				taggedUsers.put(username, tags);
				userService.updateUserTagList(new ArrayList(entry.getValue()), null, username);
			}
		}
		//remove tags
		for (Map.Entry<Set<String>, Set<String>> entry: tagsToRemoveFromUsers.entrySet()) {
			for (String userName: entry.getKey()) {
				Set<String> tags = taggedUsers.get(userName);
				if (tags != null) {
					tags.removeAll(entry.getValue());
					taggedUsers.put(userName, tags);
				}
				userService.updateUserTagList(null, new ArrayList(entry.getValue()), userName);
			}
		}
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
			tempGroup = activeDirectoryGroupsHelper.get(groupToCheck);
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
	public boolean isUserTagged(String username, String tag) {
		if (taggedUsers != null) {
			Set<String> tags = taggedUsers.get(username);
			return tags != null ? tags.contains(tag) : false;
		}
		else {
			return false;
		}
	}

	@Override
	public void addUserTag(String username, String tag) {
		Set<String> tags = taggedUsers.get(username);
		if (tags == null) {
			tags = new HashSet();
		}
		tags.add(tag);
		taggedUsers.put(username, tags);
		userService.updateUserTagList(Arrays.asList(new String[] { tag }), null, username);
	}

	@Override
	public void removeUserTag(String username, String tag) {
		Set<String> tags = taggedUsers.get(username);
		if (tags != null) {
			tags.remove(tag);
		}
		taggedUsers.put(username, tags);
		userService.updateUserTagList(null, Arrays.asList(new String[] { tag }), username);
	}

	@Override
	public UserTagEnum getTag() {
		return tag;
	}

}