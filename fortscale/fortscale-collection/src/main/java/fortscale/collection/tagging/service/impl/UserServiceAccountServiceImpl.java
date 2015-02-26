package fortscale.collection.tagging.service.impl;

import fortscale.collection.tagging.service.UserTagEnum;
import fortscale.collection.tagging.service.UserTagService;
import fortscale.collection.tagging.service.UserTaggingService;
import fortscale.domain.core.User;
import fortscale.services.UserService;
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

	private static Logger logger = Logger.getLogger(UserServiceAccountServiceImpl.class);

	@Value("${user.list.service_account.path:}")
	private String filePath;

	@Value("${user.list.service_account.deletion_symbol:}")
	private String deletionSymbol;

	@Value("${user.service.account.service.impl.page.size:1000}")
	private int pageSize;

	private Set<String> serviceAccounts = null;

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
		refreshServiceAccounts();
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
		for (Pageable pageable = new PageRequest(0, pageSize); ; pageable = pageable.next()) {
			Set<String> subset = userService.findNamesInOU(usernames, pageable);
			usersByOu.addAll(subset);
			if (subset.size() < pageSize)
				break;
		}
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
