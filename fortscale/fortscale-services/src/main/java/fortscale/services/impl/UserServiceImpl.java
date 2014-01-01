package fortscale.services.impl;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.mortbay.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import fortscale.domain.ad.AdGroup;
import fortscale.domain.ad.AdUser;
import fortscale.domain.ad.AdUserGroup;
import fortscale.domain.ad.UserMachine;
import fortscale.domain.ad.dao.AdGroupRepository;
import fortscale.domain.ad.dao.AdUserRepository;
import fortscale.domain.ad.dao.UserMachineDAO;
import fortscale.domain.analyst.ScoreConfiguration;
import fortscale.domain.analyst.ScoreWeight;
import fortscale.domain.core.AdUserDirectReport;
import fortscale.domain.core.ApplicationUserDetails;
import fortscale.domain.core.ClassifierScore;
import fortscale.domain.core.EmailAddress;
import fortscale.domain.core.ScoreInfo;
import fortscale.domain.core.User;
import fortscale.domain.core.UserAdInfo;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.fe.AdUserFeaturesExtraction;
import fortscale.domain.fe.AuthScore;
import fortscale.domain.fe.IFeature;
import fortscale.domain.fe.VpnScore;
import fortscale.domain.fe.dao.AdUsersFeaturesExtractionRepository;
import fortscale.domain.fe.dao.AuthDAO;
import fortscale.domain.fe.dao.VpnDAO;
import fortscale.services.IUserScore;
import fortscale.services.IUserScoreHistoryElement;
import fortscale.services.LogEventsEnum;
import fortscale.services.UserApplication;
import fortscale.services.UserService;
import fortscale.services.analyst.ConfigurationService;
import fortscale.services.exceptions.UnknownResourceException;
import fortscale.services.fe.Classifier;
import fortscale.services.fe.ClassifierService;
import fortscale.utils.actdir.ADUserParser;
import fortscale.utils.logging.Logger;

@Service("userService")
public class UserServiceImpl implements UserService{
	private static Logger logger = Logger.getLogger(UserServiceImpl.class);
	
	private static final String SEARCH_FIELD_PREFIX = "##";
	private static final String REGEX_SEPERATOR = "####";
	private static final int MAX_NUM_OF_HISTORY_DAYS = 21;
	public static int MAX_NUM_OF_PREV_SCORES = 14;
	
	@Autowired
	private MongoOperations mongoTemplate;
	
	@Autowired
	private AdUserRepository adUserRepository;
	
	@Autowired
	private AdGroupRepository adGroupRepository;
		
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AdUsersFeaturesExtractionRepository adUsersFeaturesExtractionRepository;
	
	@Autowired
	private ClassifierService classifierService;
	
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
	private ThreadPoolTaskExecutor mongoDbReaderExecuter;
	
	@Autowired
	private ThreadPoolTaskExecutor mongoDbWriterExecuter;
	
	@Autowired 
	private ADUserParser adUserParser; 
	
	@Value("${vpn.to.ad.username.regex.format:^%s*(?i)}")
	private String vpnToAdUsernameRegexFormat;
	
	@Value("${auth.to.ad.username.regex.format:^%s*(?i)}")
	private String authToAdUsernameRegexFormat;
	
	@Value("${ssh.to.ad.username.regex.format:^%s*(?i)}")
	private String sshToAdUsernameRegexFormat;
	
	@Value("${vpn.status.success.value.regex:SUCCESS}")
	private String vpnStatusSuccessValueRegex;
	
	@Value("${ssh.status.success.value.regex:Accepted}")
	private String sshStatusSuccessValueRegex;
	
	@Value("${ad.info.update.read.page.size:1000}")
	private int readPageSize;
	
	
	private Map<String, String> groupDnToNameMap = new HashMap<>();
	
	
	@Override
	public String getUserThumbnail(User user) {
		String ret = null;
		Long timestampepoch = adUserRepository.getLatestTimeStampepoch();
		if(timestampepoch != null){
			AdUser adUser = adUserRepository.findByTimestampepochAndObjectGUID(timestampepoch, user.getAdInfo().getObjectGUID());
			ret = adUser.getThumbnailPhoto();
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
			long count = adUserRepository.countByTimestampepoch(timestampepoch);
			logger.info("getting all {} ad users", count);
			int numOfPages = (int)((count-1)/readPageSize) + 1;
			final AtomicInteger counter = new AtomicInteger(-1);
			CompletionService<UpdateUserAdInfoContext> pool = new ExecutorCompletionService<UpdateUserAdInfoContext>(mongoDbReaderExecuter);
			for(int i = 0; i < numOfPages; i++){
				Callable<UpdateUserAdInfoContext> task = new Callable<UpdateUserAdInfoContext>() {
					@Override
					public UpdateUserAdInfoContext call(){
						int page = counter.incrementAndGet();
						PageRequest pageRequest = new PageRequest(page, readPageSize);
						List<AdUser> adUsers = adUserRepository.findByTimestampepoch(timestampepoch,pageRequest);
						List<String> guids = new ArrayList<>(adUsers.size());
						for(AdUser adUser: adUsers){
							guids.add(adUser.getObjectGUID());
						}
						List<User> users = userRepository.findByGUIDs(guids);
						return new UpdateUserAdInfoContext(adUsers, users);
					}
				};
				pool.submit(task);
			}
			
			
			logger.info("filling all users with ad info data");
			CompletionService<String> writerPool = new ExecutorCompletionService<String>(mongoDbWriterExecuter);
			int numOfThreadExceptions = 0;
			int numOfTasks = 0;
			for(int i = 0; i < numOfPages && numOfThreadExceptions <= 5; i++){
				UpdateUserAdInfoContext updateUserAdInfoContext = null;
				try {
					TaskResult<UpdateUserAdInfoContext> taskResult = getTaskResultes(pool);
					numOfThreadExceptions += taskResult.getNumOfTaskExceptions();

					if(taskResult.getResult() == null){
						logger.error("updateUserAdInfoContext is null");
						continue;
					}
					updateUserAdInfoContext = taskResult.getResult();
				} catch (TimeoutException timeoutException) {
					logger.error("while getting ad user object from mongo got the time out exception.", timeoutException);
					break;
				}

				List<AdUser> pageAdUsers = updateUserAdInfoContext.getAdUsers();
				int pageNumOfTasks = 0;
				logger.info("Going over {} ad users", pageAdUsers.size());
				for(AdUser adUser: pageAdUsers){
					try {
						pageNumOfTasks += updateUserWithADInfo(adUser, updateUserAdInfoContext, writerPool);
						
					} catch (Exception e) {
						logger.error("got exception while trying to update user with active directory info!!! dn: {}", adUser.getDistinguishedName());
					}
				}
				logger.info("finished processing {} ad users info. triggered {} tasks with {} inserts, {} username updates, {} search field updates, {} ad info updates",
						pageAdUsers.size(), pageNumOfTasks, updateUserAdInfoContext.getNumOfInserts(), updateUserAdInfoContext.getNumOfUsernameUpdates(),
						updateUserAdInfoContext.getNumOfSearchFieldUpdates(), updateUserAdInfoContext.getNumOfAdInfoUpdates());
				numOfTasks += pageNumOfTasks;
			}
			
			logger.info("waiting for all the {} writing tasks.", numOfTasks);
			waitForAllWritingTasks(writerPool, numOfTasks);
			
			logger.info("finished updating the user collection.");
		}
//		saveUserIdUsernamesMapToImpala(new Date());
		logger.info("finished updating users with ad info.");
	}
	
	private <T> TaskResult<T> getTaskResultes(CompletionService<T> pool) throws TimeoutException{
		TaskResult<T> ret = new TaskResult<>();
		while(ret.getResult() == null && ret.getNumOfTaskExceptions() <= 5){
			try {
				ret.setResult(pool.take().get(30, TimeUnit.SECONDS));
			} catch (InterruptedException | ExecutionException e1) {
				logger.error("while getting ad user object from mongo got the following exception.", e1);
				ret.incrementNumOfTaskExceptions();
			}
		}
		
		return ret;
	}
	
	
	
	private void saveUserIdUsernamesMapToImpala(Date timestamp){
		List<User> users = userRepository.findAll();
		ImpalaUseridToAppUsernameWriter writer = impalaWriterFactory.createImpalaUseridToAppUsernameWriter();
		writer.write(users, timestamp);
		writer.close();
	}
	
	private int updateUserWithADInfo(AdUser adUser, UpdateUserAdInfoContext updateUserAdInfoContext, CompletionService<String> pool) {
		if(adUser.getObjectGUID() == null) {
			logger.error("got ad user with no ObjectGUID name field.");
			return 0;
		}
		if(adUser.getDistinguishedName() == null) {
			logger.error("got ad user with no distinguished name field.");
			return 0;
		}
		
		
		
		
		final UserAdInfo userAdInfo = new UserAdInfo();
		userAdInfo.setObjectGUID(adUser.getObjectGUID());
		userAdInfo.setDn(adUser.getDistinguishedName());
		userAdInfo.setFirstname(adUser.getFirstname());
		userAdInfo.setLastname(adUser.getLastname());
		if(adUser.getEmailAddress() != null && adUser.getEmailAddress().length() > 0){
			userAdInfo.setEmailAddress(new EmailAddress(adUser.getEmailAddress()));
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
		
		User user = updateUserAdInfoContext.findByObjectGUID(adUser.getObjectGUID());
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
		
		
		final User finalUser = user;
		int numOfTasks = 0;
		if(isSaveUser){
			if(!StringUtils.isEmpty(username)) {
				finalUser.setUsername(username);
				finalUser.addApplicationUserDetails(createApplicationUserDetails(UserApplication.active_directory, finalUser.getUsername()));
			}
			finalUser.setSearchField(searchField);
			
			Callable<String> task = new Callable<String>() {
				@Override
				public String call(){
					userRepository.save(finalUser);
					return "";
				}
			};
			pool.submit(task);
			numOfTasks = 1;
			updateUserAdInfoContext.incrementNumOfInserts();
			
		} else{
			final String finalUsername = username;
			updateUserAdInfoContext.incrementNumOfAdInfoUpdates();
			if(!StringUtils.isEmpty(finalUsername) && !finalUsername.equals(finalUser.getUsername())){
				updateUserAdInfoContext.incrementNumOfUsernameUpdates();
			}
			if(!searchField.equals(finalUser.getSearchField())){
				updateUserAdInfoContext.incrementNumOfSearchFieldUpdates();
			}
			Callable<String> task = new Callable<String>() {
				@Override
				public String call(){
					updateUser(finalUser, User.adInfoField, userAdInfo);
					if(!StringUtils.isEmpty(finalUsername) && !finalUsername.equals(finalUser.getUsername())){
						updateUser(finalUser, User.usernameField, finalUsername);
					}
					if(!searchField.equals(finalUser.getSearchField())){
						updateUser(finalUser, User.searchFieldName, searchField);
					}
					return "";
				}
			};
			pool.submit(task);
			numOfTasks = 1;
		}
		return numOfTasks;
	}
	
	private void updateUser(User user, String fieldName, Object val){
		mongoTemplate.updateFirst(query(where(User.ID_FIELD).is(user.getId())), update(fieldName, val), User.class);
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
	public List<IUserScore> getUserScores(String uid) {
		User user = userRepository.findOne(uid);
		if(user == null){
			throw new UnknownResourceException(String.format("user with id [%s] does not exist", uid));
		}
		
		return getUserScores(user);
	}
	
	@Override
	public Map<User,List<IUserScore>> getUsersScoresByIds(List<String> uids) {
		List<User> users = userRepository.findByIds(uids);
		
		return getUsersScores(users);
	}
	
	@Override
	public Map<User, List<IUserScore>> getFollowedUsersScores(){
		List<User> users = userRepository.findByFollowed(true);
		
		return getUsersScores(users);
	}
	
	private Map<User, List<IUserScore>> getUsersScores(List<User> users){
		Map<User,List<IUserScore>> ret = new HashMap<>();
		for(User user: users){
			ret.put(user, getUserScores(user));
		}
		
		return ret;
	}
	
	private List<IUserScore> getUserScores(User user) {
		List<IUserScore> ret = new ArrayList<IUserScore>();
		for(ClassifierScore classifierScore: user.getScores().values()){
			if(isOnSameDay(new Date(), classifierScore.getTimestamp(), MAX_NUM_OF_HISTORY_DAYS)) {
				Classifier classifier = classifierService.getClassifier(classifierScore.getClassifierId());
				if(classifier == null){
					continue;
				}
				UserScore score = new UserScore(user.getId(), classifierScore.getClassifierId(), classifier.getDisplayName(),
						(int)Math.round(classifierScore.getScore()), (int)Math.round(classifierScore.getAvgScore()));
				ret.add(score);
			}
		}
		
//		Pageable pageable = new PageRequest(0, 1, Direction.DESC, AdUserFeaturesExtraction.timestampField);
//		List<AdUserFeaturesExtraction> ufeList = adUsersFeaturesExtractionRepository.findByUserId(user.getAdDn(), pageable);
//		if(ufeList == null || ufeList.size() == 0){
//			return Collections.emptyList();
//		}
//		AdUserFeaturesExtraction ufe = ufeList.get(0);
//		Double avgScore = adUsersFeaturesExtractionRepository.calculateAvgScore(Classifier.getAdClassifierUniqueName(), ufe.getTimestamp());
//		List<IUserScore> ret = new ArrayList<IUserScore>();
//		UserScore score = new UserScore("overall", "User Profile", ufe.getScore(), avgScore);
//		ret.add(score);
		return ret;
	}
	
	@Override
	public List<IUserScore> getUserScoresByDay(String uid, Long dayTimestamp){
		User user = userRepository.findOne(uid);
		if(user == null){
			throw new UnknownResourceException(String.format("user with id [%s] does not exist", uid));
		}
		
		DateTime dateTimeEnd = new DateTime(dayTimestamp);
		DateTime dateTimeStart = dateTimeEnd.withTimeAtStartOfDay();
		dateTimeEnd = dateTimeStart.plusHours(24);
		Map<String,IUserScore> ret = new HashMap<String, IUserScore>();
		
		for(ScoreWeight scoreWeight: configurationService.getScoreConfiguration().getConfMap().values()){
			String classifierId = scoreWeight.getId();
			ClassifierScore classifierScore = user.getScore(classifierId);
			if(classifierScore != null){
				for(ScoreInfo prevScoreInfo: classifierScore.getPrevScores()){
					if(dateTimeStart.isAfter(prevScoreInfo.getTimestampEpoc())){
						break;
					} else if(dateTimeEnd.isBefore(prevScoreInfo.getTimestampEpoc())){
						continue;
					}
					Classifier classifier = classifierService.getClassifier(classifierId);
					UserScore score = new UserScore(user.getId(), classifierId, classifier.getDisplayName(),
							(int)Math.round(prevScoreInfo.getScore()), (int)Math.round(prevScoreInfo.getAvgScore()));
					ret.put(classifierId, score);
				}
			}
		}
		
		return new ArrayList<IUserScore>(ret.values());
	}
	
	public List<IUserScoreHistoryElement> getUserScoresHistory(String uid, String classifierId, int offset, int limit){
		Classifier.validateClassifierId(classifierId);
		User user = userRepository.findOne(uid);
		if(user == null){
			throw new UnknownResourceException(String.format("user with id [%s] does not exist", uid));
		}
		
		List<IUserScoreHistoryElement> ret = new ArrayList<IUserScoreHistoryElement>();
		ClassifierScore classifierScore = user.getScore(classifierId);
		if(classifierScore != null){
			
			if(!classifierScore.getPrevScores().isEmpty()){
				ScoreInfo scoreInfo = classifierScore.getPrevScores().get(0);
				int i = 0;
				if(isOnSameDay(classifierScore.getTimestamp(), scoreInfo.getTimestamp())){
					if(classifierScore.getScore() >= scoreInfo.getScore()){
						UserScoreHistoryElement userScoreHistoryElement = new UserScoreHistoryElement(classifierScore.getTimestamp(), classifierScore.getScore(), classifierScore.getAvgScore());
						ret.add(userScoreHistoryElement);
						i++;
					}
				} else{
					UserScoreHistoryElement userScoreHistoryElement = new UserScoreHistoryElement(classifierScore.getTimestamp(), classifierScore.getScore(), classifierScore.getAvgScore());
					ret.add(userScoreHistoryElement);
				}
				for(; i < classifierScore.getPrevScores().size(); i++){
					scoreInfo = classifierScore.getPrevScores().get(i);
					UserScoreHistoryElement userScoreHistoryElement = new UserScoreHistoryElement(scoreInfo.getTimestamp(), scoreInfo.getScore(), scoreInfo.getAvgScore());
					ret.add(userScoreHistoryElement);
				}
			} else{
				UserScoreHistoryElement userScoreHistoryElement = new UserScoreHistoryElement(classifierScore.getTimestamp(), classifierScore.getScore(), classifierScore.getAvgScore());
				ret.add(userScoreHistoryElement);
			}
		}
		
//		Pageable pageable = new PageRequest(0, 14, Direction.DESC, AdUserFeaturesExtraction.timestampField);
//		List<AdUserFeaturesExtraction> ufeList = adUsersFeaturesExtractionRepository.findByUserIdAndClassifierId(uid, classifierId, pageable);
//		if(ufeList == null || ufeList.size() == 0){
//			return Collections.emptyList();
//		}
//		
//		for(AdUserFeaturesExtraction ufe: ufeList){
//			Double avgScore = adUsersFeaturesExtractionRepository.calculateUsersDailyMaxScores(classifierId, uid);
//			UserScoreHistoryElement userScoreHistoryElement = new UserScoreHistoryElement(ufe.getTimestamp(), ufe.getScore(), avgScore);
//			ret.add(userScoreHistoryElement);
//		}
		if(offset >ret.size()) {
			return Collections.emptyList();
		}
		int toIndex = offset + limit;
		if(toIndex > ret.size()) {
			toIndex = ret.size();
		}
		
		ret = ret.subList(offset, toIndex);
		return ret;
	}

	@Override
	public List<IFeature> getUserAttributesScores(String uid, String classifierId, Long timestamp, String orderBy, Direction direction) {
		Classifier.validateClassifierId(classifierId);
//		Long timestampepoch = timestamp/1000;
		AdUserFeaturesExtraction ufe = adUsersFeaturesExtractionRepository.findByClassifierIdAndUserIdAndTimestamp(classifierId, uid, new Date(timestamp));
		if(ufe == null || ufe.getAttributes() == null){
			return Collections.emptyList();
		}
		
		Collections.sort(ufe.getAttributes(), getUserFeatureComparator(orderBy, direction));
		List<IFeature> ret = ufe.getAttributes();
		
		return ret;
	}
	
	@Override
	public Map<User,List<IFeature>> getFollowedUserAttributesScores(String classifierId, Long timestamp, String orderBy, Direction direction){
		List<User> users = userRepository.findByFollowed(true);
		Map<String, User> userMap = new HashMap<>();
		for(User user: users){
			userMap.put(user.getId(), user);
		}
		List<AdUserFeaturesExtraction> adUserFeaturesExtractions = adUsersFeaturesExtractionRepository.findByClassifierIdAndTimestampAndUserIds(classifierId, new Date(timestamp), userMap.keySet());
		
		Map<User,List<IFeature>> ret = new HashMap<>();
		for(AdUserFeaturesExtraction ufe: adUserFeaturesExtractions){
			Collections.sort(ufe.getAttributes(), getUserFeatureComparator(orderBy, direction));
			ret.put(userMap.get(ufe.getUserId()), ufe.getAttributes());
		}
		
		return ret;
	}
	
	private Comparator<IFeature> getUserFeatureComparator(String orderBy, Direction direction){
		if(direction == null){
			direction = Direction.DESC;
		}
		Comparator<IFeature> ret = null;
		if(orderBy == null){
			orderBy = "featureScore";
		}
		switch(orderBy){
		case "featureScore":
			ret = new IFeature.OrderByFeatureScore(direction);
			break;
		case "featureUniqueName":
			ret = new IFeature.OrderByFeatureUniqueName(direction);
			break;
		case "explanation.featureCount":
			ret = new IFeature.OrderByFeatureExplanationCount(direction);
			break;
		case "explanation.featureDistribution":
			ret = new IFeature.OrderByFeatureExplanationDistribution(direction);
			break;
		case "explanation.featureDescription":
			ret = new IFeature.OrderByFeatureDescription(direction);
			break;
		default:
			ret = new IFeature.OrderByFeatureScore(direction);
		}
		
		return ret;
	}
	
	private Sort processOrderBy(String orderBy, Direction direction){
		if(direction == null){
			direction = Direction.DESC;
		}

		if(orderBy == null){
			orderBy = "featureScore";
		}
		
		switch(orderBy){
		case "featureScore":
			orderBy = AdUserFeaturesExtraction.getFeatureScoreField();
			break;
		case "featureUniqueName":
			orderBy = AdUserFeaturesExtraction.getFeatureUniqueNameField();
			break;
		case "explanation.featureCount":
			orderBy = AdUserFeaturesExtraction.getExplanationFeatureCountField();
			break;
		case "explanation.featureDistribution":
			orderBy = AdUserFeaturesExtraction.getExplanationFeatureDistributionField();
			break;
		default:
			orderBy = AdUserFeaturesExtraction.getFeatureScoreField();
		}
		Sort sort = new Sort(direction, orderBy);
		return sort;
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
	
	@Autowired
	private ConfigurationService configurationService; 
	
	private void updateUserTotalScore(List<User> users, boolean isToSave, Date lastRun){
		for(User user: users){
			updateUserTotalScore(user, lastRun);
		}
		
		if(isToSave){
			userRepository.save(users);
			saveUserTotalScoreToImpala(lastRun, configurationService.getScoreConfiguration());
		}
	}
	
	private void updateUserTotalScore(User user, Date lastRun){
		ScoreInfo totalScore = calculateTotalScore(configurationService.getScoreConfiguration().getConfMap().values(), user.getScores(), lastRun);
		
		updateUserScore(user, lastRun, Classifier.total.getId(), totalScore.getScore(), totalScore.getAvgScore(), false, false);
	}
	
	private ScoreInfo calculateTotalScore(Collection<ScoreWeight> scoreWeights, Map<String, ClassifierScore> classifierScoreMap, Date lastRun){
		double totalWeights = 0.00001;
		double score = 0;
		double avgScore = 0;
		
		DateTime dateTime = new DateTime(lastRun.getTime());
		dateTime = dateTime.withTimeAtStartOfDay();
		for(ScoreWeight scoreWeight: scoreWeights){
			ClassifierScore classifierScore = classifierScoreMap.get(scoreWeight.getId());
			if(classifierScore != null){
				if(dateTime.isAfter(classifierScore.getTimestampEpoc())){
					continue;
				}
				totalWeights += scoreWeight.getWeight();
				
				score += classifierScore.getScore() * scoreWeight.getWeight();
				avgScore += classifierScore.getAvgScore() * scoreWeight.getWeight();					
			}
		}
		
		ScoreInfo ret = new ScoreInfo();
		ret.setScore(score/totalWeights);
		ret.setAvgScore(avgScore/totalWeights);
		return ret;
	}
	
	@Override
	public void recalculateTotalScore(){
		List<User> users = userRepository.findAll();
		for(User user: users){
			recalculateTotalScore(user);
			userRepository.save(user);
		}
	}
	
	private void recalculateTotalScore(User user){
		List<ClassifierScore> classifierScores = new ArrayList<>();
		for(ClassifierScore classifierScore: user.getScores().values()){
			if(classifierScore.getClassifierId().equals(Classifier.total.getId())){
				continue;
			}
			for(ScoreInfo scoreInfo: classifierScore.getPrevScores()){
				classifierScores.add(new ClassifierScore(classifierScore.getClassifierId(), scoreInfo));
			}
		}
		
		user.removeAllScores();
		
		Collections.sort(classifierScores, new OrderByClassifierScoreTimestempAsc());
		
		for(ClassifierScore classifierScore: classifierScores){
			boolean isSaveMaxScore = false;
//			if(classifierScore.getClassifierId().equals(Classifier.groups.getId())){
//				isSaveMaxScore = true;
//			}
			updateUserScore(user, classifierScore.getTimestamp(), classifierScore.getClassifierId(), classifierScore.getScore(), classifierScore.getAvgScore(), false,isSaveMaxScore);
			updateUserTotalScore(user, classifierScore.getTimestamp());
		}
	}
	
	private void saveUserTotalScoreToImpala(Date timestamp, ScoreConfiguration scoreConfiguration){
		List<User> users = userRepository.findAll();
		saveUserTotalScoreToImpala(users, timestamp, scoreConfiguration);
	}
	
	private void saveUserTotalScoreToImpala(List<User> users, Date timestamp, ScoreConfiguration scoreConfiguration){
		ImpalaTotalScoreWriter writer = impalaWriterFactory.createImpalaTotalScoreWriter();

		saveUserTotalScoreToImpala(writer, users, timestamp, scoreConfiguration);
		
		writer.close();
	}
	
	private void saveUserTotalScoreToImpala(ImpalaTotalScoreWriter writer, List<User> users, Date timestamp, ScoreConfiguration scoreConfiguration){
		logger.info("writing {} users total score to local file", users.size());
		for(User user: users){
			if(user.getScore(Classifier.total.getId()) != null){
				writer.writeScores(user, timestamp, scoreConfiguration);
			}
		}
	}

	@Override
	public void updateUserWithAuthScore(Classifier classifier) {
		AuthDAO authDAO = getAuthDAO(classifier.getLogEventsEnum());
		Date lastRun = authDAO.getLastRunDate();
		updateUserWithAuthScore(authDAO, classifier, lastRun);
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
	
	private void updateUserWithAuthScore(AuthDAO authDAO, Classifier classifier, Date lastRun) {
		double avg = authDAO.calculateAvgScoreOfGlobalScore(lastRun);
		List<User> users = new ArrayList<>();
		for(AuthScore authScore: authDAO.findGlobalScoreByTimestamp(lastRun)){
			String username = authScore.getUserName();
			User user = userRepository.findByLogUsername(authDAO.getTableName(), username);
			if(user == null){
				user = findByAuthUsername(classifier.getLogEventsEnum(), username);
				if(user == null){
					if(classifier.getLogEventsEnum().equals(LogEventsEnum.ssh)){
						logger.info("no user was found with SSH username ({})", username);
						if(authDAO.countNumOfEventsByUserAndStatusRegex(lastRun, username, sshStatusSuccessValueRegex) > 0){
							logger.info("creating a new user from a successed ssh event. ssh username ({})", username);
							user = createUser(authScore);
						} else{
							continue;
						}
					} else{
						logger.error("no user was found with the username {}", username);
						continue;
					}
				}
				updateLogUsername(user, authDAO.getTableName(), username, false);
			}
			user = updateUserScore(user, lastRun, classifier.getId(), authScore.getGlobalScore(), avg, false, false);
			if(user != null){
				users.add(user);
			}
		}
		updateUserTotalScore(users, true, lastRun);		
	}
	
	private User createUser(AuthScore authScore){
		String username = authScore.getUserName();
		User user = new User();
		user.setUsername(username);
		user.setSearchField(createSearchField(null, username));
		
		return user;
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
	public void updateUserWithVpnScore() {
		Date lastRun = vpnDAO.getLastRunDate();
		updateUserWithVpnScore(lastRun);
	}
	
	private void updateUserWithVpnScore(Date lastRun) {
		double avg = vpnDAO.calculateAvgScoreOfGlobalScore(lastRun);
		List<User> users = new ArrayList<>();
		for(VpnScore vpnScore: vpnDAO.findGlobalScoreByTimestamp(lastRun)){
			String username = vpnScore.getUserName();
			User user = userRepository.findByApplicationUserName(UserApplication.vpn.getId(), username);
			if(user == null){
				user = findByVpnUsername(username);
				if(user == null){
					logger.info("no user was found with vpn username ({})", username);
					if(vpnDAO.countNumOfEventsByUserAndStatusRegex(lastRun, username, vpnStatusSuccessValueRegex) > 0){
						logger.info("creating a new user from a successed vpn event. vpn username ({})", username);
						user = createUser(vpnScore);
					} else{
						continue;
					}
				}
				updateApplicationUserDetails(user, new ApplicationUserDetails(UserApplication.vpn.getId(), username), false);
				updateVpnLogUsername(user, username, false);
			}
			user = updateUserScore(user, lastRun, Classifier.vpn.getId(), vpnScore.getGlobalScore(), avg, false, false);
			if(user != null){
				users.add(user);
			}
		}
		updateUserTotalScore(users, true, lastRun);
	}
	
	private String getAuthLogUsername(LogEventsEnum eventId, User user){
		AuthDAO authDAO = getAuthDAO(eventId);
		return user.getLogUsernameMap().get(authDAO.getTableName());
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
	
	private void updateVpnLogUsername(User user, String username, boolean isSave){
		updateLogUsername(user, VpnScore.TABLE_NAME, username, isSave);
	}
	
	private String getVpnLogUsername(User user){
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
	
	private User createUser(VpnScore vpnScore){
		String username = vpnScore.getUserName();
		User user = new User();
		user.setUsername(username);
		user.setSearchField(createSearchField(null, username));
		
		return user;
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
	public void updateUserWithGroupMembershipScore(){
		Date lastRun = adUsersFeaturesExtractionRepository.getLatestTimeStamp();
		if(lastRun == null){
			logger.warn("there is no timestamp. probably the table is empty.");
			return;
		}
		updateUserWithGroupMembershipScore(lastRun);
	}
	
	private void updateUserWithGroupMembershipScore(final Date lastRun){
		final String classifierId = Classifier.groups.getId();
		long count = adUsersFeaturesExtractionRepository.countByClassifierIdAndTimestamp(classifierId, lastRun); 
		if(count == 0){
			logger.warn("the group membership for timestamp ({}) is empty.", lastRun);
			return;
		}
		logger.info("getting all the {} AdUserFeaturesExtraction", count);
		
		final double avgScore = adUsersFeaturesExtractionRepository.calculateAvgScore(classifierId, lastRun);
		
		
		int numOfPages = (int)((count-1)/readPageSize) + 1;
		final AtomicInteger counter = new AtomicInteger(-1);
		CompletionService<UpdateUserGroupMembershipScoreContext> pool = new ExecutorCompletionService<UpdateUserGroupMembershipScoreContext>(mongoDbReaderExecuter);
		for(int i = 0; i < numOfPages; i++){
			Callable<UpdateUserGroupMembershipScoreContext> task = new Callable<UpdateUserGroupMembershipScoreContext>() {
				@Override
				public UpdateUserGroupMembershipScoreContext call(){
					int page = counter.incrementAndGet();
					PageRequest pageRequest = new PageRequest(page, readPageSize);
					List<AdUserFeaturesExtraction> adUserFeaturesExtractions = adUsersFeaturesExtractionRepository.findByClassifierIdAndTimestamp(classifierId, lastRun, pageRequest);
					List<String> ids = new ArrayList<>(adUserFeaturesExtractions.size());
					for(AdUserFeaturesExtraction adUserFeaturesExtraction: adUserFeaturesExtractions){
						ids.add(adUserFeaturesExtraction.getUserId());
					}
					List<User> users = userRepository.findByIds(ids);
					return new UpdateUserGroupMembershipScoreContext(adUserFeaturesExtractions, users);
				}
			};
			pool.submit(task);
		}
		
		
		logger.info("filling all users with group membership score");
		CompletionService<String> writerPool = new ExecutorCompletionService<String>(mongoDbWriterExecuter);
		int numOfThreadExceptions = 0;
		int numOfTasks = 0;
		ImpalaGroupsScoreWriter impalaGroupsScoreWriter = null;
		ImpalaTotalScoreWriter impalaTotalScoreWriter = null;
		try{
			impalaGroupsScoreWriter = impalaWriterFactory.createImpalaGroupsScoreWriter();
			impalaTotalScoreWriter = impalaWriterFactory.createImpalaTotalScoreWriter();
			for(int i = 0; i < numOfPages && numOfThreadExceptions <= 5; i++){
				UpdateUserGroupMembershipScoreContext updateUserGroupMembershipScoreContext = null;
				try {
					logger.info("waiting for the next page...");
					TaskResult<UpdateUserGroupMembershipScoreContext> taskResult = getTaskResultes(pool);
					numOfThreadExceptions += taskResult.getNumOfTaskExceptions();

					if(taskResult.getResult() == null){
						logger.error("updateUserGroupMembershipScoreContext is null");
						continue;
					}
					updateUserGroupMembershipScoreContext = taskResult.getResult();
				} catch (TimeoutException timeoutException) {
					logger.error("while getting AdUserFeaturesExtraction from mongo got the time out exception.", timeoutException);
					break;
				}

				logger.info("Going over {} AdUserFeaturesExtraction", updateUserGroupMembershipScoreContext.getAdUserFeaturesExtractionsSize());
				List<User> users = new ArrayList<>();
				for(AdUserFeaturesExtraction extraction: updateUserGroupMembershipScoreContext.getAdUserFeaturesExtractions()){
					User user = updateUserGroupMembershipScoreContext.findByUserId(extraction.getUserId().toString());
					if(user == null){
						logger.error("user with id ({}) was not found in user table", extraction.getUserId());
						continue;
					}
					//updating the user with the new score.
					user = updateUserScore(user, new Date(extraction.getTimestamp().getTime()), classifierId, extraction.getScore(), avgScore, false, false);
					if(user != null){
						users.add(user);
					}
					
				}
				
				updateUserTotalScore(users, false, lastRun);
				
				for(final User user: users){
					Callable<String> task = new Callable<String>() {
						@Override
						public String call(){
							updateUser(user, User.getClassifierScoreField(classifierId), user.getScore(classifierId));
							updateUser(user, User.getClassifierScoreField(Classifier.total.getId()), user.getScore(Classifier.total.getId()));
							return "";
						}
					};
					writerPool.submit(task);
				}
				
				numOfTasks += users.size();
				logger.info("finished processing {} AdUserFeaturesExtraction. triggered {} tasks.",	updateUserGroupMembershipScoreContext.getAdUserFeaturesExtractionsSize(), users.size());
				
				logger.info("writing group score to local file");
				for(User user: users){
					impalaGroupsScoreWriter.writeScore(user, updateUserGroupMembershipScoreContext.findFeautreByUserId(user.getId()), avgScore);
				}
	
				saveUserTotalScoreToImpala(impalaTotalScoreWriter, users, lastRun, configurationService.getScoreConfiguration());
				
				
			}
		} finally{
			if(impalaGroupsScoreWriter != null){
				impalaGroupsScoreWriter.close();
			}
			if(impalaTotalScoreWriter != null){
				impalaTotalScoreWriter.close();
			}
		}
		
		logger.info("waiting for all the {} writing tasks.", numOfTasks);
		waitForAllWritingTasks(writerPool, numOfTasks);


		logger.info("finished updating the user collection. with group membership score and total score.");
	}
	
	private void waitForAllWritingTasks(CompletionService<String> writerPool, int numOfTasks){
		int numOfThreadExceptions = 0;
		for(int i = 0; i < numOfTasks && numOfThreadExceptions <= 5; i++){
			try {
				TaskResult<String> taskResult = getTaskResultes(writerPool);
				numOfThreadExceptions += taskResult.getNumOfTaskExceptions();
			} catch (TimeoutException timeoutException) {
				logger.error("while getting ad user object from mongo got the time out exception.", timeoutException);
				break;
			}
		}
	}
	
	@Override
	public User updateUserScore(User user, Date timestamp, String classifierId, double value, double avgScore, boolean isToSave, boolean isSaveMaxScore){
		ClassifierScore cScore = user.getScore(classifierId);
		
		
		boolean isReplaceCurrentScore = true;
		double trend = 0.0; 
		double diffScore = 0.0;
		if(cScore == null){
			cScore = new ClassifierScore();
			cScore.setClassifierId(classifierId);
			ScoreInfo scoreInfo = new ScoreInfo();
			scoreInfo.setScore(value);
			scoreInfo.setAvgScore(avgScore);
			scoreInfo.setTimestamp(timestamp);
			scoreInfo.setTimestampEpoc(timestamp.getTime());
			List<ScoreInfo> prevScores = new ArrayList<ScoreInfo>();
			prevScores.add(scoreInfo);
			cScore.setPrevScores(prevScores);
		}else{
			boolean isOnSameDay = isOnSameDay(timestamp, cScore.getTimestamp());
			if(!isOnSameDay && timestamp.before(cScore.getTimestamp())){
				logger.warn("Got a score that belong to the past. classifierId ({}), current timestamp ({}), new score timestamp ({})", classifierId, cScore.getTimestamp(), timestamp);
				return null;
			}
			if(cScore.getPrevScores().size() > 1){
				double prevScore = cScore.getPrevScores().get(1).getScore() + 0.00001;
				double curScore = value + 0.00001;
				diffScore = curScore - prevScore;
				trend = diffScore / prevScore;
			}
			
			ScoreInfo scoreInfo = new ScoreInfo();
			scoreInfo.setScore(value);
			scoreInfo.setAvgScore(avgScore);
			scoreInfo.setTimestamp(timestamp);
			scoreInfo.setTimestampEpoc(timestamp.getTime());
			scoreInfo.setTrend(trend);
			scoreInfo.setTrendScore(diffScore);
			if (isOnSameDay) {
				if(isSaveMaxScore && value < cScore.getScore()){
					isReplaceCurrentScore = false;
				}else{
					cScore.getPrevScores().set(0, scoreInfo);
				}
			} else{
				List<ScoreInfo> prevScores = cScore.getPrevScores();
				prevScores.add(0, scoreInfo);
				if(prevScores.size() > MAX_NUM_OF_PREV_SCORES){
					cScore.setPrevScores(prevScores.subList(0, MAX_NUM_OF_PREV_SCORES));
				}
			}
		}
		if(isReplaceCurrentScore) {
			
			cScore.setScore(value);
			cScore.setAvgScore(avgScore);
			cScore.setTimestamp(timestamp);
			cScore.setTimestampEpoc(timestamp.getTime());
			cScore.setTrend(trend);
			cScore.setTrendScore(Math.abs(diffScore));
		}
		user.putClassifierScore(cScore);
		if(isToSave){
			userRepository.save(user);
		}
		return user;
	}
	
	private boolean isOnSameDay(Date date1, Date date2){
		return isOnSameDay(date1, date2, 0);
	}
	
	private boolean isOnSameDay(Date date1, Date date2, int dayThreshold){
		DateTime dateTime1 = new DateTime(date1.getTime());
		dateTime1 = dateTime1.withTimeAtStartOfDay();
		DateTime dateTime2 = new DateTime(date2.getTime());
		dateTime2 = dateTime2.withTimeAtStartOfDay();
		if(dateTime1.equals(dateTime2)){
			return true;
		}
		
		
		if(dateTime1.isAfter(dateTime2)){
			dateTime1 = dateTime1.minusDays(dayThreshold);
			return dateTime1.isBefore(dateTime2);
		} else{
			dateTime2 = dateTime2.minusDays(dayThreshold);
			return dateTime2.isBefore(dateTime1);
		}
	}

	public void updateApplicationUserDetails(User user, ApplicationUserDetails applicationUserDetails, boolean isSave) {
		user.addApplicationUserDetails(applicationUserDetails);
		if(isSave){
			userRepository.save(user);
		}
	}
	
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
	public void recalculateUsersScores() {
		Calendar oldestTime = Calendar.getInstance();
		oldestTime.add(Calendar.DAY_OF_MONTH, -7);
		List<ClassifierRuntime> classifierRuntimes = new ArrayList<>();
		List<User> users = userRepository.findAll();
		for(User user: users){
			user.removeAllScores();
		}
		userRepository.save(users);
		
		List<Date> distinctDates = adUsersFeaturesExtractionRepository.getDistinctRuntime(Classifier.groups.getId());
		for(Date date: distinctDates){
			if(date.getTime() < oldestTime.getTimeInMillis()){
				continue;
			}
			classifierRuntimes.add(new ClassifierRuntime(Classifier.groups, date.getTime()));
		}
		
		List<Long> distinctRuntimes = null;
		
		Classifier classifiers[] = {Classifier.auth, Classifier.ssh};
		for(Classifier classifier: classifiers){
			AuthDAO authDAO = getAuthDAO(classifier.getLogEventsEnum());
			distinctRuntimes = authDAO.getDistinctRuntime();
			for(Long runtime: distinctRuntimes){
				if(runtime == null){
					logger.warn("got runtime null in the vpndatares table.");
					continue;
				}
				runtime = runtime*1000;
				if(runtime < oldestTime.getTimeInMillis()){
					continue;
				}
				classifierRuntimes.add(new ClassifierRuntime(classifier, runtime));
			}
		}
		
		distinctRuntimes = vpnDAO.getDistinctRuntime();
		for(Long runtime: distinctRuntimes){
			if(runtime == null){
				logger.warn("got runtime null in the vpndatares table.");
				continue;
			}
			runtime = runtime*1000;
			if(runtime < oldestTime.getTimeInMillis()){
				continue;
			}
			classifierRuntimes.add(new ClassifierRuntime(Classifier.vpn, runtime));
		}
		
		Collections.sort(classifierRuntimes);
		
		for(ClassifierRuntime classifierRuntime: classifierRuntimes){
			try{
				switch (classifierRuntime.getClassifier()) {
				case auth:
					updateUserWithAuthScore(loginDAO, Classifier.auth, new Date(classifierRuntime.getRuntime()));
					break;
				case ssh:
					updateUserWithAuthScore(sshDAO, Classifier.ssh, new Date(classifierRuntime.getRuntime()));
					break;
				case vpn:
					updateUserWithVpnScore(new Date(classifierRuntime.getRuntime()));
					break;
				case groups:
					updateUserWithGroupMembershipScore(new Date(classifierRuntime.getRuntime()));
					break;
				default:
					break;
				}
			} catch(Exception e){
				logger.error("failed to update classifier {} on runtime{}", classifierRuntime.classifier, classifierRuntime.getRuntime());
				logger.error(e.getMessage(),e);
			}
		}
	}
	
	class ClassifierRuntime implements Comparable<ClassifierRuntime>{
		private Classifier classifier;
		private long runtime;
		
		public ClassifierRuntime(Classifier classifier, long runtime) {
			this.classifier = classifier;
			this.runtime = runtime;
		}

		@Override
		public int compareTo(ClassifierRuntime o) {
			long diff = (int)(this.runtime - o.runtime);
			int ret = (diff > 0 ? 1 : diff < 0 ? -1 : 0);
			return ret;
		}

		public Classifier getClassifier() {
			return classifier;
		}

		public void setClassifier(Classifier classifier) {
			this.classifier = classifier;
		}

		public long getRuntime() {
			return runtime;
		}

		public void setRuntime(long runtime) {
			this.runtime = runtime;
		}

		
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
		
		user.getAdInfo().setFirstname(adUser.getFirstname());
		user.getAdInfo().setLastname(adUser.getLastname());
		if(adUser.getEmailAddress() != null && adUser.getEmailAddress().length() > 0){
			user.getAdInfo().setEmailAddress(new EmailAddress(adUser.getEmailAddress()));
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
	
	class UpdateUserGroupMembershipScoreContext{
		private Map<String,AdUserFeaturesExtraction> adUserFeaturesExtractionsMap = new HashMap<>();
		private Map<String,User> usersMap = new HashMap<>();
		
		public UpdateUserGroupMembershipScoreContext(List<AdUserFeaturesExtraction> adUserFeaturesExtractions, List<User> users){
			for(AdUserFeaturesExtraction adUserFeaturesExtraction: adUserFeaturesExtractions){
				adUserFeaturesExtractionsMap.put(adUserFeaturesExtraction.getUserId(), adUserFeaturesExtraction);
			}

			for(User user: users){
				usersMap.put(user.getId(), user);
			}
		}
		
		

		
		public User findByUserId(String uid){
			return usersMap.get(uid);
		}
		
		public AdUserFeaturesExtraction findFeautreByUserId(String uid){
			return adUserFeaturesExtractionsMap.get(uid);
		}



		public Collection<AdUserFeaturesExtraction> getAdUserFeaturesExtractions() {
			return adUserFeaturesExtractionsMap.values();
		}
		
		public int getAdUserFeaturesExtractionsSize(){
			return adUserFeaturesExtractionsMap.size();
		}



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
	
	
	class TaskResult<T>{
		private T result;
		private int numOfTaskExceptions = 0;
		
		
		public void setResult(T result) {
			this.result = result;
		}

		public T getResult() {
			return result;
		}

		public int getNumOfTaskExceptions() {
			return numOfTaskExceptions;
		}
		public void incrementNumOfTaskExceptions() {
			this.numOfTaskExceptions++;
		}
		
		
	}

	
}
