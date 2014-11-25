package fortscale.collection.usersfiltering.service.impl;

import fortscale.collection.usersfiltering.service.SupportedUsersService;
import fortscale.domain.core.dao.UserRepository;
import fortscale.utils.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

@Service("supportedUsersService")
public class SupportedUsersServiceImpl implements SupportedUsersService, InitializingBean{
	private static Logger logger = Logger.getLogger(SupportedUsersServiceImpl.class);
	
	private HashSet<String> supportedUsersGUID;
	private HashSet<String> supportedUsernames;
	@Autowired
	private UserRepository userRepository;
	@Value("${users.filter.prioritylist:}")
    private String ouUsersFilter;
	
	@Value("${user.ad.username.whitelist.file:}")
	private String userAdUsernameWhitelistFilename;
	
	@Override
	public void afterPropertiesSet()
		throws Exception {

		if (!StringUtils.isEmpty(ouUsersFilter)) {
			if (userRepository.count() != 0) {
				supportedUsersGUID = userRepository.getUsersGUID();
			}
			else {
				supportedUsersGUID = new HashSet<String>();
			}
		}
		
		try{
			if (!StringUtils.isEmpty(userAdUsernameWhitelistFilename)) {
				File f = new File(userAdUsernameWhitelistFilename);
				if (f.exists() && f.isFile()) {
					supportedUsernames = new HashSet<String>(new ArrayList<String>(FileUtils.readLines(f)));
				}
			}
			else
				supportedUsernames = new HashSet<String> ();

		} catch(Exception e){
			logger.warn("got the following exception while trying to read from username white list file.",e);
		}
	}
	public boolean isSupportedUser(String userGUID){
		return supportedUsersGUID.contains(userGUID);
	}
	public int getSupportedUsersNumber(){
		return supportedUsersGUID.size();
	}
	public void addSupportedUser(String userGUID){
		supportedUsersGUID.add(userGUID);
	}
	public boolean isSupportedUsername(String username){
		return supportedUsernames.contains(username);
	}
}
