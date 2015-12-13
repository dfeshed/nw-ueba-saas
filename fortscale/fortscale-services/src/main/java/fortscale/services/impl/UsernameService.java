package fortscale.services.impl;

import fortscale.domain.core.ApplicationUserDetails;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.events.LogEventsEnum;
import fortscale.domain.fe.dao.EventScoreDAO;
import fortscale.services.CachingService;
import fortscale.services.cache.CacheHandler;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Update;

import java.util.*;

public class UsernameService implements InitializingBean, CachingService{

	private static final String USER_ID_TO_LOG_USERNAME_DELIMITER = "###";

	// maps log event id to user representation combined of the user id and the log username, e.g. :
	// vpn -> {12345678###gils@fortscale.com, 33333333###ami@fortscale.com}
	private Map<String, Set<String>> logEventIdToUsers = new HashMap<>();

	// maps log event id to map of username to user id , e.g. :
	// vpn -> {gils -> 12345678, ami -> 33333}
	private Map<String, Map<String, String>> logEventIdToUserIdMap = new HashMap<>();

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EventScoreDAO loginDAO;

	@Autowired
	private EventScoreDAO sshDAO;

	@Autowired
	private EventScoreDAO vpnDAO;

	@Autowired
	private EventScoreDAO amtDAO;

	@Autowired
	private EventScoreDAO amtsessionDAO;

	@Autowired
	private CacheHandler<String, String> usernameToUserIdCache;

	@Autowired
	private SamAccountNameService samAccountNameService;

	@Value("${username.service.page.size:1000}")
	private int usernameServicePageSize;

	private boolean isLazyUsernameCachesUpdate = true;

	// For unit tests only
	protected int getPageSize() {
		return usernameServicePageSize;
	}

	// For unit tests only
	protected void setPageSize(int pageSize) {
		usernameServicePageSize = pageSize;
	}

	public String getAuthLogUsername(LogEventsEnum eventId, User user){
		return getLogUsername(eventId.getId(), user);
	}

	public String getLogUsername(String eventId, User user){
		return user.getLogUsernameMap().get(getLogname(eventId));
	}

	public List<String> getFollowedUsersUsername(){
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

	public String getVpnLogUsername(User user){
		return user.getLogUsernameMap().get(getLogname(LogEventsEnum.vpn.name()));
	}

	public void fillUpdateLogUsername(Update update, String username, String logEventName) {
		update.set(User.getLogUserNameField(getLogname(logEventName)), username);
	}

	public void fillUpdateAppUsername(Update update, ApplicationUserDetails applicationUserDetails, String userApplication) {
		update.set(User.getAppField(userApplication), applicationUserDetails);
	}

	public void updateLogUsername(User user, String eventId, String username) {
		user.addLogUsername(getLogname(eventId), username);
	}

	public String getTableName(String eventId) {
		boolean eventIdExist = EnumUtils.isValidEnum(LogEventsEnum.class, eventId);

		if (eventIdExist) {
			LogEventsEnum logEventIdType = LogEventsEnum.valueOf(eventId);

			switch (logEventIdType) {
				case login:
					return loginDAO.getTableName();
				case ssh:
					return sshDAO.getTableName();
				case vpn:
					return vpnDAO.getTableName();
				case amt:
					return amtDAO.getTableName();
				case amtsession:
					return amtsessionDAO.getTableName();
				default:
					break;
			}
		}

		return eventId;
	}

	public String getLogname(String eventId){
		return getTableName(eventId);
	}


	public boolean isUsernameExist(String username){
		return isUsernameExist(username, null);
	}

	public boolean isUsernameExist(String username, LogEventsEnum logEventsEnum){
		if (usernameToUserIdCache.containsKey(username))
			return true;

		if(logEventsEnum != null && logEventIdToUserIdMap.containsKey(logEventsEnum.getId()) && logEventIdToUserIdMap.get(logEventsEnum.getId()).containsKey(username))
			return true;

		// resort to lookup mongodb and save the user id in cache
		User user = userRepository.findByUsername(username);

		if (user != null) {
			updateUsernameInCache(user);

			return true;
		}

		return false;
	}

	public String getUserId(String username, String logEventName){
		if (usernameToUserIdCache.containsKey(username))
			return usernameToUserIdCache.get(username);

		if(logEventName != null && logEventIdToUserIdMap.containsKey(logEventName) && logEventIdToUserIdMap.get(logEventName).containsKey(username)) {
			return logEventIdToUserIdMap.get(logEventName).get(username);
		}

		// fall back to query mongo if not found
		User user = userRepository.findByUsername(username);

		if (user != null) {
			updateUsernameInCache(user);

			return user.getId();
		}

		return null;
	}

	public void updateUsernameInCache(User user){
		if (!usernameToUserIdCache.containsKey(user.getUsername())) {
			usernameToUserIdCache.put(user.getUsername(), user.getId());
		}
	}



	public boolean isLogUsernameExist(String logEventName, String logUsername, String userId) {

		if (logEventIdToUsers.containsKey(logEventName) && logEventIdToUsers.get(logEventName).contains(formatUserIdWithLogUsername(userId, logUsername))) {
			return true;
		}

		User user = userRepository.findOne(userId);
		if(user != null && logUsername.equals(user.getLogUserName(getLogname(logEventName)))) {
			if (!logEventIdToUsers.containsKey(logEventName)) {
				logEventIdToUsers.put(logEventName, new HashSet<String>());
			}

			logEventIdToUsers.get(logEventName).add(formatUserIdWithLogUsername(userId, logUsername));

			return true;
		}

		// TODO!!!!!
		// TODO: maintain a "blacklist" of usernames not found instead of re-querying mongodb

		return false;
	}

	private String formatUserIdWithLogUsername(String userId, String logUsername){
		return String.format("%s" + USER_ID_TO_LOG_USERNAME_DELIMITER + "%s", userId, logUsername);
	}

	public void updateUsernameCaches() {
		// Get number of users and calculate number of pages
		long count = userRepository.count();
		int numOfPages = (int)(((count - 1) / usernameServicePageSize) + 1);

		// Initialize a logEventIdToUsernamesMap from LogEventsEnum to a set of UserIdWithLogUsername
		Map<String, Set<String>> logEventIdToUsernamesMap = new HashMap<>();


		usernameToUserIdCache.clear();
		samAccountNameService.clearCache();
		for (int i = 0; i < numOfPages; i++) {
			Pageable pageable = new PageRequest(i, usernameServicePageSize);
			List<User> listOfUsers = userRepository.findAllUsers(pageable);
			// Iterate users on current page
			for (User user : listOfUsers) {
				String username = user.getUsername();
				String userId = user.getId();

				if (username != null) {
					usernameToUserIdCache.put(username, userId);

					samAccountNameService.updateSamAccountnameCache(user);
				}

				Map<String, String> logUsernameMap = user.getLogUsernameMap();

				for (Map.Entry<String, String> logUsernameEntry : logUsernameMap.entrySet()) {
					String logEventId = logUsernameEntry.getKey();

					if (!logEventIdToUsers.containsKey(logEventId)) {
						logEventIdToUsers.put(logEventId, new HashSet<String>());
					}

					logEventIdToUsers.get(logEventId).add(formatUserIdWithLogUsername(userId, logUsernameEntry.getValue()));
				}
			}
		}
	}

	public void addLogNormalizedUsername(String logEventName, String userId, String username){
		if (!logEventIdToUserIdMap.containsKey(logEventName)) {
			logEventIdToUserIdMap.put(logEventName, new HashMap<String, String>());
		}

		logEventIdToUserIdMap.get(logEventName).put(username, userId);
	}

	public void addLogUsernameToCache(String logEventName, String logUsername, String userId){
		if (!logEventIdToUsers.containsKey(logEventName)) {
			logEventIdToUsers.put(logEventName, new HashSet<String>());
		}

		logEventIdToUsers.get(logEventName).add(formatUserIdWithLogUsername(userId, logUsername));
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (!isLazyUsernameCachesUpdate) {
			updateUsernameCaches();
		}
	}

	public void setLazyUsernameCachesUpdate(boolean isLazyUsernameCachesUpdate) {
		this.isLazyUsernameCachesUpdate = isLazyUsernameCachesUpdate;
	}

	@Override public CacheHandler<String, String> getCache() {
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
