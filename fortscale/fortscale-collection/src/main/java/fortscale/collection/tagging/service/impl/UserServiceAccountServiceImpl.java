package fortscale.collection.tagging.service.impl;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import fortscale.services.impl.UsernameNormalizer;
import fortscale.utils.logging.Logger;


@Service("userServiceAccountService")
public class UserServiceAccountServiceImpl implements UserTagService,InitializingBean {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UsernameNormalizer secUsernameNormalizer;
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
		if (serviceAccounts !=  null) {
			return serviceAccounts.contains(username);
		}
		else{
			return false;
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		userTaggingService.putUserTagService(UserTagEnum.service.getId(), this);
		update();
	}
	
	@Override
	public void update() throws Exception {
		boolean isFileOk = true;
		if(!StringUtils.isEmpty(getFilePath())){
			File f = new File(getFilePath());
			if(f.exists() && !f.isDirectory()) {
				serviceAccounts = updateMongoUserServiceAccountTag(new HashSet<String>(FileUtils.readLines(new File(getFilePath()))));
				logger.info("ServiceAccount file loaded from path: {}",getFilePath());
			}
			else {
				isFileOk = false;
				logger.warn("ServiceAccount file not found in path: {}",getFilePath());
			}
		}
		else {
			isFileOk = false;
			logger.info("ServiceAccount file path not configured");		
		}
		if (!isFileOk) {
			serviceAccounts = loadUserServiceAccountTagFromMongo();
		}
	}
	
	private Set<String> loadUserServiceAccountTagFromMongo() {
		Set<String> result = new HashSet<String>();
		List<User> users = userRepository.findByUserServiceAccount(true);
		for (User user : users) {
			result.add(user.getUsername());
		}
		return result;
	}
	
	private Set<String> updateMongoUserServiceAccountTag(Set<String> serviceAccounts) {
		for (String serviceAccountUser : serviceAccounts) {
			String username;
			boolean isUserServiceAccount;
			if (serviceAccountUser.startsWith(getDeletionSymbol())) {
				// Remove tag from user.
				isUserServiceAccount = false;
				username = secUsernameNormalizer.normalize(serviceAccountUser.substring(1,serviceAccountUser.length()));
			}
			else {
				isUserServiceAccount = true;
				username = secUsernameNormalizer.normalize(serviceAccountUser);
			}
			User user = userRepository.findByUsername(username);
			if ((user != null)) {				
				userRepository.updateUserServiceAccount(user,isUserServiceAccount);
			}
			else {
				logger.warn("User {} isn't in the user repository.",serviceAccountUser);
			}
		}
		return loadUserServiceAccountTagFromMongo();
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
}
