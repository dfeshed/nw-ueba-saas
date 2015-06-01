package fortscale.collection.tagging.service.impl;

import fortscale.collection.tagging.service.UserTagEnum;
import fortscale.collection.tagging.service.UserTagService;
import fortscale.collection.tagging.service.UserTaggingService;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

@Service("userLRTag")
public class LRUserTagServiceImpl implements UserTagService, InitializingBean {

	private static Logger logger = LoggerFactory.getLogger(LRUserTagServiceImpl.class);

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserTaggingService userTaggingService;
	@Autowired
	protected UserService userService;

	@Value("${user.list.lr_tags.path:}")
	private String filePath;
	@Value("${user.list.lr_tags.deletion_symbol:-}")
	private String deletionSymbol;

	private UserTagEnum tag = UserTagEnum.LR;

	private Set<String> aboutToLeaveList = null;


	@Override
	public boolean isUserTagged(String username) {
		if (aboutToLeaveList != null) {
			return aboutToLeaveList.contains(username);
		}
		else {
			return false;
		}
	}


	@Override
	public void afterPropertiesSet() throws Exception {
		// register the LR tag service with the user tagging service
		userTaggingService.putUserTagService(UserTagEnum.LR.getId(), this);
		refreshAboutToLeave();
	}

	private void refreshAboutToLeave() {
		this.aboutToLeaveList = loadUserAboutToLeaveListTagFromMongo();
	}

	private Set<String> loadUserAboutToLeaveListTagFromMongo() {
		return userService.findNamesByTag(getTagMongoField(), UserTagEnum.LR.getId());
	}

	@Override
	public void update() throws Exception {
		// read the user list file and update mongodb with all users in that list
		if (StringUtils.isEmpty(filePath)) {
			logger.info("no LR tag user list file configured, skipping setting users");
			return;
		}

		File usersFile = new File(filePath);
		if (usersFile.exists() && usersFile.isFile() && usersFile.canRead()) {
			// read all users from the file
			for (String line : FileUtils.readLines(usersFile)) {
				boolean removeFlag = line.startsWith(deletionSymbol);
				String username = (removeFlag)? line.substring(1).toLowerCase() : line.toLowerCase();
				if (removeFlag)
					userRepository.syncTags(username, Collections.<String>emptyList() , Arrays.asList(UserTagEnum.LR.getId()));
				else
					userRepository.syncTags(username, Arrays.asList(UserTagEnum.LR.getId()), Collections.<String>emptyList());
			}
		} else {
			logger.warn("LR tag user list file not accessible in path {}", filePath);
		}
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
