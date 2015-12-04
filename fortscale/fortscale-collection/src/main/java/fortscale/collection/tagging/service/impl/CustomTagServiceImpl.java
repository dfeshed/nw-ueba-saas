package fortscale.collection.tagging.service.impl;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import fortscale.collection.tagging.service.UserTagEnum;
import fortscale.collection.tagging.service.UserTagService;
import fortscale.collection.tagging.service.UserTaggingService;
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
	private Set<String> tagsToIgnore = ImmutableSet.of(UserTagEnum.admin.getId(), UserTagEnum.service.getId(),
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
		File usersFile = new File(filePath);
		if (usersFile.exists() && usersFile.isFile() && usersFile.canRead()) {
			//read all users from the file
			for (String line : FileUtils.readLines(usersFile)) {
				String regex = line.split(CSV_DELIMITER)[0];
				Set<String> tags = new HashSet();
				if (line.split(CSV_DELIMITER).length > 1) {
					tags.addAll(Arrays.asList(line.split(CSV_DELIMITER)[1].split(VALUE_DELIMITER)));
				}
				List<User> users = userRepository.findByUsernameRegex(regex);
				for (User user: users) {
					Set<String> existingTags = user.getTags();
					//ignore the static type tags
					existingTags = Sets.difference(existingTags, tagsToIgnore);
					List<String> tagsToAdd = new ArrayList(Sets.difference(tags, existingTags));
					tagsToAdd = new ArrayList(Sets.difference(new HashSet(tagsToAdd), tagsToIgnore));
					for (String tagStr: tagsToAdd) {
						tagService.addTag(new Tag(tagStr));
					}
					List<String> tagsToRemove = new ArrayList(Sets.difference(existingTags, tags));
					//if we need to remove or add tags
					if (!tagsToAdd.isEmpty() || !tagsToRemove.isEmpty()) {
						//sync mongo and cache with the user's tags
						userService.updateUserTagList(tagsToAdd, tagsToRemove, user.getUsername(), getTag().getId());
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
		tags = Sets.difference(tags, tagsToIgnore);
		//if the user contains a custom tag
		return !tags.isEmpty();
	}

	@Override
	public String getTagMongoField() {
		return User.tagsField;
	}

	@Override
	public UserTagEnum getTag(){
		return tag;
	}

}