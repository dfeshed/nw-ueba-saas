package fortscale.services.impl;

import fortscale.domain.core.ApplicationUserDetails;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.events.LogEventsEnum;
import fortscale.domain.fe.dao.EventScoreDAO;
import fortscale.services.CachingService;
import fortscale.services.cache.CacheHandler;
import fortscale.services.fe.Classifier;
import fortscale.utils.logging.Logger;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.query.Update;

import java.util.*;

public class UsernameService implements InitializingBean, CachingService{
	private static Logger logger = Logger.getLogger(UsernameService.class);
	private static final int USERNAME_SERVICE_PAGE_SIZE = 1000;

	private List<Set<String>> logUsernameSetList;
	private List<HashMap<String, String>> logUsernameToUserIdMapList;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private EventScoreDAO loginDAO;
	
	@Autowired
	private EventScoreDAO sshDAO;
	
	@Autowired
	private EventScoreDAO vpnDAO;

	@Autowired
	private CacheHandler<String, String> usernameToUserIdCache;

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
	
	public List<String> getFollowedUsersUsername(LogEventsEnum eventId){
		List<String> usernames = new ArrayList<>();
		for(User user: userRepository.findByFollowed(true)){
			String username = user.getUsername();
			if(username != null){
				usernames.add(username);
			}
		}
		return usernames;
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
		
		if(user == null && username.contains("@")){
			user = userRepository.findByUsername(username);
		}
		
		if(user == null){
			String usernameSplit[] = StringUtils.split(username, '@');
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
	
	
	public boolean isUsernameExist(String username){
		return isUsernameExist(username, null);
	}

	public boolean isUsernameExist(String username, LogEventsEnum eventId){
		if (usernameToUserIdCache.containsKey(username))
			return true;

		if(eventId != null && logUsernameToUserIdMapList.get(eventId.ordinal()).containsKey(username))
			return true;

		// resort to lookup mongodb and save the user id in cache
		User user = userRepository.findByUsername(username);
		return updateUsernameCache(user);
	}
		
	public String getUserId(String username,LogEventsEnum eventId){
		if (usernameToUserIdCache.containsKey(username))
			return usernameToUserIdCache.get(username);

		if(eventId != null && logUsernameToUserIdMapList.get(eventId.ordinal()).containsKey(username))
			return logUsernameToUserIdMapList.get(eventId.ordinal()).get(username);

		// fall back to query mongo if not found
		User user = userRepository.findByUsername(username);
		if (updateUsernameCache(user)) {
			return user.getId();
		}

		return null;
	}

	public boolean updateUsernameCache(User user){
		if (user!=null) {
			if (! usernameToUserIdCache.containsKey(user.getUsername())) {
				usernameToUserIdCache.put(user.getUsername(), user.getId());
				return true;
			}
		}
		return false;
	}
	
	public boolean isLogUsernameExist(LogEventsEnum eventId, String logUsername, String userId) {

		if (logUsernameSetList.get(eventId.ordinal()).contains(formatUserIdWithLogUsername(userId, logUsername)))
			return true;

		User user = userRepository.findOne(userId);
		if(user != null && logUsername.equals(user.getLogUserName(getLogname(eventId)))) {
			logUsernameSetList.get(eventId.ordinal()).add(formatUserIdWithLogUsername(userId, logUsername));
			return true;
		}

		// TODO!!!!!
		// TODO: maintain a "blacklist" of usernames not found instead of re-querying mongodb

		return false;
	}
	
	private String formatUserIdWithLogUsername(String userId, String logUsername){
		return String.format("%s%s", userId, logUsername);
	}

	public void update() {
		// Get number of users and calculate number of pages
		long count = userRepository.count();
		int numOfPages = (int)(((count - 1) / USERNAME_SERVICE_PAGE_SIZE) + 1);

		// Initialize a map from LogEventsEnum to a set of UserIdWithLogUsername
		Map<LogEventsEnum, Set<String>> map = new HashMap<>();
		for (LogEventsEnum logEventsEnum : LogEventsEnum.values())
			map.put(logEventsEnum, new HashSet<String>());

		usernameToUserIdCache.clear();
		for (int i = 0; i < numOfPages; i++) {
			Pageable pageable = new PageRequest(i, USERNAME_SERVICE_PAGE_SIZE);
			List<User> listOfUsers = userRepository.findAllExcludeAdInfo(pageable);
			// Iterate users on current page
			for (User user : listOfUsers) {
				String username = user.getUsername();
				String userId = user.getId();

				if (username != null)
					usernameToUserIdCache.put(username, userId);

				for (Map.Entry<LogEventsEnum, Set<String>> entry : map.entrySet()) {
					String logUsername = getLogUsername(entry.getKey(), user);
					if (logUsername != null)
						entry.getValue().add(formatUserIdWithLogUsername(userId, logUsername));
				}
			}
		}

		// Update logUsername sets
		for (Map.Entry<LogEventsEnum, Set<String>> entry : map.entrySet())
			logUsernameSetList.set(entry.getKey().ordinal(), entry.getValue());
	}

	public void addLogNormalizedUsername(LogEventsEnum eventId, String userId, String username){
		logUsernameToUserIdMapList.get(eventId.ordinal()).put(username, userId);
	}
	
	public void addLogUsername(LogEventsEnum eventId, String logUsername, String userId){
		logUsernameSetList.get(eventId.ordinal()).add(formatUserIdWithLogUsername(userId, logUsername));
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

	@Override public CacheHandler getCache() {
		return usernameToUserIdCache;
	}

	@Override public void setCache(CacheHandler cache) {
		usernameToUserIdCache = cache;
	}

	@Override public void handleNewValue(String key, String value) throws Exception {
		if(value == null){
			getCache().remove(key);
		}
		else {
			getCache().putFromString(key, value);
		}
	}
}
