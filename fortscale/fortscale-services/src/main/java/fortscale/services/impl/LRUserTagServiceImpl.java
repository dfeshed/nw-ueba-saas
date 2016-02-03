package fortscale.services.impl;

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
import java.util.ArrayList;
import java.util.List;
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
	@Autowired
	protected TagService tagService;

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
		tagService.addTag(new Tag(UserTagEnum.LR.getId(), UserTagEnum.LR.getDisplayName(), true, true));
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

				List<String> tagsToAdd = new ArrayList<>();
				List<String> tagsToRemove = new ArrayList<>();

				if (removeFlag)
					tagsToRemove.add(getTag().getId());

				else
					tagsToAdd.add(getTag().getId());


				//Sync mongo and cache with the user's tags
				userService.updateUserTagList(tagsToAdd,tagsToRemove,username);
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
	public void addUserTag(String userName, String tag) {
		List<String> tagsToAdd = new ArrayList();
		tagsToAdd.add(getTag().getId());
		userService.updateUserTagList(tagsToAdd, null, userName);
	}

	@Override
	public void removeUserTag(String userName, String tag) {
		List<String> tagsToRemove = new ArrayList();
		tagsToRemove.add(getTag().getId());
		userService.updateUserTagList(null, tagsToRemove, userName);
	}

	@Override
	public UserTagEnum getTag(){
		return tag;
	}

}
