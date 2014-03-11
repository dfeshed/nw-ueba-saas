package fortscale.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Update;

import fortscale.domain.core.ApplicationUserDetails;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.fe.dao.AuthDAO;
import fortscale.domain.fe.dao.VpnDAO;
import fortscale.services.LogEventsEnum;
import fortscale.services.fe.Classifier;
import fortscale.utils.logging.Logger;

public class UsernameService implements InitializingBean{
	private static Logger logger = Logger.getLogger(UsernameService.class);

	private HashMap<String, String> usernameToUserIdMap = new HashMap<>();
	private List<Set<String>> logUsernameSetList;
	private List<HashMap<String, String>> logUsernameToUserIdMapList;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AuthDAO loginDAO;
	
	@Autowired
	private AuthDAO sshDAO;
	
	@Autowired
	private VpnDAO vpnDAO;
	
	private boolean isLazy = true;
	
	@Value("${vpn.to.ad.username.regex.format:^%s@.*(?i)}")
	private String vpnToAdUsernameRegexFormat;
	
	@Value("${auth.to.ad.username.regex.format:^%s@.*(?i)}")
	private String authToAdUsernameRegexFormat;
	
	@Value("${ssh.to.ad.username.regex.format:^%s@.*(?i)}")
	private String sshToAdUsernameRegexFormat;
	
	
	public void setLazy(boolean isLazy) {
		this.isLazy = isLazy;
	}
	
	public String getAuthLogUsername(LogEventsEnum eventId, User user){
		return getLogUsername(eventId, user);
	}
	
	public String getLogUsername(LogEventsEnum eventId, User user){
		return user.getLogUsernameMap().get(getLogname(eventId));
	}
		
	public User findByLogUsername(LogEventsEnum eventId, String username){
		return userRepository.findByLogUsername(getLogname(eventId), username);
	}
	
	public String getTableName(LogEventsEnum eventId){
		String tablename = null;
		switch(eventId){
		case login:
			tablename = loginDAO.getTableName();
			break;
		case ssh:
			tablename = sshDAO.getTableName();
			break;
		case vpn:
			tablename = vpnDAO.getTableName();
			break;
		default:
			break;
		}
		
		return tablename;
	}
	
	public String getLogname(LogEventsEnum eventId){
		return getTableName(eventId);
	}
	
	public List<String> getFollowedUsersAuthLogUsername(LogEventsEnum eventId){
		List<String> usernames = new ArrayList<>();
		for(User user: userRepository.findByFollowed(true)){
			String username = getAuthLogUsername(eventId, user);
			if(username != null){
				usernames.add(username);
			}
		}
		return usernames;
	}
	
	public List<String> getFollowedUsersVpnLogUsername(){
		List<String> usernames = new ArrayList<>();
		for(User user: userRepository.findByFollowed(true)){
			String username = getVpnLogUsername(user);
			if(username != null){
				usernames.add(username);
			}
		}
		return usernames;
	}
	
	public User findByAuthUsername(LogEventsEnum eventId, String username){
		if(StringUtils.isEmpty(username)){
			return null;
		}
		
		User user = findByLogUsername(eventId, username);
		
		String usernameSplit[] = StringUtils.split(username, '@');
		if(user == null && usernameSplit.length > 1){
			user = userRepository.findByUsername(username);
		}
		
		if(user == null){
			user = userRepository.findByNoDomainUsername(usernameSplit[0]);
			
			if(user == null){
				//tried to avoid this call since its performance is bad.
				user = findByUsername(generateUsernameRegexByAuthUsername(eventId, usernameSplit[0]), username);
			}
		}
		
		return user;
	}
	
	private User findByUsername(String regex, String username){
		if(StringUtils.isEmpty(regex)){
			return null;
		}
		
		List<User> tmpUsers = userRepository.findByUsernameRegex(regex);
		if(tmpUsers == null || tmpUsers.size() == 0){
			return null;
		}
		
		if(tmpUsers.size() == 1){
			return tmpUsers.get(0);
		}
		
		for(User tmpUser: tmpUsers){
			if(tmpUser.getUsername().equalsIgnoreCase(username)){
				return tmpUser;
			}
		}
		
		return null;
	}
	
	private String generateUsernameRegexByAuthUsername(LogEventsEnum eventId, String authUsername){
		String regexFormat = null;
		switch(eventId){
		case login:
			regexFormat = authToAdUsernameRegexFormat;
			break;
		case ssh:
			regexFormat = sshToAdUsernameRegexFormat;
			break;
		case vpn:
			regexFormat = vpnToAdUsernameRegexFormat;
			break;
		default:
			break;
		}
		
		if(StringUtils.isEmpty(regexFormat)){
			return null;
		}

		return String.format(regexFormat, authUsername);
	}
	
	public String getVpnLogUsername(User user){
		return user.getLogUsernameMap().get(getLogname(LogEventsEnum.vpn));
	}
	
	public void fillUpdateLogUsername(Update update, String username, LogEventsEnum eventId) {
		update.set(User.getLogUserNameField(getLogname(eventId)), username);
	}


	public void fillUpdateAppUsername(Update update, User user, Classifier classifier) {
		fillUpdateAppUsername(update, user.getApplicationUserDetails().get(classifier.getUserApplication().getId()), classifier);
	}
	
	public void fillUpdateAppUsername(Update update, ApplicationUserDetails applicationUserDetails, Classifier classifier) {
		update.set(User.getAppField(classifier.getUserApplication().getId()), applicationUserDetails);
	}
	
	public void updateLogUsername(User user, LogEventsEnum eventId, String username) {
		user.addLogUsername(getLogname(eventId), username);
	}
	
	
	public boolean isUsernameExist(String username, boolean useCache){
		return isUsernameExist(username, useCache, null);
	}

	public boolean isUsernameExist(String username, boolean useCache, LogEventsEnum eventId){
		boolean found = false;
		if(useCache){
			found = usernameToUserIdMap.containsKey(username);
			if(!found && eventId != null){
				found = logUsernameToUserIdMapList.get(eventId.ordinal()).containsKey(username);
			}
		} else{
			found = userRepository.findByUsername(username) != null ? true : false;
		}
		
		return found;
	}
		
	public String getUserId(String username, boolean useCache, LogEventsEnum eventId){
		String ret = null;
		if(useCache){
			ret = usernameToUserIdMap.get(username);
			if(ret == null && eventId != null){
				ret = logUsernameToUserIdMapList.get(eventId.ordinal()).get(username);
			}
		} else{
			User user = userRepository.findByUsername(username);
			ret = user != null ? user.getId() : null;
		}
		
		return ret;
	}
	
	public boolean isLogUsernameExist(LogEventsEnum eventId, String logUsername, boolean useCache){
		if(useCache){
			return logUsernameSetList.get(eventId.ordinal()).contains(logUsername);
		} else{
			return userRepository.findByLogUsername(getLogname(eventId), logUsername) != null ? true : false;
		}
	}
	
	
	
	public void update(){
		HashMap<String, String> tmpMap = new HashMap<>();
		
		List<User> users = userRepository.findAllExcludeAdInfo();
		for(User user: users){
			if(user.getUsername() != null){
				tmpMap.put(user.getUsername(), user.getId());
			}
		}
		usernameToUserIdMap = tmpMap;
		
		for(LogEventsEnum logEventsEnum: LogEventsEnum.values()){
			Set<String> logUsernameSet = new HashSet<>();
			for(User user: users){
				String logUsername = getLogUsername(logEventsEnum, user);
				if(logUsername != null){
					logUsernameSet.add(logUsername);
				}
			}
			logUsernameSetList.set(logEventsEnum.ordinal(), logUsernameSet);
		}
		logger.debug("username set contain {} elements", usernameToUserIdMap.size());
	}
	
	public void addLogNormalizedUsername(LogEventsEnum eventId, String userId, String username){
		logUsernameToUserIdMapList.get(eventId.ordinal()).put(username, userId);
	}
	
	public void addLogUsername(LogEventsEnum eventId, String logUsername){
		logUsernameSetList.get(eventId.ordinal()).add(logUsername);
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		logUsernameSetList = new ArrayList<>(LogEventsEnum.values().length);
		logUsernameToUserIdMapList = new ArrayList<>(LogEventsEnum.values().length);
		for(@SuppressWarnings("unused") LogEventsEnum logEventsEnum: LogEventsEnum.values()){
			logUsernameSetList.add(new HashSet<String>());
			logUsernameToUserIdMapList.add(new HashMap<String,String>());
		}
		
		if(!isLazy){
			update();
		}
	}
}
