package fortscale.services.impl;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.mortbay.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import fortscale.domain.ad.AdUser;
import fortscale.domain.ad.AdUserGroup;
import fortscale.domain.ad.AdUserThumbnail;
import fortscale.domain.ad.UserMachine;
import fortscale.domain.ad.dao.AdGroupRepository;
import fortscale.domain.ad.dao.AdUserRepository;
import fortscale.domain.ad.dao.AdUserThumbnailRepository;
import fortscale.domain.ad.dao.UserMachineDAO;
import fortscale.domain.core.AdUserDirectReport;
import fortscale.domain.core.ApplicationUserDetails;
import fortscale.domain.core.EmailAddress;
import fortscale.domain.core.User;
import fortscale.domain.core.UserAdInfo;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.events.LogEventsEnum;
import fortscale.domain.fe.dao.AuthDAO;
import fortscale.domain.fe.dao.VpnDAO;
import fortscale.services.UserApplication;
import fortscale.services.UserService;
import fortscale.services.exceptions.UnknownResourceException;
import fortscale.services.fe.Classifier;
import fortscale.utils.TimestampUtils;
import fortscale.utils.actdir.ADParser;
import fortscale.utils.logging.Logger;

@Service("userService")
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
	private UserMachineDAO userMachineDAO;
	
	@Autowired
	private AuthDAO loginDAO;
	
	@Autowired
	private AuthDAO sshDAO;
	
	@Autowired
	private VpnDAO vpnDAO;
	
	@Autowired
	private ImpalaWriterFactory impalaWriterFactory;
	
	@Autowired
	private UsernameService usernameService;
	
	
	@Autowired 
	private ADParser adUserParser; 
	
	
	
	@Value("${ad.info.update.read.page.size:1000}")
	private int readPageSize;
	
	
	private Map<String, String> groupDnToNameMap = new HashMap<>();
	
	
	
	@Override
	public User createUser(UserApplication userApplication, String username, String appUsername){
		User user = new User();
		user.setUsername(username);
		user.setSearchField(createSearchField(null, username));
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
		String userId = usernameService.getUserId(normalizedUsername, true, eventId);
		if(userId == null && onlyUpdate){
			return;
		}
			
		if(userId != null){
			if(!usernameService.isLogUsernameExist(eventId, logUsername, userId, true)){
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
			user = userRepository.save(user);
			if(user == null || user.getId() == null){
				logger.info("Failed to save {} user with normalize username ({}) and log username ({})", classifier, normalizedUsername, logUsername);
			} else{
				usernameService.addLogNormalizedUsername(eventId, user.getId(), normalizedUsername);
				usernameService.addLogUsername(eventId, logUsername,user.getId());
			}
		}		
	}
	
	@Override
	public void updateUserLastActivity(LogEventsEnum eventId, String username, DateTime dateTime){
		Update update = new Update();
		update.set(User.getLogLastActivityField(eventId), dateTime);
		mongoTemplate.updateFirst(query(where(User.usernameField).is(username)), update, User.class);
	}
	
	@Override
	public void updateUsersLastActivity(LogEventsEnum eventId, Map<String, Long> userLastActivityMap){
		Iterator<Entry<String, Long>> entries = userLastActivityMap.entrySet().iterator();
		while(entries.hasNext()){
			Entry<String, Long> entry = entries.next();
			updateUserLastActivity(eventId, entry.getKey(), new DateTime(TimestampUtils.convertToMilliSeconds(entry.getValue())));
		}
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
	public void removeClassifierFromAllUsers(String classifierId){
		if(!Classifier.ad.getId().equals(classifierId)){
			return;
		}
		
		List<User> users = userRepository.findAll();
		for(User user: users){
			user.removeClassifierScore(classifierId);
		}
		
		userRepository.save(users);
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
		
		Iterable<AdUser> adUsers = adUserRepository.findByTimestampepoch(timestampepoch);
		for(AdUser adUser: adUsers){
			updateUserWithADInfo(adUser);
		}
		
		logger.info("finished updating users with ad info.");
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
		
		if(user != null && whenChanged != null && !user.getAdInfo().getWhenChanged().before(whenChanged)){
			return;
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
		Boolean isAccountDisable = null;
		try {
			isAccountDisable = userAdInfo.getUserAccountControl() != null ? adUserParser.isAccountIsDisabled(userAdInfo.getUserAccountControl()) : null;
		} catch (NumberFormatException e) {
			logger.warn("got NumberFormatException while trying to parse user account control.", userAdInfo.getUserAccountControl());
		}
		userAdInfo.setIsAccountDisabled(isAccountDisable);
		
		ADParser adUserParser = new ADParser();
		String[] groups = adUserParser.getUserGroups(adUser.getMemberOf());
		if(groups != null){
			for(String groupDN: groups){
				String groupName = groupDnToNameMap.get(groupDN);
				if(groupName == null){
//					AdGroup adGroup = adGroupRepository.findByDistinguishedName(groupDN);
//					if(adGroup != null){
//						groupName = adGroup.getName();
//					}else{
//						Log.warn("the user ({}) group ({}) was not found", adUser.getDistinguishedName(), groupDN);
						groupName = adUserParser.parseFirstCNFromDN(groupDN);
						if(groupName == null){
							Log.warn("invalid group dn ({}) for user ({})", groupDN, adUser.getDistinguishedName());
							continue;
						}
//					}
					groupDnToNameMap.put(groupDN, groupName);
				}
				userAdInfo.addGroup(new AdUserGroup(groupDN, groupName));
			}
		}
		
		String[] directReports = adUserParser.getDirectReports(adUser.getDirectReports());
		if(directReports != null){
			for(String directReportsDN: directReports){
				String displayName = adUserParser.parseFirstCNFromDN(directReportsDN);
				AdUserDirectReport adUserDirectReport = new AdUserDirectReport(directReportsDN, displayName);
				userAdInfo.addDirectReport(adUserDirectReport);
			}
		}
		
		DateTime disableAccountTime = null;
		if(userAdInfo.getIsAccountDisabled() != null && userAdInfo.getIsAccountDisabled()){
			if(user == null || !user.getAdInfo().getIsAccountDisabled()){
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
			
			userRepository.save(user);			
		} else{
			Update update = new Update();
			update.set(User.adInfoField, userAdInfo);
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
		
		return userRepository.findBySearchFieldContaining(SEARCH_FIELD_PREFIX+prefix.toLowerCase(), new PageRequest(page, size));
	}

	
	
	

	@Override
	public List<UserMachine> getUserMachines(String uid) {
		User user = userRepository.findOne(uid);
		if(user == null){
			throw new UnknownResourceException(String.format("user with id [%s] does not exist", uid));
		}
		
		String userName = user.getUsername();
		return userMachineDAO.findByUsername(userName);
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
		return createNewApplicationUserDetails(user,createNewApplicationUserDetails(userApplication, username), isSave);
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
			userRepository.save(user);
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
	
	
	
	
	

	@Override
	public void fillUpdateUserScore(Update update, User user, Classifier classifier) {
		update.set(User.getClassifierScoreField(classifier.getId()), user.getScore(classifier.getId()));
	}

	@Override
	public DateTime findLastActiveTime(LogEventsEnum eventId){
		User user = userRepository.findLastActiveUser(eventId);
		return user == null ? null : user.getLogLastActivity(eventId);
	}
	
}
