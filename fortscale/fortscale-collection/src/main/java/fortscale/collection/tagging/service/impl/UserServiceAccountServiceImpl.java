
package fortscale.collection.tagging.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import fortscale.collection.tagging.service.UserTagEnum;
import fortscale.collection.tagging.service.UserTagService;
import fortscale.collection.tagging.service.UserTaggingService;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.utils.logging.Logger;

@Service("userServiceAccountService")
public class UserServiceAccountServiceImpl implements UserTagService, InitializingBean {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserTaggingService userTaggingService;

	private static Logger logger = Logger.getLogger(UserServiceAccountServiceImpl.class);

	@Value("${user.list.service_account.path:}")
	private String filePath;

	@Value("${user.list.service_account.deletion_symbol:}")
	private String deletionSymbol;

	private Set<String> serviceAccounts = null;

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

	private Set<String> findUsersByOU(String userName) {

		List<String> ousToTag = new LinkedList<>();
		ousToTag.add(userName);
		return userRepository.findByUserInOU(ousToTag);
	}

	private boolean removeTagFromUser(String userName) {

		if (serviceAccounts.contains(userName)) {
			boolean userExists = userRepository.findIfUserExists(userName);
			if (userExists) {
				userRepository.updateUserTag(User.userServiceAccountField, userName, false);
				userRepository.syncTags(userName, Collections.<String>emptyList(), Arrays.asList(UserTagEnum.service.getId()));
				serviceAccounts.remove(userName);
				return true;
			}
		}
		return false;
	}

	private boolean tagServiceAccount(String userName) {

		if (!serviceAccounts.contains(userName)) {
			boolean userExists = userRepository.findIfUserExists(userName);
			if (userExists) {
				userRepository.updateUserTag(User.userServiceAccountField, userName, true);
				userRepository.syncTags(userName, Arrays.asList(UserTagEnum.service.getId()), Collections.<String>emptyList());
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

		return userRepository.findNameByTag(getTagMongoField(), true);
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

	public Set<String> getServiceAccounts() {

		return serviceAccounts;
	}

	public void setServiceAccounts(Set<String> serviceAccounts) {

		this.serviceAccounts = serviceAccounts;
	}
}
