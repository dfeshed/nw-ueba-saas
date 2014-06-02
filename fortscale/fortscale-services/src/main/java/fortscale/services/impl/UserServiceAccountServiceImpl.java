package fortscale.services.impl;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
	
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import fortscale.services.UserServiceAccountService;


@Service("userServiceAccountService")
public class UserServiceAccountServiceImpl implements UserServiceAccountService,InitializingBean {

	private static Logger logger = LoggerFactory.getLogger(UserServiceAccountServiceImpl.class);
	
	@Value("${user.list.service_account.path:}")
	private String filePath;
	
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
		if(!StringUtils.isEmpty(filePath)){
			File f = new File(filePath);
			if(f.exists() && !f.isDirectory()) {
				serviceAccounts = new HashSet<String>(FileUtils.readLines(new File(filePath)));
			}
			else {
				logger.warn("ServiceAccount file not found in path: %s",filePath);
			}
		}
		else {
			logger.info("ServiceAccount file path not configured");		
		}		
	}		
}
