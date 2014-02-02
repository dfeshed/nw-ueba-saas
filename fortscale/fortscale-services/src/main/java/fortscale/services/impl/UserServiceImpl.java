package fortscale.services.impl;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mortbay.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import fortscale.domain.ad.AdGroup;
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
import fortscale.domain.core.ClassifierScore;
import fortscale.domain.core.EmailAddress;
import fortscale.domain.core.User;
import fortscale.domain.core.UserAdInfo;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.fe.VpnScore;
import fortscale.domain.fe.dao.AuthDAO;
import fortscale.domain.fe.dao.VpnDAO;
import fortscale.services.LogEventsEnum;
import fortscale.services.UserApplication;
import fortscale.services.UserService;
import fortscale.services.exceptions.UnknownResourceException;
import fortscale.services.fe.Classifier;
import fortscale.utils.actdir.ADUserParser;
import fortscale.utils.logging.Logger;

@Service("userService")
public class UserServiceImpl implements UserService{
	private static Logger logger = Logger.getLogger(UserServiceImpl.class);
	
	private static final String SEARCH_FIELD_PREFIX = "##";
	private static final String REGEX_SEPERATOR = "####";
	
	
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
	private ADUserParser adUserParser; 
	
	@Value("${vpn.to.ad.username.regex.format:^%s*(?i)}")
	private String vpnToAdUsernameRegexFormat;
	
	@Value("${auth.to.ad.username.regex.format:^%s*(?i)}")
	private String authToAdUsernameRegexFormat;
	
	@Value("${ssh.to.ad.username.regex.format:^%s*(?i)}")
	private String sshToAdUsernameRegexFormat;
	
	
	
	@Value("${ad.info.update.read.page.size:1000}")
	private int readPageSize;
	
	
	private Map<String, String> groupDnToNameMap = new HashMap<>();
	
	
	
	@Override
	public User createUser(UserApplication userApplication, String username){
		User user = new User();
		user.setUsername(username);
		user.setSearchField(createSearchField(null, username));
		createNewApplicationUserDetails(user, new ApplicationUserDetails(userApplication.getId(), username), false);
		return user;
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
		if(!mongoTemplate.exists(query(where(User.adInfoField).exists(true)), User.class) && userRepository.count() > 0){
			logger.info("Updating User schema regarding to the active directory info");
			Iterable<AdUser> adUsers = adUserRepository.findByTimestampepoch(timestampepoch);
			List<User> users = updateUserWithADInfoNewSchema(adUsers);
			
			try{
				logger.info("Dropping adDn index");
				mongoTemplate.indexOps(User.class).dropIndex("adDn");
			} catch(Exception e){
				logger.error("failed to drop adDn index.", e);
			}
			try{
				logger.info("Dropping adObjectGUID index");
				mongoTemplate.indexOps(User.class).dropIndex("adObjectGUID");
			} catch(Exception e){
				logger.error("failed to drop adObjectGUID index.", e);
			}
			
			userRepository.save(users);
		} else{
			Iterable<AdUser> adUsers = adUserRepository.findByTimestampepoch(timestampepoch);
			for(AdUser adUser: adUsers){
				updateUserWithADInfo(adUser);
			}
		}
//		saveUserIdUsernamesMapToImpala(new Date());
		logger.info("finished updating users with ad info.");
	}
	
	
	
	
	
	
	
	private void saveUserIdUsernamesMapToImpala(Date timestamp){
		List<User> users = userRepository.findAll();
		ImpalaUseridToAppUsernameWriter writer = impalaWriterFactory.createImpalaUseridToAppUsernameWriter();
		writer.write(users, timestamp);
		writer.close();
	}
	
	@Override
	public void updateUserWithADInfo(AdUser adUser) {
		if(adUser.getObjectGUID() == null) {
			logger.error("got ad user with no ObjectGUID name field.");
			return;
		}
		if(adUser.getDistinguishedName() == null) {
			logger.error("got ad user with no distinguished name field.");
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
		try {
			userAdInfo.setWhenChanged(adUserParser.parseDate(adUser.getWhenChanged()));
		} catch (ParseException e) {
			logger.error("got and exception while trying to parse active directory when changed field ({})",adUser.getWhenChanged());
			logger.error("got and exception while trying to parse active directory when changed field",e);
		}
		
		try {
			userAdInfo.setWhenCreated(adUserParser.parseDate(adUser.getWhenCreated()));
		} catch (ParseException e) {
			logger.error("got and exception while trying to parse active directory when created field ({})",adUser.getWhenChanged());
			logger.error("got and exception while trying to parse active directory when created field",e);
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
				logger.error("got and exception while trying to parse active directory account expires field ({})",adUser.getWhenChanged());
				logger.error("got and exception while trying to parse active directory account expires field",e);
			}
		}
		userAdInfo.setUserAccountControl(adUser.getUserAccountControl());
		
		ADUserParser adUserParser = new ADUserParser();
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
		
		User user =  findUserByObjectGUID(adUser.getObjectGUID());
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
		
		
		if(isSaveUser){
			if(!StringUtils.isEmpty(username)) {
				user.setUsername(username);
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
		
		String userName = user.getUsername().split("@")[0];
		return userMachineDAO.findByUsername(userName);
	}
	
	
	
	@Override
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
	
	@Override
	public String getAuthLogUsername(LogEventsEnum eventId, User user){
		AuthDAO authDAO = getAuthDAO(eventId);
		return user.getLogUsernameMap().get(authDAO.getTableName());
	}
	
	private AuthDAO getAuthDAO(LogEventsEnum eventId){
		AuthDAO ret = null;
		switch(eventId){
			case login:
				ret = loginDAO;
				break;
			case ssh:
				ret = sshDAO;
				break;
		default:
			break;
		}
		
		return ret;
	}
	
	@Override
	public String getVpnLogUsername(User user){
		return user.getLogUsernameMap().get(VpnScore.TABLE_NAME);
	}
	
	@Override
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
	
	
	
	@Override
	public User findByVpnUsername(String username){
		return findByUsername(generateUsernameRegexesByVpnUsername(username), username);
	}
	
	private List<String> generateUsernameRegexesByVpnUsername(String vpnUsername){
		List<String> regexes = new ArrayList<>();
		for(String regexFormat: vpnToAdUsernameRegexFormat.split(REGEX_SEPERATOR)){
			regexes.add(String.format(regexFormat, vpnUsername));
		}
		return regexes;
	}
	
	@Override
	public User findByAuthUsername(LogEventsEnum eventId, String username){
		return findByUsername(generateUsernameRegexesByAuthUsername(eventId, username), username);
	}
	
	
	public User findByUsername(List<String> regexes, String username){
		for(String regex: regexes){
			List<User> tmpUsers = userRepository.findByUsernameRegex(regex);
			if(tmpUsers == null || tmpUsers.size() == 0){
				continue;
			}
			if(tmpUsers.size() > 1){
				String tmpUsername = String.format("%s@", username);
				for(User tmpUser: tmpUsers){
					if(tmpUser.getUsername().startsWith(tmpUsername)){
						return tmpUser;
					}
				}
			}else{
				return tmpUsers.get(0);
			}
		}
		return null;
	}
	
	private List<String> generateUsernameRegexesByAuthUsername(LogEventsEnum eventId, String authUsername){
		List<String> regexes = new ArrayList<>();
		String regex = authToAdUsernameRegexFormat;
		if(eventId.equals(LogEventsEnum.ssh)){
			regex = sshToAdUsernameRegexFormat;
		}
		for(String regexFormat: regex.split(REGEX_SEPERATOR)){
			regexes.add(String.format(regexFormat, authUsername));
		}
		return regexes;
	}
	
	
	@Override
	public User findByUserId(String userId){
		return userRepository.findOne(userId);
	}
	
	
	
	
	@Override
	public boolean createNewApplicationUserDetails(User user, UserApplication userApplication, String username, boolean isSave){
		return createNewApplicationUserDetails(user, new ApplicationUserDetails(userApplication.getId(), username), isSave);
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
	public void updateLogUsername(User user, String logname, String username, boolean isSave) {
		user.addLogUsername(logname, username);
		if(isSave){
			userRepository.save(user);
		}
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
	
	
	
	public static class OrderByClassifierScoreTimestempAsc implements Comparator<ClassifierScore>{

		@Override
		public int compare(ClassifierScore o1, ClassifierScore o2) {
			return o1.getTimestamp().compareTo(o2.getTimestamp());
		}
		
	}
	
	
	@Override
	public void updateUserWithCurrentADInfoNewSchema() {
		Long timestampepoch = adUserRepository.getLatestTimeStampepoch();
		if(timestampepoch != null) {
			List<User> users = updateUserWithADInfoNewSchema(adUserRepository.findByTimestampepoch(timestampepoch));
			userRepository.save(users);
			saveUserIdUsernamesMapToImpala(new Date());
		} else {
			logger.error("no timestamp. probably the ad_user table is empty");
		}
		
	}
	
	
	private List<User> updateUserWithADInfoNewSchema(Iterable<AdUser> adUsers) {
		List<User> ret = new ArrayList<>();
		for(AdUser adUser: adUsers){
			try {
				User user = updateUserWithADInfoNewSchema(adUser);
				if(user != null){
					ret.add(user);
				}
			} catch (Exception e) {
				logger.error("got exception while trying to update user with active directory info!!! dn: {}", adUser.getDistinguishedName());
			}
			
		}
		
		
		return ret;
	}
		
	private User updateUserWithADInfoNewSchema(AdUser adUser) {
		if(adUser.getObjectGUID() == null) {
			logger.error("got ad user with no ObjectGUID name field.");
			return null;
		}
		if(adUser.getDistinguishedName() == null) {
			logger.error("got ad user with no distinguished name field.");
			return null;
		}
		
		User user = null;
		if(adUser.getObjectGUID() != null) {
			user = userRepository.findByAdObjectGUID(adUser.getObjectGUID());
		}
		if(user == null){
			user = userRepository.findByAdDn(adUser.getDistinguishedName());
		}
		if(user == null){
			return null;
		}
		
		user.getAdInfo().setFirstname(adUser.getGivenName());
		user.getAdInfo().setLastname(adUser.getSn());
		if(adUser.getMail() != null && adUser.getMail().length() > 0){
			user.getAdInfo().setEmailAddress(new EmailAddress(adUser.getMail()));
		}
		user.getAdInfo().setUserPrincipalName(adUser.getUserPrincipalName());
		user.getAdInfo().setsAMAccountName(adUser.getsAMAccountName());
		String username = adUser.getUserPrincipalName();
		if(StringUtils.isEmpty(username)) {
			username = adUser.getsAMAccountName();
		}
		if(!StringUtils.isEmpty(username)) {
			user.setUsername(username.toLowerCase());
			user.addApplicationUserDetails(createApplicationUserDetails(UserApplication.active_directory, user.getUsername()));
		} else{
			logger.error("ad user does not have ad user principal name and no sAMAcountName!!! dn: {}", adUser.getDistinguishedName());
		}
		
		user.getAdInfo().setEmployeeID(adUser.getEmployeeID());
		user.getAdInfo().setEmployeeNumber(adUser.getEmployeeNumber());
		user.getAdInfo().setManagerDN(adUser.getManager());
		user.getAdInfo().setMobile(adUser.getMobile());
		user.getAdInfo().setTelephoneNumber(adUser.getTelephoneNumber());
		user.getAdInfo().setOtherFacsimileTelephoneNumber(adUser.getOtherFacsimileTelephoneNumber());
		user.getAdInfo().setOtherHomePhone(adUser.getOtherHomePhone());
		user.getAdInfo().setOtherMobile(adUser.getOtherMobile());
		user.getAdInfo().setOtherTelephone(adUser.getOtherTelephone());
		user.getAdInfo().setHomePhone(adUser.getHomePhone());
		user.getAdInfo().setDepartment(adUser.getDepartment());
		user.getAdInfo().setPosition(adUser.getTitle());
		user.getAdInfo().setDisplayName(adUser.getDisplayName());
		user.getAdInfo().setLogonHours(adUser.getLogonHours());
		try {
			user.getAdInfo().setWhenChanged(adUserParser.parseDate(adUser.getWhenChanged()));
		} catch (ParseException e) {
			logger.error("got and exception while trying to parse active directory when changed field ({})",adUser.getWhenChanged());
			logger.error("got and exception while trying to parse active directory when changed field",e);
		}
		
		try {
			user.getAdInfo().setWhenCreated(adUserParser.parseDate(adUser.getWhenCreated()));
		} catch (ParseException e) {
			logger.error("got and exception while trying to parse active directory when created field ({})",adUser.getWhenChanged());
			logger.error("got and exception while trying to parse active directory when created field",e);
		}
		
		user.getAdInfo().setDescription(adUser.getDescription());
		user.getAdInfo().setStreetAddress(adUser.getStreetAddress());
		user.getAdInfo().setCompany(adUser.getCompany());
		user.getAdInfo().setC(adUser.getC());
		user.getAdInfo().setDivision(adUser.getDivision());
		user.getAdInfo().setL(adUser.getL());
		user.getAdInfo().setO(adUser.getO());
		user.getAdInfo().setRoomNumber(adUser.getRoomNumber());
		if(!StringUtils.isEmpty(adUser.getAccountExpires()) && !adUser.getAccountExpires().equals("0") && !adUser.getAccountExpires().startsWith("30828")){
			try {
				user.getAdInfo().setAccountExpires(adUserParser.parseDate(adUser.getAccountExpires()));
			} catch (ParseException e) {
				logger.error("got and exception while trying to parse active directory account expires field ({})",adUser.getWhenChanged());
				logger.error("got and exception while trying to parse active directory account expires field",e);
			}
		}
		user.getAdInfo().setUserAccountControl(adUser.getUserAccountControl());
		
		ADUserParser adUserParser = new ADUserParser();
		String[] groups = adUserParser.getUserGroups(adUser.getMemberOf());
		user.getAdInfo().clearGroups();
		if(groups != null){
			for(String groupDN: groups){
				AdGroup adGroup = adGroupRepository.findByDistinguishedName(groupDN);
				String groupName = null;
				if(adGroup != null){
					groupName = adGroup.getName();
				}else{
					Log.warn("the user ({}) group ({}) was not found", user.getAdInfo().getDn(), groupDN);
					groupName = adUserParser.parseFirstCNFromDN(groupDN);
					if(groupName == null){
						Log.warn("invalid group dn ({}) for user ({})", groupDN, user.getAdInfo().getDn());
						continue;
					}
				}
				user.getAdInfo().addGroup(new AdUserGroup(groupDN, groupName));
			}
		}
		
		String[] directReports = adUserParser.getDirectReports(adUser.getDirectReports());
		user.getAdInfo().clearDirectReport();
		if(directReports != null){
			for(String directReportsDN: directReports){
				User userDirectReport = userRepository.findByAdDn(directReportsDN);
				if(userDirectReport != null){
					String displayName = userDirectReport.getUsername();
					if(userDirectReport.getAdInfo() != null && !StringUtils.isEmpty(userDirectReport.getAdInfo().getDisplayName())){
						displayName = userDirectReport.getAdInfo().getDisplayName();
					}
					AdUserDirectReport adUserDirectReport = new AdUserDirectReport(directReportsDN, displayName);
					adUserDirectReport.setUserId(userDirectReport.getId());
					adUserDirectReport.setUsername(userDirectReport.getUsername());
					if(userDirectReport.getAdInfo() != null){
						adUserDirectReport.setFirstname(userDirectReport.getAdInfo().getFirstname());
						adUserDirectReport.setLastname(userDirectReport.getAdInfo().getLastname());
					}
					
					user.getAdInfo().addDirectReport(adUserDirectReport);
				}else{
					logger.warn("the user ({}) direct report ({}) was not found", user.getAdInfo().getDn(), directReportsDN);
				}
			}
		}
		
		user.setSearchField(createSearchField(user.getAdInfo(), user.getUsername()));
		user.getAdInfo().setObjectGUID(adUser.getObjectGUID());
		user.getAdInfo().setDn(adUser.getDistinguishedName());
		user.setAdDn(null);
		user.setAdObjectGUID(null);

		return user;
	}
	
	
	
	
	
	
	class UpdateUserAdInfoContext{
		private List<AdUser> adUsers = new ArrayList<>();
		private Map<String,User> guidToUsersMap = new HashMap<>();
		private int numOfInserts = 0;
		private int numOfUsernameUpdates = 0;
		private int numOfSearchFieldUpdates = 0;
		private int numOfAdInfoUpdates = 0;
		
		public UpdateUserAdInfoContext(List<AdUser> adUsers, List<User> users){
			this.adUsers = adUsers;
			for(User user: users){
				guidToUsersMap.put(user.getAdInfo().getObjectGUID(), user);
			}
		}
		
		
		public List<AdUser> getAdUsers() {
			return adUsers;
		}
		public void setAdUsers(List<AdUser> adUsers) {
			this.adUsers = adUsers;
		}
		public Map<String,User> getGuidToUsersMap() {
			return guidToUsersMap;
		}
		public void setGuidToUsersMap(Map<String,User> guidToUsersMap) {
			this.guidToUsersMap = guidToUsersMap;
		}
		
		public User findByObjectGUID(String objectGUID){
			return guidToUsersMap.get(objectGUID);
		}


		public int getNumOfInserts() {
			return numOfInserts;
		}


		public void incrementNumOfInserts() {
			this.numOfInserts++;
		}


		public int getNumOfUsernameUpdates() {
			return numOfUsernameUpdates;
		}


		public void incrementNumOfUsernameUpdates() {
			this.numOfUsernameUpdates++;
		}


		public int getNumOfSearchFieldUpdates() {
			return numOfSearchFieldUpdates;
		}


		public void incrementNumOfSearchFieldUpdates() {
			this.numOfSearchFieldUpdates++;
		}


		public int getNumOfAdInfoUpdates() {
			return numOfAdInfoUpdates;
		}


		public void incrementNumOfAdInfoUpdates() {
			this.numOfAdInfoUpdates++;
		}
	}


	@Override
	public void fillUpdateUserScore(Update update, User user, Classifier classifier) {
		update.set(User.getClassifierScoreField(classifier.getId()), user.getScore(classifier.getId()));
	}


	@Override
	public void fillUpdateLogUsername(Update update, String username, String logname) {
		update.set(User.getLogUserNameField(logname), username);
	}


	@Override
	public void fillUpdateAppUsername(Update update, User user, Classifier classifier) {
		update.set(User.getAppField(classifier.getUserApplication().getId()), user.getApplicationUserDetails().get(classifier.getUserApplication().getId()));
	}

}
