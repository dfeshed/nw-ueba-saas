package fortscale.services.impl;

import fortscale.domain.ad.*;
import fortscale.domain.ad.dao.AdGroupRepository;
import fortscale.domain.ad.dao.AdUserRepository;
import fortscale.domain.ad.dao.AdUserThumbnailRepository;
import fortscale.domain.ad.dao.UserMachineDAO;
import fortscale.domain.core.*;
import fortscale.domain.core.dao.ComputerRepository;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.events.LogEventsEnum;
import fortscale.domain.fe.dao.EventScoreDAO;
import fortscale.domain.fe.dao.EventsToMachineCount;
import fortscale.services.UserApplication;
import fortscale.services.UserService;
import fortscale.services.cache.CacheHandler;
import fortscale.services.exceptions.UnknownResourceException;
import fortscale.services.fe.Classifier;
import fortscale.services.types.PropertiesDistribution;
import fortscale.utils.JksonSerilaizablePair;
import fortscale.utils.TimestampUtils;
import fortscale.utils.actdir.ADParser;
import fortscale.utils.logging.Logger;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mortbay.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;

import java.text.ParseException;
import java.util.*;
import java.util.Map.Entry;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

public class UserServiceImpl implements UserService{
	private static Logger logger = Logger.getLogger(UserServiceImpl.class);
	private static final String SEARCH_FIELD_PREFIX = "##";
	
	@Autowired
	private MongoOperations mongoTemplate;
	
	@Autowired
	private AdUserRepository adUserRepository;
	
	@Autowired
	private AdUserThumbnailRepository adUserThumbnailRepository;
	
	@Autowired
	private AdGroupRepository adGroupRepository;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ComputerRepository computerRepository;
	
	@Autowired
	private UserMachineDAO userMachineDAO;
	
	@Autowired
	private EventScoreDAO loginDAO;
	
	@Autowired
	private EventScoreDAO sshDAO;
	
	@Autowired
	private EventScoreDAO vpnDAO;
	
	@Autowired
	private ImpalaWriterFactory impalaWriterFactory;
	
	@Autowired
	private UsernameService usernameService;

	@Autowired 
	private ADParser adUserParser; 

	@Value("${ad.info.update.read.page.size:1000}")
	private int readPageSize;

	@Value("${users.ou.filter:}")
	private String usersOUfilter;

	@Value("${user.service.impl.page.size:1000}")
	private int userServiceImplPageSize;

	// For unit tests only
	protected int getPageSize() {
		return userServiceImplPageSize;
	}

	// For unit tests only
	protected void setPageSize(int pageSize) {
		userServiceImplPageSize = pageSize;
	}

	private Map<String, String> groupDnToNameMap = new HashMap<>();

	@Autowired
	private CacheHandler<String, List<String>> userTagsCache;
	
	@Override
	public User createUser(UserApplication userApplication, String username, String appUsername){
		User user = new User();
		user.setUsername(username);
		user.setSearchField(createSearchField(null, username));
		user.setWhenCreated(new Date());
		createNewApplicationUserDetails(user, new ApplicationUserDetails(userApplication.getId(), appUsername), false);
		return user;
	}
	
	
	//NOTICE: The user of this method should check the status of the event if he doesn't want to add new users with fail status he should call with onlyUpdate=true
	//        The same goes for cases like security events where we don't want to create new User if there is no correlation with the active directory.
	@Override
	public void updateOrCreateUserWithClassifierUsername(final Classifier classifier, String normalizedUsername, String logUsername, boolean onlyUpdate, boolean updateAppUsername) {
		if(StringUtils.isEmpty(normalizedUsername)){
			logger.warn("got a empty string {} username", classifier);
			return;
		}

		LogEventsEnum eventId = classifier.getLogEventsEnum();
		String userId = usernameService.getUserId(normalizedUsername, eventId);
		if(userId == null && onlyUpdate){
			return;
		}
			
		if(userId != null){
			if(!usernameService.isLogUsernameExist(eventId, logUsername, userId)){
				Update update = new Update();
				usernameService.fillUpdateLogUsername(update, logUsername, eventId);
				if(updateAppUsername){
					usernameService.fillUpdateAppUsername(update, createNewApplicationUserDetails(classifier.getUserApplication(), logUsername), classifier);
				}
			
				updateUser(userId, update);
				usernameService.addLogUsername(eventId, logUsername, userId);
			}
        } else{
			User user = createUser(classifier.getUserApplication(), normalizedUsername, logUsername);
			usernameService.updateLogUsername(user, eventId, logUsername);
			saveUser(user);
			if(user == null || user.getId() == null){
				logger.info("Failed to save {} user with normalize username ({}) and log username ({})", classifier, normalizedUsername, logUsername);
			} else{
				usernameService.addLogNormalizedUsername(eventId, user.getId(), normalizedUsername);
				usernameService.addLogUsername(eventId, logUsername,user.getId());
			}
		}		
	}

	private User saveUser(User user){
		user = userRepository.save(user);
		usernameService.updateUsernameCache(user);
		//probably will never be called, but just to make sure the cache is always synchronized with mongoDB
		if (user.getTags() != null && user.getTags().size() > 0){
			userTagsCache.put(user.getUsername(), new ArrayList<String>(user.getTags()));
		}
		return user;
	}

	private void saveUsers(List<User> users) {
		userRepository.save(users);
		for (User user : users) {
			usernameService.updateUsernameCache(user);
			//probably will never be called, but just to make sure the cache is always synchronized with mongoDB
			if (user.getTags() != null && user.getTags().size() > 0) {
				userTagsCache.put(user.getUsername(), new ArrayList<String>(user.getTags()));
			}
		}
	}

	@Override
	public void updateUserLastActivityOfType(LogEventsEnum eventId, String username, DateTime dateTime){
		Update update = new Update();
		update.set(User.getLogLastActivityField(eventId), dateTime);
		mongoTemplate.updateFirst(query(where(User.usernameField).is(username)), update, User.class);
	}
	
	@Override
	public void updateUsersLastActivityOfType(LogEventsEnum eventId, Map<String, Long> userLastActivityMap){
		Iterator<Entry<String, Long>> entries = userLastActivityMap.entrySet().iterator();
		while(entries.hasNext()){
			Entry<String, Long> entry = entries.next();
			updateUserLastActivityOfType(eventId, entry.getKey(), new DateTime(TimestampUtils.convertToMilliSeconds(entry.getValue())));
		}
	}
	
	@Override
	public void updateUserLastActivity(String username, DateTime maxTime){
		Update update = new Update();
		update.set(User.lastActivityField, maxTime);
		mongoTemplate.updateFirst(query(where(User.usernameField).is(username)), update, User.class);
	}

	@Deprecated
	@Override
	public void updateUsersLastActivity(Map<String, Long> userLastActivityMap){
		Iterator<Entry<String, Long>> entries = userLastActivityMap.entrySet().iterator();
		while(entries.hasNext()){
			Entry<String, Long> entry = entries.next();
			User user = userRepository.getLastActivityAndLogUserNameByUserName(entry.getKey());
			if(user == null){
				return;
			}
			DateTime userCurrLast = user.getLastActivity();
			DateTime currTime = new DateTime(TimestampUtils.convertToMilliSeconds(entry.getValue()));
			if(userCurrLast == null || currTime.isAfter(userCurrLast)){
				updateUserLastActivity(entry.getKey(), currTime);
			}
		}
	}

	@Override
	@Deprecated
	public void updateUsersLastActivityGeneralAndPerType(LogEventsEnum eventId, Map<String, Long> userLastActivityMap) {

		// Go over map of updates
		for (Entry<String, Long> entry : userLastActivityMap.entrySet()) {

			// get user by username
			String username = entry.getKey();
			User user = userRepository.getLastActivityByUserName(eventId, username);
			if (user == null) {
				continue;
			}

			// get the time of the event
			DateTime currTime = new DateTime(TimestampUtils.convertToMilliSeconds(entry.getValue()));

			Update update = null;

			// last activity
			DateTime userCurrLast = user.getLastActivity();
			if (userCurrLast == null || currTime.isAfter(userCurrLast)) {
				update = new Update();
				update.set(User.lastActivityField, currTime);
			}

			// Last activity of data source
			userCurrLast = user.getLogLastActivity(eventId);
			if (userCurrLast == null || currTime.isAfter(userCurrLast)) {
				if (update == null) update = new Update();
				update.set(User.getLogLastActivityField(eventId), currTime);
			}

			// update user
			if (update != null) {
				mongoTemplate.updateFirst(query(where(User.usernameField).is(username)), update, User.class);
			}
		}

	}

	@Override
	public void updateUsersInfo(String username, Map<String, JksonSerilaizablePair<Long,String>> userInfo,Map<String,Boolean> dataSourceUpdateOnlyFlagMap) {


		// get user by username
		User user = userRepository.getLastActivityAndLogUserNameByUserName(username);



		if (user == null) {

			//in case that this user not need to be create in mongo (doesnt have data source info that related to OnlyUpdate flag = false)
			if (udpateOnly(userInfo,dataSourceUpdateOnlyFlagMap)) {
				logger.warn("Can't find user {} - Not going to update last activity and user info", username);
				return;
			}

			Classifier classifier = getFirstClassifier(userInfo,dataSourceUpdateOnlyFlagMap);
			String logUsernameValue = userInfo.get(classifier.getId()).getValue();


			// need to create the user at mongo
			user = createUser(classifier.getUserApplication(), username, logUsernameValue);

			saveUser(user);
			if(user == null || user.getId() == null) {
				logger.info("Failed to save {} user with normalize username ({}) and log username ({})", classifier, username, logUsernameValue);
			}


		}

		DateTime userCurrLast = user.getLastActivity();



		try {

			Update update = null;



			for (String classifierId : userInfo.keySet()) {

				// get the time of the event
				DateTime currTime = new DateTime(userInfo.get(classifierId).getKey(), DateTimeZone.UTC);
				LogEventsEnum logEventsEnum = LogEventsEnum.valueOf(classifierId);
				String logUsernameValue = userInfo.get(classifierId).getValue();


				// last activity
				if (userCurrLast == null || currTime.isAfter(userCurrLast)) {
					if (update == null)
						update = new Update();
					update.set(User.lastActivityField, currTime);
					userCurrLast = currTime;
				}

				// Last activity of data source
				DateTime userCurrLastOfType = user.getLogLastActivity(logEventsEnum);
				if (userCurrLastOfType == null || currTime.isAfter(userCurrLastOfType)) {
					if (update == null)
						update = new Update();
					update.set(User.getLogLastActivityField(logEventsEnum), currTime);
				}


				//update the logusername if needed
				boolean isLogUserNameExist = user.containsLogUsername(usernameService.getLogname(logEventsEnum));

				if (!isLogUserNameExist)
				{

					if (update == null)
						update = new Update();
					update.set(User.getLogUserNameField(usernameService.getLogname(logEventsEnum)), logUsernameValue);
				}

			}


			// update user
			if (update != null) {
				mongoTemplate.updateFirst(query(where(User.usernameField).is(username)), update, User.class);
			}

		} catch (Exception e) {
			logger.error("Failed to update last activity of user {} : {}", username, e.getMessage());
		}

	}

	/**
	 * This method will determine if fir that user we need to only update the mongo or also create the user if needed
	 * @param userInfo - Map: <DataSource,Pair <lastActivity,logUserName>>
	 * @param dataSourceUpdateOnlyFlagMap - Map: <DataSource,update only flag>
	 * @return - boolean need to only update or not
	 */
	private boolean  udpateOnly(Map<String, JksonSerilaizablePair<Long,String>> userInfo,Map<String,Boolean> dataSourceUpdateOnlyFlagMap){
		boolean result = true;

		for (Entry<String, JksonSerilaizablePair<Long,String>> entry : userInfo.entrySet() )
		{
			if (!dataSourceUpdateOnlyFlagMap.get(entry.getKey()))
			{
				return false;
			}
		}
		return result;


	}

	/**
	 * This method will return the earliest event classifier that trigger new user creation
	 * @param userInfo - Map: <DataSource,Pair <lastActivity,logUserName>>
	 * @param dataSourceUpdateOnlyFlagMap - Map: <DataSource,update only flag>
	 * @return - the Classifier of the win event
	 */
	private Classifier getFirstClassifier(Map<String, JksonSerilaizablePair<Long,String>> userInfo,Map<String,Boolean> dataSourceUpdateOnlyFlagMap)
	{
		Classifier result = null;
		Entry<String, JksonSerilaizablePair<Long,String>> earlierEntry = null;

		for (Entry<String, JksonSerilaizablePair<Long,String>> entry : userInfo.entrySet() )
		{
			if (!dataSourceUpdateOnlyFlagMap.get(entry.getKey()))
			{
				if (earlierEntry == null  || earlierEntry.getValue().getKey() > entry.getValue().getKey())
					earlierEntry = entry;
			}
		}

		result = earlierEntry != null ? Classifier.valueOf(earlierEntry.getKey()) : null;
		return result;
	}

	@Override
	public String getUserThumbnail(User user) {
		String ret = null;
		
		PageRequest pageRequest = new PageRequest(0, 1, Direction.DESC, AdUserThumbnail.CREATED_AT_FIELD_NAME);
		List<AdUserThumbnail> adUserThumbnails = adUserThumbnailRepository.findByObjectGUID(user.getAdInfo().getObjectGUID(), pageRequest);
		if(adUserThumbnails.size() > 0){
			ret = adUserThumbnails.get(0).getThumbnailPhoto();
		}
		
		return ret;
	}

	@Override
	public void removeClassifierFromAllUsers(String classifierId) {
		int numOfPages = (int)(((userRepository.count() - 1) / userServiceImplPageSize) + 1);

		for (int i = 0; i < numOfPages; i++) {
			PageRequest pageRequest = new PageRequest(i, userServiceImplPageSize);
			List<User> listOfUsers = userRepository.findAllExcludeAdInfo(pageRequest);
			for (User user : listOfUsers)
				user.removeClassifierScore(classifierId);
			saveUsers(listOfUsers);
		}
	}

	@Override
	public void updateUserWithCurrentADInfo() {
		Long timestampepoc = adUserRepository.getLatestTimeStampepoch();
		if(timestampepoc != null) {
			updateUserWithADInfo(timestampepoc);
		} else {
			logger.warn("no timestamp. probably the ad_user table is empty");
		}
		
	}

	@Override
	public void updateUserWithADInfo(final Long timestampepoch) {
		logger.info("Starting to update users with ad info.");

		int numOfPages = (int)(((adUserRepository.count() - 1) / userServiceImplPageSize) + 1);
		for (int i = 0; i < numOfPages; i++) {
			PageRequest pageRequest = new PageRequest(i, userServiceImplPageSize);
			Iterable<AdUser> listOfAdUsers = adUserRepository.findByTimestampepoch(timestampepoch, pageRequest);
			for (AdUser adUser : listOfAdUsers)
				updateUserWithADInfo(adUser);
		}

		logger.info("Finished updating users with ad info.");
	}

	@Override
	public void updateUserWithADInfo(AdUser adUser) {
		if(adUser.getObjectGUID() == null) {
			logger.warn("got ad user with no ObjectGUID name field.");
			return;
		}
		if(adUser.getDistinguishedName() == null) {
			logger.warn("got ad user with no distinguished name field.");
			return;
		}
		
		
		User user =  findUserByObjectGUID(adUser.getObjectGUID());
		Date whenChanged = null;
		try {
			if(!StringUtils.isEmpty(adUser.getWhenChanged())){
				whenChanged = adUserParser.parseDate(adUser.getWhenChanged());
			}
		} catch (ParseException e) {
			logger.error(String.format("got and exception while trying to parse active directory when changed field (%s)",adUser.getWhenChanged()), e);
		}

		// calculate user groups so we can use it in the skip logic below and in the user update code further down this method
		Set<AdUserGroup> groups = calculateUserGroups(adUser);

		// calculate the user direct reports so we can use it in the skip logic below and in the user update code further down this method
		Set<AdUserDirectReport> directReports = calculateDirectReports(adUser);

		// skip when the existing user's when-changed is newer then the AD-user's
		if(user != null && whenChanged != null && user.getAdInfo().getWhenChanged() != null && !user.getAdInfo().getWhenChanged().before(whenChanged)){
			// check if the groups did not changed, since this property does not updates the whenChanged field
			// 1. build a AdUserGroup set from the adUser instance
			// 2. see that the two sets are equal
			// 3. do the same for the direct reports
			if (groups.equals(user.getAdInfo().getGroups()) && directReports.equals(user.getAdInfo().getDirectReports())) {
				// skip user if groups and direct reports did not change
				return;
			}
		}
		
		final UserAdInfo userAdInfo = new UserAdInfo();
		userAdInfo.setObjectGUID(adUser.getObjectGUID());
		userAdInfo.setDn(adUser.getDistinguishedName());
		userAdInfo.setFirstname(adUser.getGivenName());
		userAdInfo.setLastname(adUser.getSn());
		if(adUser.getMail() != null && adUser.getMail().length() > 0){
			userAdInfo.setEmailAddress(new EmailAddress(adUser.getMail()));
		}
		userAdInfo.setUserPrincipalName(adUser.getUserPrincipalName());
		userAdInfo.setsAMAccountName(adUser.getsAMAccountName());
		
		
		
		userAdInfo.setEmployeeID(adUser.getEmployeeID());
		userAdInfo.setEmployeeNumber(adUser.getEmployeeNumber());
		userAdInfo.setManagerDN(adUser.getManager());
		userAdInfo.setMobile(adUser.getMobile());
		userAdInfo.setTelephoneNumber(adUser.getTelephoneNumber());
		userAdInfo.setOtherFacsimileTelephoneNumber(adUser.getOtherFacsimileTelephoneNumber());
		userAdInfo.setOtherHomePhone(adUser.getOtherHomePhone());
		userAdInfo.setOtherMobile(adUser.getOtherMobile());
		userAdInfo.setOtherTelephone(adUser.getOtherTelephone());
		userAdInfo.setHomePhone(adUser.getHomePhone());
		userAdInfo.setDepartment(adUser.getDepartment());
		userAdInfo.setPosition(adUser.getTitle());
		userAdInfo.setDisplayName(adUser.getDisplayName());
		userAdInfo.setLogonHours(adUser.getLogonHours());

		userAdInfo.setWhenChanged(whenChanged);
		
		try {
			if(!StringUtils.isEmpty(adUser.getWhenCreated())){
				userAdInfo.setWhenCreated(adUserParser.parseDate(adUser.getWhenCreated()));
			}
		} catch (ParseException e) {
			logger.error(String.format("got and exception while trying to parse active directory when created field (%s)",adUser.getWhenCreated()), e);
		}
		
		userAdInfo.setDescription(adUser.getDescription());
		userAdInfo.setStreetAddress(adUser.getStreetAddress());
		userAdInfo.setCompany(adUser.getCompany());
		userAdInfo.setC(adUser.getC());
		userAdInfo.setDivision(adUser.getDivision());
		userAdInfo.setL(adUser.getL());
		userAdInfo.setO(adUser.getO());
		userAdInfo.setRoomNumber(adUser.getRoomNumber());
		if(!StringUtils.isEmpty(adUser.getAccountExpires()) && !adUser.getAccountExpires().equals("0") && !adUser.getAccountExpires().startsWith("30828")){
			try {
				userAdInfo.setAccountExpires(adUserParser.parseDate(adUser.getAccountExpires()));
			} catch (ParseException e) {
				logger.error(String.format("got and exception while trying to parse active directory account expires field (%s)",adUser.getAccountExpires()), e);
			}
		}
		userAdInfo.setUserAccountControl(adUser.getUserAccountControl());
		userAdInfo.setIsAccountDisabled(adUserParser.isAccountIsDisabled(userAdInfo.getUserAccountControl()));
		
		// update user's groups
		userAdInfo.setGroups(groups);

		// update user's direct reports
		userAdInfo.setAdDirectReports(directReports);

		DateTime disableAccountTime = null;
		if (userAdInfo.getIsAccountDisabled()){
			if (user == null || !user.getAdInfo().getIsAccountDisabled()){
				disableAccountTime = new DateTime(whenChanged);
			} else{
				disableAccountTime = user.getAdInfo().getDisableAccountTime();
			}
		}
		userAdInfo.setDisableAccountTime(disableAccountTime);
				
		boolean isSaveUser = false;
		if(user == null){
			user = new User();
			isSaveUser = true;
		}
		
		user.setAdInfo(userAdInfo);
		
		String username = adUser.getUserPrincipalName();
		if(StringUtils.isEmpty(username)) {
			username = adUser.getsAMAccountName();
		}
		
		if(!StringUtils.isEmpty(username)) {
			username = username.toLowerCase();
		} else{
			logger.error("ad user does not have ad user principal name and no sAMAcountName!!! dn: {}", adUser.getDistinguishedName());
		}
		
		
		
		final String searchField = createSearchField(userAdInfo, username);
		
		String noDomainUsername = null;
		if(!StringUtils.isEmpty(username)) {
			noDomainUsername = StringUtils.split(username, '@')[0];
		}
		if(isSaveUser){
			if(!StringUtils.isEmpty(username)) {
				user.setUsername(username);
				user.setNoDomainUsername(noDomainUsername);
				user.addApplicationUserDetails(createApplicationUserDetails(UserApplication.active_directory, user.getUsername()));
			}
			user.setSearchField(searchField);
			user.setWhenCreated(userAdInfo.getWhenCreated());
			saveUser(user);
		} else{
			Update update = new Update();
			update.set(User.adInfoField, userAdInfo);
			update.set(User.whenCreatedField, userAdInfo.getWhenCreated());
			if(!StringUtils.isEmpty(username) && !username.equals(user.getUsername())){
				update.set(User.usernameField, username);
			}
			if(!StringUtils.isEmpty(noDomainUsername) && !noDomainUsername.equals(user.getNoDomainUsername())){
				update.set(User.noDomainUsernameField, noDomainUsername);
			}
			if(!searchField.equals(user.getSearchField())){
				update.set(User.searchFieldName, searchField);
			}
			updateUser(user, update);
		}
	}


	private  Set<AdUserDirectReport> calculateDirectReports(AdUser adUser) {
		Set<AdUserDirectReport> reports = new HashSet<>();

		for (String directReportsDN : ADParser.getDirectReports(adUser.getDirectReports())) {
			String displayName = ADParser.parseFirstCNFromDN(directReportsDN);
			reports.add(new AdUserDirectReport(directReportsDN, displayName));
		}
		return reports;
	}


	private Set<AdUserGroup> calculateUserGroups(AdUser adUser) {
		Set<AdUserGroup> groups = new HashSet<>();

		for (String groupDN : ADParser.getUserGroups(adUser.getMemberOf())) {
			String groupName = groupDnToNameMap.get(groupDN);
			if(groupName == null) {
				groupName = ADParser.parseFirstCNFromDN(groupDN);
			}
			if(groupName == null){
				Log.warn("invalid group dn ({}) for user ({})", groupDN, adUser.getDistinguishedName());
				continue;
			}

			groups.add(new AdUserGroup(groupDN, groupName));
		}

		return groups;
	}

	private User findUserByObjectGUID(String objectGUID){
		return userRepository.findByObjectGUID(objectGUID);
	}
	
//	private void updateUser(User user, String fieldName, Object val){
//		mongoTemplate.updateFirst(query(where(User.ID_FIELD).is(user.getId())), update(fieldName, val), User.class);
//	}
	
	@Override
	public void updateUser(User user, Update update){
		if(user.getId() != null){
			mongoTemplate.updateFirst(query(where(User.ID_FIELD).is(user.getId())), update, User.class);
		}
	}
	
	public void updateUser(String userId, Update update){
		mongoTemplate.updateFirst(query(where(User.ID_FIELD).is(userId)), update, User.class);
	}
	
	private String createSearchField(UserAdInfo userAdInfo, String username){
		StringBuilder sb = new StringBuilder();
		if(userAdInfo != null){
			if(userAdInfo.getFirstname() != null && userAdInfo.getFirstname().length() > 0){
				if(userAdInfo.getLastname() != null && userAdInfo.getLastname().length() > 0){
					sb.append(SEARCH_FIELD_PREFIX).append(userAdInfo.getFirstname().toLowerCase()).append(" ").append(userAdInfo.getLastname().toLowerCase());
					sb.append(SEARCH_FIELD_PREFIX).append(userAdInfo.getLastname().toLowerCase()).append(" ").append(userAdInfo.getFirstname().toLowerCase());
				} else{
					sb.append(SEARCH_FIELD_PREFIX).append(userAdInfo.getFirstname().toLowerCase());
				}
			}else{
				if(userAdInfo.getLastname() != null && userAdInfo.getLastname().length() > 0){
					sb.append(SEARCH_FIELD_PREFIX).append(SEARCH_FIELD_PREFIX).append(userAdInfo.getLastname().toLowerCase());
				}
			}
		}
		
		if(!StringUtils.isEmpty(username)){
			sb.append(SEARCH_FIELD_PREFIX).append(username);
		}
		return sb.toString();
	}

	@Override
	public List<User> findBySearchFieldContaining(String prefix, int page, int size) {
		
		return userRepository.findBySearchFieldContaining(SEARCH_FIELD_PREFIX + prefix.toLowerCase(), new PageRequest(page, size));
	}

	private String getUserNameFromID(String uid) {
		User user = userRepository.findOne(uid);
		if(user == null){
			throw new UnknownResourceException(String.format("user with id [%s] does not exist", uid));
		}
		
		return user.getUsername();
	}
	
	

	@Override
	public List<UserMachine> getUserMachines(String uid) {
		String userName = getUserNameFromID(uid);
		List<UserMachine> userMachines = userMachineDAO.findByUsername(userName);
		List<String> machinesNames = new ArrayList<String>();
		for(UserMachine userMachine : userMachines){
			machinesNames.add(userMachine.getHostname().toUpperCase());
		}
		// get from computers repository
		List<Computer> computers = computerRepository.getComputersFromNames(machinesNames);
		for (Computer comp : computers){
			for(UserMachine machine : userMachines){
				if(machine.getHostname().toUpperCase().equals(comp.getName())){
					machine.setIsSensitive(comp.getIsSensitive());
					machine.setOperatingSystem(comp.getOperatingSystem());
					machine.setUsageClassifiers(comp.getUsageClassifiers());
				}
			}
		}
		return userMachines;
	}
	
	
	@Override
	public String findByNormalizedUserName(String normalizedUsername) {
		return userRepository.getUserIdByNormalizedUsername(normalizedUsername);
	}

	@Override
	public boolean findIfUserExists(String username) {
		return userRepository.findIfUserExists(username);
	}

	@Override
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
	
	
	
	
	
	@Override
	public User findByUserId(String userId){
		return userRepository.findOne(userId);
	}
	
	
	
	
	@Override
	public boolean createNewApplicationUserDetails(User user, UserApplication userApplication, String username, boolean isSave){
		return createNewApplicationUserDetails(user, createNewApplicationUserDetails(userApplication, username), isSave);
	}
	
	private ApplicationUserDetails createNewApplicationUserDetails(UserApplication userApplication, String username){
		return new ApplicationUserDetails(userApplication.getId(), username);
	}
	
	public boolean createNewApplicationUserDetails(User user, ApplicationUserDetails applicationUserDetails, boolean isSave) {
		boolean isNewVal = false;
		if(!user.containsApplicationUserDetails(applicationUserDetails)){
			user.addApplicationUserDetails(applicationUserDetails);
			isNewVal = true;
		}
		
		if(isSave && isNewVal){
			saveUser(user);
		}
		
		return isNewVal;
	}
	
	@Override
	public ApplicationUserDetails createApplicationUserDetails(UserApplication userApplication, String username) {
		return new ApplicationUserDetails(userApplication.getId(), username);
	}
	
	@Override
	public ApplicationUserDetails getApplicationUserDetails(User user, UserApplication userApplication) {
		return user.getApplicationUserDetails().get(userApplication.getId());
	}

	

	@Override
	public List<User> findByApplicationUserName(
			UserApplication userApplication, List<String> usernames) {
		return userRepository.findByApplicationUserName(userApplication.getId(), usernames);
	}
	
	public PropertiesDistribution getDestinationComputerPropertyDistribution(String uid, String propertyName, Long latestDate, Long earliestDate, int maxValues, int minScore) {
		// get the destinations from 4769 events and ssh events
		Map<String, EventsToMachineCount> destinationsCount = new HashMap<String, EventsToMachineCount>();
		
		String username = getUserNameFromID(uid);
		
		addEventsToMachineCountToMap(destinationsCount, loginDAO.getEventsToTargetMachineCount(username, latestDate,earliestDate, minScore));
		addEventsToMachineCountToMap(destinationsCount, sshDAO.getEventsToTargetMachineCount(username, latestDate,earliestDate, minScore));
	
		// create a properties distribution object
		PropertiesDistribution distribution = new PropertiesDistribution(propertyName);
		
		// go over the computers returned by events and get the operating system for each one
		int numberOfDestinations = 0;
		for (EventsToMachineCount destMachine : destinationsCount.values()) {
			Computer computer = computerRepository.getComputerWithPartialFields(destMachine.getHostname().toUpperCase(), propertyName);
			distribution.incValueCount(computer.getPropertyValue(propertyName).toString(), destMachine.getEventsCount());
			numberOfDestinations++;
			
			// in case we return more than a certain amount of values distribution, mark result as not conclusive
			if (numberOfDestinations > maxValues) {
				distribution.setConclusive(false);
				break;
			}
		}
		
		// calculate distribution for every operating systems and return the result
		if (distribution.isConclusive())
			distribution.calculateValuesDistribution();
		
		return distribution;
	}
	
	private void addEventsToMachineCountToMap(Map<String, EventsToMachineCount> total, List<EventsToMachineCount> toAdd) {
		for (EventsToMachineCount machine : toAdd) {
			String hostname = machine.getHostname();
			if (StringUtils.isNotEmpty(hostname)) {
				if (total.containsKey(hostname)) {
					total.get(hostname).incEventsCount(machine.getEventsCount());
				} else {
					total.put(hostname, machine);
				}
			}
		}
	}
	
	

	@Override
	public void fillUpdateUserScore(Update update, User user, Classifier classifier) {
		update.set(User.getClassifierScoreField(classifier.getId()), user.getScore(classifier.getId()));
	}

	@Override
	public DateTime findLastActiveTime(LogEventsEnum eventId){
		User user = userRepository.findLastActiveUser(eventId);
		return user == null ? null : user.getLogLastActivity(eventId);
	}
	
	
	public void updateTags(String username, Map<String, Boolean> tagSettings) {
		
		// construct lists of tags to remove and tags to add from the map
		List<String> tagsToAdd = new LinkedList<String>();
		List<String> tagsToRemove = new LinkedList<String>();
		for (String tag : tagSettings.keySet()) {
			if (tagSettings.get(tag)) 
				tagsToAdd.add(tag);
			else
				tagsToRemove.add(tag);
		}
		updateTags(username, tagsToAdd, tagsToRemove);
	}

	private void updateTags(String username, List<String> tagsToAdd, List<String> tagsToRemove){
		// call the repository to update mongodb with the tags settings
		userRepository.syncTags(username, tagsToAdd, tagsToRemove);
		//also update the tags cache with the new updates
		List<String> tags = userTagsCache.get(username);
		Set<String> tagSet = new HashSet<String>();
		if (tags!=null) {
			tagSet = new HashSet<String>(tags);
		}
		if (tagsToAdd != null)
			tagSet.addAll(tagsToAdd);
		if (tagsToRemove != null)
			tagSet.removeAll(tagsToRemove);


		tags = new ArrayList<String>(tagSet);


		userTagsCache.put(username, tags);
	}


	@Override
	public boolean isUserTagged(String username, String tag) {
		// check if the user tags are kept in cache
		List<String> tags = userTagsCache.get(username);
		if (tags==null) {
			// get tags from mongodb and add to cache
			Set<String> tagSet = userRepository.getUserTags(username);
			if (tagSet != null) {
				tags = new ArrayList<String>(tagSet);
				userTagsCache.put(username, tags);
			}
		}
		return tags!=null & tags.contains(tag);
	}

	@Override public CacheHandler getCache() {
		return userTagsCache;
	}

	@Override public void setCache(CacheHandler cache) {
		userTagsCache = cache;
	}

	@Override
	public Set<String> findNamesInGroup(List<String> groupsToTag, Pageable pageable) {
		return userRepository.findByUserInGroup(groupsToTag, pageable);
	}

	@Override
	public Set<String> findNamesInOU(List<String> ousToTag, Pageable pageable) {
		return userRepository.findByUserInOU(ousToTag, pageable);
	}

	public String findAdMembers(String adName) {
		return adGroupRepository.findByName(adName);
	}

	public List<AdGroup> getActiveDirectoryGroups(int maxNumberOfReturnElements){
		return adGroupRepository.getActiveDirectoryGroups(maxNumberOfReturnElements);
	}

	@Override
	public Set<String> findNamesByTag(String tagFieldName, Boolean value) {
		Set<String> namesByTag = new HashSet<String>();
		int numOfPages = (int)(((userRepository.count() - 1) / userServiceImplPageSize) + 1);
		for (int i = 0; i < numOfPages; i++) {
			PageRequest pageRequest = new PageRequest(i, userServiceImplPageSize);
			namesByTag.addAll(userRepository.findNameByTag(tagFieldName, value, pageRequest));
		}
		return namesByTag;
	}

	@Override
	public Set<String> findNamesByTag(String tagFieldName, String value) {
		Set<String> namesByTag = new HashSet<String>();
		int numOfPages = (int)(((userRepository.count() - 1) / userServiceImplPageSize) + 1);
		for (int i = 0; i < numOfPages; i++) {
			PageRequest pageRequest = new PageRequest(i, userServiceImplPageSize);
			namesByTag.addAll(userRepository.findNameByTag(tagFieldName, value, pageRequest));
		}
		return namesByTag;
	}


	@Override
	public void updateUserTag(String tagField, String userTagEnumId, String username, boolean value){
		userRepository.updateUserTag(tagField, username, value);
		List<String> tagsToAdd = new ArrayList<>();
		List<String> tagsToRemove = new ArrayList<>();
		if (value) {
			tagsToAdd.add(userTagEnumId);
		}
		else{
			tagsToRemove.add(userTagEnumId);
		}
		userRepository.syncTags(username, tagsToAdd, tagsToRemove);
		//also update the tags cache with the new updates
		List<String> tags = userTagsCache.get(username);
		if (tags == null){
			tags = new ArrayList<String>();
		}
		if (value) {
			if (!tags.contains(tagField)) {
				tags.add(tagField);
			}
		}
		else {
			tags.remove(tagField);
		}
		userTagsCache.put(username, tags);
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
