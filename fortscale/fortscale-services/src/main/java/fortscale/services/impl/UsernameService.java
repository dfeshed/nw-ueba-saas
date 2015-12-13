package fortscale.services.impl;

import fortscale.domain.core.ApplicationUserDetails;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.events.LogEventsEnum;
import fortscale.domain.fe.dao.EventScoreDAO;
import fortscale.services.CachingService;
import fortscale.services.cache.CacheHandler;
import fortscale.services.cache.SimpleLRUCache;
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

	@Value("${max.user.elements.per.data.source.in.cache:100000}")
	private int maxUserElementsPerDataSourceInCache;

	// maps log event id to user representation combined of the user id and the log username, e.g. :
	// vpn -> {1111111###gils@fortscale.com, 2222222222###gabi@cbs.com, 33333333###ami@nbc.com}
	private Map<String, Set<String>> logEventIdToUsersRep = new HashMap<>();

	// maps log event id to a map of username to user id , e.g. :
	// vpn -> {gils -> 11111111, gabi -> 22222222, ami -> 333333333}
	private Map<String, Map<String, String>> logEventIdToUserIdMapping = new HashMap<>();

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

		if(logEventsEnum != null && logEventIdToUserIdMapping.containsKey(logEventsEnum.getId()) && logEventIdToUserIdMapping.get(logEventsEnum.getId()).containsKey(username)) {
			return true;
		}

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

		if(logEventName != null && logEventIdToUserIdMapping.containsKey(logEventName) && logEventIdToUserIdMapping.get(logEventName).containsKey(username)) {
			return logEventIdToUserIdMapping.get(logEventName).get(username);
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

		if (logEventIdToUsersRep.containsKey(logEventName) && logEventIdToUsersRep.get(logEventName).contains(formatUserIdWithLogUsername(userId, logUsername))) {
			return true;
		}

		User user = userRepository.findOne(userId);
		if(user != null && logUsername.equals(user.getLogUserName(getLogname(logEventName)))) {
			if (!logEventIdToUsersRep.containsKey(logEventName)) {
				createLogEventIdToUserEntry(logEventName);
			}

			logEventIdToUsersRep.get(logEventName).add(formatUserIdWithLogUsername(userId, logUsername));

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

					if (!logEventIdToUsersRep.containsKey(logEventId)) {
						createLogEventIdToUserEntry(logEventId);
					}

					logEventIdToUsersRep.get(logEventId).add(formatUserIdWithLogUsername(userId, logUsernameEntry.getValue()));
				}
			}
		}
	}

	public void addLogNormalizedUsername(String logEventName, String userId, String username){
		if (!logEventIdToUserIdMapping.containsKey(logEventName)) {
			createLogEventToUserIdMap(logEventName);
		}

		logEventIdToUserIdMapping.get(logEventName).put(username, userId);
	}

	private void createLogEventIdToUserEntry(String logEventName) {
		Set<String> usersSet = Collections.newSetFromMap(new SimpleLRUCache<String, Boolean>(maxUserElementsPerDataSourceInCache));

		logEventIdToUsersRep.put(logEventName, usersSet);
	}

	private void createLogEventToUserIdMap(String logEventName) {
		Map<String, String> usersMap = new SimpleLRUCache<>(maxUserElementsPerDataSourceInCache);

		logEventIdToUserIdMapping.put(logEventName, usersMap);
	}

	public void addLogUsernameToCache(String logEventName, String logUsername, String userId){
		if (!logEventIdToUsersRep.containsKey(logEventName)) {
			createLogEventIdToUserEntry(logEventName);
		}

		logEventIdToUsersRep.get(logEventName).add(formatUserIdWithLogUsername(userId, logUsername));
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
