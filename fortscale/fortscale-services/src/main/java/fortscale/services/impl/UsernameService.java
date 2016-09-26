package fortscale.services.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import fortscale.domain.core.ApplicationUserDetails;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.events.LogEventsEnum;
import fortscale.domain.fe.dao.EventScoreDAO;
import fortscale.services.CachingService;
import fortscale.services.cache.CacheHandler;
import fortscale.services.cache.SimpleLRUCache;
import fortscale.services.impl.metrics.UsernameServiceMetrics;
import fortscale.utils.monitoring.stats.StatsService;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Update;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class UsernameService implements InitializingBean, CachingService {

	private static final String USER_ID_TO_LOG_USERNAME_DELIMITER = "###";

	private int maxCacheUserEntriesPerDataSource = 100000; // TODO externalize the value to spring properties

	// maps log event id to user representation combined of the user id and the log username, e.g. :
	// vpn -> {1111111###gils@fortscale.com, 2222222222###gabi@cbs.com, 33333333###ami@nbc.com}
	private Map<String, Set<String>> logUsernamesCache = new HashMap<>();

	// maps log event id to a map of username to user id , e.g. :
	// vpn -> {gils -> 11111111, gabi -> 22222222, ami -> 333333333}
	private Map<String, Map<String, String>> normalizedUsernameToUserIdCache = new HashMap<>();

	@Autowired
	private StatsService statsService;

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

    @Autowired
    private CacheHandler<String, String> dNToUserName;

	@Autowired
	private SamAccountNameService samAccountNameService;

	private Map<String,Cache<String,String>> adFieldToUserCahce;

	@Value("${username.service.page.size:1000}")
	private int usernameServicePageSize;

	private boolean isLazyUsernameCachesUpdate = true;
	private UsernameServiceMetrics serviceMetrics;

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

	public void addLogUsername(User user, String eventId, String username) {
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

	public boolean isUsernameExist(String normalizedUsername, LogEventsEnum logEventsEnum) {
		serviceMetrics.lookingForUsername++;
		if (usernameToUserIdCache.containsKey(normalizedUsername)) {
			return true;
		}

		if(logEventsEnum != null && normalizedUsernameToUserIdCache.containsKey(logEventsEnum.getId()) && normalizedUsernameToUserIdCache.get(logEventsEnum.getId()).containsKey(normalizedUsername)) {
			return true;
		}

		// resort to lookup mongodb and save the user id in cache
		User user = userRepository.findByUsername(normalizedUsername);

		if (user != null) {
			updateUsernameInCache(user);

			return true;
		}

		serviceMetrics.usernameNotFound++;
		return false;
	}

	public String getUserId(String username, String logEventName){
		serviceMetrics.lookingForUserId++;
		if (usernameToUserIdCache.containsKey(username))
			return usernameToUserIdCache.get(username);

		if(logEventName != null && normalizedUsernameToUserIdCache.containsKey(logEventName) && normalizedUsernameToUserIdCache.get(logEventName).containsKey(username)) {
			return normalizedUsernameToUserIdCache.get(logEventName).get(username);
		}

		// fall back to query mongo if not found
		User user = userRepository.findByUsername(username);

		if (user != null) {
			updateUsernameInCache(user);

			return user.getId();
		}

		serviceMetrics.userIdNotFound++;
		return null;
	}

	public void updateUsernameInCache(User user){
		serviceMetrics.checkingUsernameInCache++;
		if (!usernameToUserIdCache.containsKey(user.getUsername())) {
			serviceMetrics.usernameAddedToCache++;
			usernameToUserIdCache.put(user.getUsername(), user.getId());
		}
	}

	public boolean isLogUsernameExist(String logEventName, String logUsername, String userId) {
		serviceMetrics.lookingForLogUsername++;
		if (logUsernamesCache.containsKey(logEventName) && logUsernamesCache.get(logEventName).contains(formatUserIdWithLogUsername(userId, logUsername))) {
			return true;
		}

		User user = userRepository.findOne(userId);
		if(user != null && logUsername.equals(user.getLogUserName(getLogname(logEventName)))) {
			return true;
		}

		// TODO!!!!!
		// TODO: maintain a "blacklist" of usernames not found instead of re-querying mongodb

		serviceMetrics.logUsernameNotFound++;
		return false;
	}

	private String formatUserIdWithLogUsername(String userId, String logUsername){
		return String.format("%s" + USER_ID_TO_LOG_USERNAME_DELIMITER + "%s", userId, logUsername);
	}

	public void updateUsernameCaches() {
		serviceMetrics.updateUsernameCache++;
		// Get number of users and calculate number of pages
		long count = userRepository.count();
		int numOfPages = (int)(((count - 1) / usernameServicePageSize) + 1);

		usernameToUserIdCache.clear();
		samAccountNameService.clearCache();
        dNToUserName.clear();
		for (int i = 0; i < numOfPages; i++) {
			Pageable pageable = new PageRequest(i, usernameServicePageSize);
			List<User> listOfUsers = userRepository.findAllUsers(pageable);
			// Iterate users on current page
			for (User user : listOfUsers) {
				String username = user.getUsername();
				String userId = user.getId();
                String dn = user.getAdInfo().getDn();

				if (username != null) {
					usernameToUserIdCache.put(username, userId);
					if (StringUtils.isNotBlank(dn)) {
						dNToUserName.put(dn, username);
					}
					samAccountNameService.updateSamAccountnameCache(user);
				}

				Map<String, String> logUsernameMap = user.getLogUsernameMap();

				for (Map.Entry<String, String> logUsernameEntry : logUsernameMap.entrySet()) {
					String logEventId = logUsernameEntry.getKey();

					addLogUsernameToCache(logEventId, logUsernameEntry.getValue(), userId);
				}
			}
		}
	}

	public void addUsernameToCache(String logEventName, String userId, String normalizedUsername){
		serviceMetrics.checkingNormalizedUsernameInCache++;
		if (!normalizedUsernameToUserIdCache.containsKey(logEventName)) {
			serviceMetrics.normalizedUsernameAddedToCache++;
			createLogEventToUserIdMap(logEventName);
		}

		normalizedUsernameToUserIdCache.get(logEventName).put(normalizedUsername, userId);
	}

	private void createLogEventIdToUserEntry(String logEventName) {
		Set<String> usersSet = Collections.newSetFromMap(new SimpleLRUCache<String, Boolean>(maxCacheUserEntriesPerDataSource));

		logUsernamesCache.put(logEventName, usersSet);
	}

	private void createLogEventToUserIdMap(String logEventName) {
		normalizedUsernameToUserIdCache.put(logEventName, new SimpleLRUCache<String, String>(maxCacheUserEntriesPerDataSource));
	}

	public void addLogUsernameToCache(String logEventName, String logUsername, String userId){
		serviceMetrics.updateLogUsernameCache++;
		if (!logUsernamesCache.containsKey(logEventName)) {
			serviceMetrics.logUsernameAddedToCache++;
			createLogEventIdToUserEntry(logEventName);
		}

		logUsernamesCache.get(logEventName).add(formatUserIdWithLogUsername(userId, logUsername));
	}

	/**
	 * This method return username (For AD users the username is equivalent to Fortscale noramlized_username ) of a given dn that represent a User , if the user doesn't exist the method return null
	 * @param dn
	 * @return
	 */
	public String getUserNameByDn(String dn){

		serviceMetrics.getUsernameByDn++;
		//if this DN exist at the cache
		if (dNToUserName.containsKey(dn)){
			return dNToUserName.get(dn);
		}

        User user = userRepository.findByAdInfoDn(dn);

        if (user != null)
        {
            String username = user.getUsername();
            dNToUserName.put(dn,username);
            updateUsernameInCache(user);

			return username;
		}
		serviceMetrics.userNameNotFoundByDn++;
		return null;
	}

	/**
	 * This method return username based on other AD field information (i.e - username--->DN_value)
	 * @param aDFieldName -  the AD field to be based on the search
	 * @param aDFieldValue - the AD given field value
	 * @param partOrFullFlag -  will sign if to do part ore full equalisation ( true - full , false -part (contain) )
	 * @return
	 */
	public String getUserNameByADField(String aDFieldName, String aDFieldValue,boolean partOrFullFlag) {

		serviceMetrics.getUsernameByAd++;
		String username =null;

		//in case this filed name doesn't have any <value,username> cache - create it
		if (adFieldToUserCahce == null || !adFieldToUserCahce.containsKey(aDFieldName)) {
			if (adFieldToUserCahce == null)
				adFieldToUserCahce = new HashMap<>();

			CacheBuilder builder = CacheBuilder.newBuilder();

			// todo - move it out to configuration
			builder.maximumSize(10000);
			builder.expireAfterWrite(1, TimeUnit.DAYS);

			Cache<String,String> cache = builder.build();


			username = userRepository.findUserNameByfield(aDFieldName, aDFieldValue, partOrFullFlag);

			if (!StringUtils.isEmpty(username))
				cache.put(aDFieldValue,username);

			this.adFieldToUserCahce.put(aDFieldName,cache);

			if (StringUtils.isEmpty(username)) {
				serviceMetrics.usernameNotFoundByAd++;
				return null;
			}

			return username;
		}

		Cache cache = adFieldToUserCahce.get(aDFieldName);
		username = (String) cache.getIfPresent(aDFieldValue);
		if(username == null)
		{
			username = userRepository.findUserNameByfield(aDFieldName, aDFieldValue, partOrFullFlag);

			if(!StringUtils.isEmpty(username) ) {
				cache.put(aDFieldValue, username);
				this.adFieldToUserCahce.put(aDFieldName, cache);
			} else {
				serviceMetrics.usernameNotFoundByAd++;
			}
		}

		return StringUtils.isEmpty(username) ? null : username;

	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (!isLazyUsernameCachesUpdate) {
			updateUsernameCaches();
		}
		serviceMetrics = new UsernameServiceMetrics(statsService);
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
