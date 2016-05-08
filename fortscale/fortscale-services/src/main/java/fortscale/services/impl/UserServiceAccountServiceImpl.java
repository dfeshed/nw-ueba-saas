package fortscale.services.impl;

import fortscale.domain.core.Tag;
import fortscale.domain.core.User;
import fortscale.services.*;
import fortscale.utils.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Service("userServiceAccountService")
public class UserServiceAccountServiceImpl implements UserTagService, InitializingBean {

	@Autowired
	protected UserService userService;

	@Autowired
	private UserTaggingService userTaggingService;
	@Autowired
	private TagService tagService;

	private static Logger logger = Logger.getLogger(UserServiceAccountServiceImpl.class);

	@Value("${user.list.service_account.path:}")
	private String filePath;

	@Value("${user.list.service_account.deletion_symbol:}")
	private String deletionSymbol;

	@Value("${user.service.account.service.impl.page.size:1000}")
	private int pageSize;

	@Value("${user.tag.service.abstract.lazy.upload:false}")
	private boolean isLazyUpload;

	private Set<String> serviceAccounts = new HashSet<>();

	private UserTagEnum tag = UserTagEnum.service;


	@Override
	public boolean isUserTagged(String username) {

		if (serviceAccounts != null) {
			return serviceAccounts.contains(username);
		}
		else {
			return false;
		}
	}

	@Override
	public void afterPropertiesSet()
		throws Exception {

		userTaggingService.putUserTagService(UserTagEnum.service.getId(), this);
		Tag tag = new Tag(UserTagEnum.service.getId(), UserTagEnum.service.getDisplayName(), true, true);
		tagService.addTag(tag);

		//In case that Lazy flag turned on the tags will be loaded from db during the tagging or querying process
		if (!isLazyUpload) {
			refreshServiceAccounts();
		}
	}

	@Override
	public void update()
		throws IOException {

		if (!StringUtils.isEmpty(getFilePath())) {
			File usersFile = new File(getFilePath());
			if (usersFile.exists() && usersFile.isFile()) {
				Set<String> usersFromFile = null;
				usersFromFile = new HashSet<String>(FileUtils.readLines(usersFile));
				int addedCounter = 0;
				int removedCounter = 0;
				for (String userLine : usersFromFile) {
					if (userLine.startsWith(deletionSymbol)) {

						// Remove user's tag

						String userName = userLine.substring(1).toLowerCase();
						if (userName.toLowerCase().startsWith("ou=")) {
							// Remove tag from all uses in OU
							Set<String> usersByOu = findUsersByOU(userName);
							for (String user : usersByOu) {
								if (removeTagFromUser(user)) {
									removedCounter++;
								}
							}
						} else {
							// Remove tag from single user
							if (removeTagFromUser(userName)) {
								removedCounter++;
							}
						}
					}
					else {

						// Add user's tag

						String userName = userLine.toLowerCase();
						if (userName.toLowerCase().startsWith("ou=")) {
							// Tag all uses in OU
							Set<String> usersByOu = findUsersByOU(userName);
							for (String user : usersByOu) {
								if (tagServiceAccount(user)) {
									addedCounter++;
								}
							}
						} else {
							// Tag single user
							if (tagServiceAccount(userName)) {
								addedCounter++;
							}
						}
					}
				}

				logger.info("ServiceAccount file loaded from path: {}", getFilePath());
				logger.debug("{} accounts were tagged and {} accounts were untagged", addedCounter, removedCounter);
			}
			else {
				logger.warn("ServiceAccount file not found in path: {}", getFilePath());
			}
		}
		else {
			logger.info("ServiceAccount file path not configured");
		}
	}

	private Set<String> findUsersByOU(List<String> usernames) {
		Set<String> usersByOu = new HashSet<String>();
		Set<String> subset;
		Pageable pageable = new PageRequest(0, pageSize);
		do {
			subset = userService.findNamesInOU(usernames, pageable);
			usersByOu.addAll(subset);
			pageable = pageable.next();
		} while (subset.size() == pageSize);
		return usersByOu;
	}

	private Set<String> findUsersByOU(String username) {
		List<String> ousToTag = new LinkedList<>();
		ousToTag.add(username);
		return findUsersByOU(ousToTag);
	}

	private boolean removeTagFromUser(String userName) {
		if (serviceAccounts.contains(userName)) {
			boolean userExists = userService.findIfUserExists(userName);
			if (userExists) {
				userService.updateUserTag(getTagMongoField(), getTag().getId(), userName, false);
				serviceAccounts.remove(userName);
				return true;
			}
		}
		return false;
	}

	private boolean tagServiceAccount(String userName) {
		if (!serviceAccounts.contains(userName)) {
			boolean userExists = userService.findIfUserExists(userName);
			if (userExists) {
				userService.updateUserTag(getTagMongoField(), getTag().getId(), userName, true);
				serviceAccounts.add(userName);
				return true;
			}
		}
		return false;
	}

	public void refreshServiceAccounts() {

		this.serviceAccounts = loadUserServiceAccountTagFromMongo();
	}

	private Set<String> loadUserServiceAccountTagFromMongo() {

		return userService.findNamesByTag(getTagMongoField(), true);
	}

	public String getDeletionSymbol() {

		return deletionSymbol;
	}

	public void setDeletionSymbol(String deletionSymbol) {

		this.deletionSymbol = deletionSymbol;
	}

	public String getFilePath() {

		return filePath;
	}

	public void setFilePath(String filePath) {

		this.filePath = filePath;
	}

	@Override
	public String getTagMongoField() {

		return User.userServiceAccountField;
	}

	@Override
	public void addUserTag(String userName, String tag) {
		tagServiceAccount(userName);
	}

	@Override
	public void removeUserTag(String userName, String tag) {
		removeTagFromUser(userName);
	}

	@Override
	public UserTagEnum getTag() {

		return tag;
	}

	public Set<String> getServiceAccounts() {

		return serviceAccounts;
	}

	public void setServiceAccounts(Set<String> serviceAccounts) {

		this.serviceAccounts = serviceAccounts;
	}
}
