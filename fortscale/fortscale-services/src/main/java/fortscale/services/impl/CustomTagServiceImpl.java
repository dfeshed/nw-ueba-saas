package fortscale.services.impl;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import fortscale.services.UserTagEnum;
import fortscale.services.UserTagService;
import fortscale.services.UserTaggingService;
import fortscale.domain.core.Tag;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.services.TagService;
import fortscale.services.UserService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

@Service("customTag")
public class CustomTagServiceImpl implements UserTagService, InitializingBean {

	private static Logger logger = LoggerFactory.getLogger(CustomTagServiceImpl.class);

	private static final String CSV_DELIMITER = ",";
	private static final String VALUE_DELIMITER = "\\|";

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserTaggingService userTaggingService;
	@Autowired
	private UserService userService;
	@Autowired
	private TagService tagService;

	@Value("${user.list.custom_tags.path:}")
	private String filePath;

	private UserTagEnum tag = UserTagEnum.custom;
	private Set<String> fixedTags = ImmutableSet.of(UserTagEnum.admin.getId(), UserTagEnum.service.getId(),
			UserTagEnum.executive.getId(), UserTagEnum.LR.getId());

	@Override
	public void afterPropertiesSet() throws Exception {
		//register the custom tag service with the user tagging service
		userTaggingService.putUserTagService(UserTagEnum.custom.getId(), this);
	}

	@Override
	public void update() throws Exception {
		//read the custom tag list file and update mongodb with all users on that list
		if (StringUtils.isEmpty(filePath)) {
			logger.warn("No custom tag user list file configured, skipping tagging users");
			return;
		}
		File tagsFile = new File(filePath);
		if (tagsFile.exists() && tagsFile.isFile() && tagsFile.canRead()) {
			//read all users from the file
			for (String line : FileUtils.readLines(tagsFile)) {
				String regex = line.split(CSV_DELIMITER)[0];
				Set<String> tags = new HashSet();
				if (line.split(CSV_DELIMITER).length > 1) {
					tags.addAll(Arrays.asList(line.split(CSV_DELIMITER)[1].split(VALUE_DELIMITER)));
				}
				List<User> users = userRepository.findByUsernameRegex(regex);
				for (User user: users) {
					Set<String> existingTags = user.getTags();
					List<String> tagsToAdd = new ArrayList();
					Set<String> tagsDifference = Sets.difference(tags, existingTags);
					//for now - ignore adding fixed tags
					for (String tagStr: tagsDifference) {
						if (fixedTags.contains(tagStr)) {
							/*UserTagService userTagService = userTaggingService.getUserTagService(tagStr);
							userTagService.addUserTag(user.getUsername(), null);*/
							continue;
						} else {
							tagsToAdd.add(tagStr);
							tagService.addTag(new Tag(tagStr));
						}
					}
					List<String> tagsToRemove = new ArrayList();
					tagsDifference = Sets.difference(existingTags, tags);
					//for now - ignore removing fixed tags
					for (String tagStr: tagsDifference) {
						if (fixedTags.contains(tagStr)) {
							/*UserTagService userTagService = userTaggingService.getUserTagService(tagStr);
							userTagService.removeUserTag(user.getUsername(), null);*/
							continue;
						} else {
							tagsToRemove.add(tagStr);
						}
					}
					//if we need to remove or add tags
					if (!tagsToAdd.isEmpty() || !tagsToRemove.isEmpty()) {
						//sync mongo and cache with the user's tags
						userService.updateUserTagList(tagsToAdd, tagsToRemove, user.getUsername());
					}
				}
			}
		} else {
			logger.error("Custom tag list file not accessible in path {}", filePath);
		}
	}

	@Override
	public boolean isUserTagged(String username) {
		Set<String> tags = userRepository.getUserTags(username);
		//ignore the static type tags
		tags = Sets.difference(tags, fixedTags);
		//if the user contains a custom tag
		return !tags.isEmpty();
	}

	@Override
	public String getTagMongoField() {
		return User.tagsField;
	}

	@Override
	public void addUserTag(String userName, String tag) {
		List tagsToAdd = new ArrayList();
		tagsToAdd.add(tag);
		userService.updateUserTagList(tagsToAdd, null, userName);
	}

	@Override
	public void removeUserTag(String userName, String tag) {
		List tagsToRemove = new ArrayList();
		tagsToRemove.add(tag);
		userService.updateUserTagList(null, tagsToRemove, userName);
	}

	@Override
	public UserTagEnum getTag(){
		return tag;
	}

}