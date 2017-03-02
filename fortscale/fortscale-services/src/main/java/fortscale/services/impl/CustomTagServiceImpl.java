package fortscale.services.impl;

import fortscale.domain.core.Tag;
import fortscale.services.TagService;
import fortscale.services.UserService;
import fortscale.services.UserTagService;
import org.apache.commons.lang.WordUtils;
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

	private static final String OU_PREFIX = "ou=";
	private static final String GROUP_PREFIX = "cn=";

	@Value("${user.tag.service.abstract.page.size:1000}")
	private int pageSize;
	@Value("${user.tag.service.abstract.lazy.upload:false}")
	private boolean isLazyUpload;
	@Value("${user.list.custom_tags.deletion_symbol:-}")
	private String deletionSymbol;

	private UserService userService;
	private TagService tagService;
	private ActiveDirectoryGroupsHelper activeDirectoryGroupsHelper;

	@Autowired
	public CustomTagServiceImpl(UserService userService, TagService tagService,
			ActiveDirectoryGroupsHelper activeDirectoryGroupsHelper) {
		this.userService = userService;
		this.tagService = tagService;
		this.activeDirectoryGroupsHelper = activeDirectoryGroupsHelper;
	}

	@Override
	public void update() throws Exception {
		logger.info("starting tagging process");
		boolean warmedUpCache = false;
		for (Tag tag : tagService.getAllTags(false)) {
			logger.info("processing tag - {}", tag.getName());
			Map<String, Set<String>> tagsToAddToUsers = new HashMap<>();
			Map<String, Set<String>> tagsToRemoveFromUsers = new HashMap<>();
			Set<String> users = new HashSet<>();
			for (String rule : tag.getRules()) {
				boolean removeFlag = rule.startsWith(deletionSymbol);
				String searchTerm = removeFlag ? rule.substring(1) : rule;
				//if group
				if (searchTerm.toLowerCase().startsWith(GROUP_PREFIX)) {
					logger.info("group rule - {}", rule);
					if (!warmedUpCache) {
						// Warm up the cache
						activeDirectoryGroupsHelper.warmUpCache();
						warmedUpCache = true;
					}
					Set<String> groupsToTag = new HashSet<>(Arrays.asList(searchTerm));
					// Extend the group list
					groupsToTag.addAll(updateGroupsList(groupsToTag));
					Set<String> subset;
					Pageable pageable = new PageRequest(0, pageSize);
					do {
						subset = userService.findNamesInGroup(new ArrayList<>(groupsToTag), pageable);
						users.addAll(subset);
						pageable = pageable.next();
					} while (subset.size() == pageSize);
				//if ou
				} else if (searchTerm.toLowerCase().startsWith(OU_PREFIX)) {
					logger.info("ou rule - {}", rule);
					Set<String> subset;
					Pageable pageable = new PageRequest(0, pageSize);
					do {
						subset = userService.findNamesInOU(Collections.singletonList(searchTerm), pageable);
						users.addAll(subset);
						pageable = pageable.next();
					} while (subset.size() == pageSize);
				//if regex
				} else {
					logger.info("regex rule - {}", rule);
					users.addAll(userService.findByUsernameRegex(searchTerm));
				}
				if (!users.isEmpty()) {
					if (removeFlag) {
						tagsToRemoveFromUsers.put(tag.getName(), users);
					} else {
						tagsToAddToUsers.put(tag.getName(), users);
					}
				}
			}
			logger.info("updating user tags");
			updateAllUsersTags(tagsToAddToUsers, tagsToRemoveFromUsers);
		}
	}

	private void updateAllUsersTags(Map<String, Set<String>> tagsToAddToUsers,
			Map<String, Set<String>> tagsToRemoveFromUsers) {
		//add tags
		for (Map.Entry<String, Set<String>> entry: tagsToAddToUsers.entrySet()) {
			for (String username: entry.getValue()) {
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
		Set<String> completeGroupList = new HashSet<>();
		// Set to hold the group to be checked
		Queue<String> groupsToCheck = new LinkedList<>();
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

	@Override
	public void afterPropertiesSet() throws Exception {
		//add default tags to system
		tagService.addTag(new Tag(Tag.ADMIN_TAG, WordUtils.capitalize(Tag.ADMIN_TAG), true, true,true));
		tagService.addTag(new Tag(Tag.EXECUTIVE_TAG, WordUtils.capitalize(Tag.EXECUTIVE_TAG), true, true,true));
		tagService.addTag(new Tag(Tag.SERVICE_ACCOUNT_TAG, WordUtils.capitalize(Tag.SERVICE_ACCOUNT_TAG), true, true,true));
	}

	@Override
	public void addUserTags(String username, List<String> tags) throws Exception {
		for (String tag: tags) {
			//if there's no such tag in the system
			if (tagService.getTag(tag) == null) {
				//try to add the new tag
				if (!tagService.addTag(new Tag(tag))) {
					//if failed
					throw new Exception("failed to add new tag - " + tag);
				}
			}
		}
		userService.updateUserTagList(tags, null, username);
	}

	@Override
	public void addUserTagsRegex(String usernameRegex, List<String> tags) throws Exception {
		final Set<String> usernames = userService.findByUsernameRegex(usernameRegex);
		for (String username : usernames) {
			addUserTags(username, tags);
		}
	}

	@Override
	public void removeUserTags(String username, List<String> tags) {
		userService.updateUserTagList(null, tags, username);
	}

	@Override
	public int removeTagFromAllUsers(String tagName) {
		return userService.removeTagFromAllUsers(tagName);
	}


}