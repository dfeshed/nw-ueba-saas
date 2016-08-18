package fortscale.services.impl;

import fortscale.domain.core.Tag;
import fortscale.domain.core.UserTagEnum;
import fortscale.domain.core.dao.UserRepository;
import fortscale.services.TagService;
import fortscale.services.UserService;
import fortscale.services.UserTagService;
import fortscale.services.UserTaggingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("customTag")
public class CustomTagServiceImpl implements UserTagService, InitializingBean {

	private static Logger logger = LoggerFactory.getLogger(CustomTagServiceImpl.class);

	private static final String CSV_DELIMITER = ",";
	private static final String VALUE_DELIMITER = "\\|";
	private static final String OU_PREFIX = "ou=";
	private static final String GROUP_PREFIX = "cn=";

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
			UserService userService, TagService tagService, ActiveDirectoryGroupsHelper activeDirectoryGroupsHelper) {
		this.userRepository = userRepository;
		this.userTaggingService = userTaggingService;
		this.userService = userService;
		this.tagService = tagService;
		this.activeDirectoryGroupsHelper = activeDirectoryGroupsHelper;
	}

	@Override
	public void update() throws Exception {
		taggedUsers = new HashMap();
		boolean warmedUpCache = false;
		for (Tag tag : tagService.getAllTags()) {
			Map<String, Set<String>> tagsToAddToUsers = new HashMap();
			Map<String, Set<String>> tagsToRemoveFromUsers = new HashMap();
			Set<String> users = new HashSet();
			for (String rule : tag.getRules()) {
				boolean removeFlag = rule.startsWith(deletionSymbol);
				String searchTerm = removeFlag ? rule.substring(1) : rule;
				//if group
				if (searchTerm.toLowerCase().startsWith(GROUP_PREFIX)) {
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
				} else if (searchTerm.toLowerCase().startsWith(OU_PREFIX)) {
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
						tagsToRemoveFromUsers.put(tag.getName(), users);
					} else {
						tagsToAddToUsers.put(tag.getName(), users);
					}
				}
			}
			updateAllUsersTags(tagsToAddToUsers, tagsToRemoveFromUsers);
		}
	}

	private void updateAllUsersTags(Map<String, Set<String>> tagsToAddToUsers,
			Map<String, Set<String>> tagsToRemoveFromUsers) {
		//add tags
		for (Map.Entry<String, Set<String>> entry: tagsToAddToUsers.entrySet()) {
			for (String username: entry.getValue()) {
				Set<String> tags = taggedUsers.get(username);
				if (tags == null) {
					tags = new HashSet();
				}
				tags.add(entry.getKey());
				taggedUsers.put(username, tags);
				userService.updateUserTagList(Collections.singletonList(entry.getKey()), null, username);
			}
		}
		//remove tags
		for (Map.Entry<String, Set<String>> entry: tagsToRemoveFromUsers.entrySet()) {
			for (String userName: entry.getValue()) {
				userService.updateUserTagList(null, Collections.singletonList(entry.getKey()), userName);
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

	private void refresh() {
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