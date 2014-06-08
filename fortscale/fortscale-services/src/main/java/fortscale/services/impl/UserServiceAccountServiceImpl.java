package fortscale.services.impl;

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

import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.services.UserServiceAccountService;
import fortscale.utils.logging.Logger;


@Service("userServiceAccountService")
public class UserServiceAccountServiceImpl implements UserServiceAccountService,InitializingBean {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UsernameNormalizer secUsernameNormalizer;
	
	private static Logger logger = Logger.getLogger(UserServiceAccountServiceImpl.class);
	
	@Value("${user.list.service_account.path:}")
	public String filePath;
	
	@Value("${user.list.service_account.deletion_symbol:}")
	public String deletionSymbol;
	
	private Set<String> serviceAccounts = null;


	@Override
	public boolean isUserServiceAccount(String username) {
		if (serviceAccounts !=  null) {
			return serviceAccounts.contains(username);
		}
		else{
			return false;
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		serviceAccounts = loadUserServiceAccountTagFromMongo();
		if(!StringUtils.isEmpty(filePath)){
			File f = new File(filePath);
			if(f.exists() && !f.isDirectory()) {
				serviceAccounts = updateMongoUserServiceAccountTag(new HashSet<String>(FileUtils.readLines(new File(filePath))));
				logger.info("ServiceAccount file loaded from path: {}",filePath);
			}
			else {
				logger.warn("ServiceAccount file not found in path: {}",filePath);
			}
		}
		else {
			logger.info("ServiceAccount file path not configured");		
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
			String username = secUsernameNormalizer.normalize(serviceAccountUser);
			boolean isUserServiceAccount;
			if (serviceAccountUser.startsWith(deletionSymbol)) {
				// Remove tag from user.
				isUserServiceAccount = false;
				username = secUsernameNormalizer.normalize(serviceAccountUser.substring(1,serviceAccountUser.length()));
			}
			else {
				isUserServiceAccount = true;
			}
			if ((userRepository.findByUsername(username) != null)) {				
				userRepository.updateUserServiceAccount(userRepository.findByUsername(username),
					isUserServiceAccount);
			}
			else {
				logger.warn("User {} isn't in the user repository.",serviceAccountUser);
			}
		}
		return loadUserServiceAccountTagFromMongo();
	}	
}
