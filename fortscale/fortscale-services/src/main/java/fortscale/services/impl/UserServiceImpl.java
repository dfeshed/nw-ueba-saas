package fortscale.services.impl;

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

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.mortbay.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort.Direction;
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
	private AuthDAO authDAO;
	
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
		String timestamp = adUserRepository.getLatestTimeStamp();
		if(timestamp != null) {
			updateUserWithADInfo(timestamp);
		} else {
			logger.warn("no timestamp. probably the ad_user table is empty");
		}
		
	}
	
	@Override
	public void updateUserWithADInfo(String timestamp) {
		updateUserWithADInfo(adUserRepository.findByTimestamp(timestamp));
	}
	
	private void updateUserWithADInfo(Iterable<AdUser> adUsers) {
		for(AdUser adUser: adUsers){
			try {
				updateUserWithADInfo(adUser);
			} catch (Exception e) {
				logger.error("got exception while trying to update user with active directory info!!! dn: {}", adUser.getDistinguishedName());
			}
			
		}
		saveUserIdUsernamesMapToImpala(new Date());
	}
	
	private void saveUserIdUsernamesMapToImpala(Date timestamp){
		List<User> users = userRepository.findAll();
		ImpalaUseridToAppUsernameWriter writer = impalaWriterFactory.createImpalaUseridToAppUsernameWriter();
		writer.write(users, timestamp);
		writer.close();
	}
	
	private void updateUserWithADInfo(AdUser adUser) {
		if(adUser.getDistinguishedName() == null) {
			logger.error("got ad user with no distinguished name field.");
			return;
		}
		User user = userRepository.findByAdDn(adUser.getDistinguishedName());
		if(user == null){
			user = new User(adUser.getDistinguishedName());
		}
		user.setFirstname(adUser.getFirstname());
		user.setLastname(adUser.getLastname());
		if(adUser.getEmailAddress() != null && adUser.getEmailAddress().length() > 0){
			user.setEmailAddress(new EmailAddress(adUser.getEmailAddress()));
		}
		user.setAdUserPrincipalName(adUser.getUserPrincipalName());
		user.setAdSAMAccountName(adUser.getsAMAccountName());
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
		
		user.setAdEmployeeID(adUser.getEmployeeID());
		user.setAdEmployeeNumber(adUser.getEmployeeNumber());
		user.setManagerDN(adUser.getManager());
		user.setMobile(adUser.getMobile());
		user.setTelephoneNumber(adUser.getTelephoneNumber());
		user.setOtherFacsimileTelephoneNumber(adUser.getOtherFacsimileTelephoneNumber());
		user.setOtherHomePhone(adUser.getOtherHomePhone());
		user.setOtherMobile(adUser.getOtherMobile());
		user.setOtherTelephone(adUser.getOtherTelephone());
		user.setHomePhone(adUser.getHomePhone());
		user.setSearchField(createSearchField(user));
		user.setDepartment(adUser.getDepartment());
		user.setPosition(adUser.getTitle());
		user.setThumbnailPhoto(adUser.getThumbnailPhoto());
		user.setAdDisplayName(adUser.getDisplayName());
		user.setAdLogonHours(adUser.getLogonHours());
		try {
			user.setAdWhenChanged(adUserParser.parseDate(adUser.getWhenChanged()));
		} catch (ParseException e) {
			logger.error("got and exception while trying to parse active directory when changed field ({})",adUser.getWhenChanged());
			logger.error("got and exception while trying to parse active directory when changed field",e);
		}
		
		try {
			user.setAdWhenCreated(adUserParser.parseDate(adUser.getWhenCreated()));
		} catch (ParseException e) {
			logger.error("got and exception while trying to parse active directory when created field ({})",adUser.getWhenChanged());
			logger.error("got and exception while trying to parse active directory when created field",e);
		}
		
		user.setAdDescription(adUser.getDescription());
		user.setAdStreetAddress(adUser.getStreetAddress());
		user.setAdCompany(adUser.getCompany());
		user.setAdC(adUser.getC());
		user.setAdDivision(adUser.getDivision());
		user.setAdL(adUser.getL());
		user.setAdO(adUser.getO());
		user.setAdRoomNumber(adUser.getRoomNumber());
		if(!StringUtils.isEmpty(adUser.getAccountExpires()) && !adUser.getAccountExpires().equals("0") && !adUser.getAccountExpires().startsWith("30828")){
			try {
				user.setAccountExpires(adUserParser.parseDate(adUser.getAccountExpires()));
			} catch (ParseException e) {
				logger.error("got and exception while trying to parse active directory account expires field ({})",adUser.getWhenChanged());
				logger.error("got and exception while trying to parse active directory account expires field",e);
			}
		}
		user.setAdUserAccountControl(adUser.getUserAccountControl());
		
		ADUserParser adUserParser = new ADUserParser();
		String[] groups = adUserParser.getUserGroups(adUser.getMemberOf());
		user.clearGroups();
		if(groups != null){
			for(String groupDN: groups){
				AdGroup adGroup = adGroupRepository.findByDistinguishedName(groupDN);
				String groupName = null;
				if(adGroup != null){
					groupName = adGroup.getName();
				}else{
					Log.warn("the user ({}) group ({}) was not found", user.getAdDn(), groupDN);
					groupName = adUserParser.parseFirstCNFromDN(groupDN);
					if(groupName == null){
						Log.warn("invalid group dn ({}) for user ({})", groupDN, user.getAdDn());
						continue;
					}
				}
				user.addGroup(new AdUserGroup(groupDN, groupName));
			}
		}
		
		String[] directReports = adUserParser.getDirectReports(adUser.getDirectReports());
		user.clearAdDirectReport();
		if(directReports != null){
			for(String directReportsDN: directReports){
				User userDirectReport = userRepository.findByAdDn(directReportsDN);
				if(userDirectReport != null){
					AdUserDirectReport adUserDirectReport = new AdUserDirectReport(directReportsDN, userDirectReport.getAdDisplayName());
					adUserDirectReport.setUserId(userDirectReport.getId());
					adUserDirectReport.setFirstname(userDirectReport.getFirstname());
					adUserDirectReport.setLastname(userDirectReport.getLastname());
					adUserDirectReport.setUsername(userDirectReport.getUsername());
					user.addAdDirectReport(adUserDirectReport);
				}else{
					logger.warn("the user ({}) direct report ({}) was not found", user.getAdDn(), directReportsDN);
				}
			}
		}
		
		userRepository.save(user);
	}
	
	private String createSearchField(User user){
		StringBuilder sb = new StringBuilder();
		if(user.getFirstname() != null && user.getFirstname().length() > 0){
			if(user.getLastname() != null && user.getLastname().length() > 0){
				sb.append(SEARCH_FIELD_PREFIX).append(user.getFirstname().toLowerCase()).append(" ").append(user.getLastname().toLowerCase());
				sb.append(SEARCH_FIELD_PREFIX).append(user.getLastname().toLowerCase()).append(" ").append(user.getFirstname().toLowerCase());
			} else{
				sb.append(SEARCH_FIELD_PREFIX).append(user.getFirstname().toLowerCase());
			}
		}else{
			if(user.getLastname() != null && user.getLastname().length() > 0){
				sb.append(SEARCH_FIELD_PREFIX).append(SEARCH_FIELD_PREFIX).append(user.getLastname().toLowerCase());
			}
		}
		
		if(!StringUtils.isEmpty(user.getUsername())){
			sb.append(SEARCH_FIELD_PREFIX).append(user.getUsername());
		}
		return sb.toString();
	}

	@Override
	public List<User> findBySearchFieldContaining(String prefix) {
		
		return userRepository.findBySearchFieldContaining(SEARCH_FIELD_PREFIX+prefix.toLowerCase());
	}

	@Override
	public List<IUserScore> getUserScores(String uid) {
		User user = userRepository.findOne(uid);
		if(user == null){
			throw new UnknownResourceException(String.format("user with id [%s] does not exist", uid));
		}
		
		List<IUserScore> ret = new ArrayList<IUserScore>();
		for(ClassifierScore classifierScore: user.getScores().values()){
			if(isOnSameDay(new Date(), classifierScore.getTimestamp(), MAX_NUM_OF_HISTORY_DAYS)) {
				Classifier classifier = classifierService.getClassifier(classifierScore.getClassifierId());
				if(classifier == null){
					continue;
				}
				UserScore score = new UserScore(classifierScore.getClassifierId(), classifier.getDisplayName(),
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
					UserScore score = new UserScore(classifierId, classifier.getDisplayName(),
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
		Date currentDate = new Date();
		if(classifierScore != null && isOnSameDay(currentDate, classifierScore.getTimestamp(), MAX_NUM_OF_HISTORY_DAYS)){
			
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
					if(!isOnSameDay(currentDate, scoreInfo.getTimestamp(), MAX_NUM_OF_HISTORY_DAYS)) {
						break;
					}
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
		Collections.reverse(ret);
		return ret;
	}

	@Override
	public List<IFeature> getUserAttributesScores(String uid, String classifierId, Long timestamp, String orderBy, Direction direction) {
		Classifier.validateClassifierId(classifierId);
//		Long timestampepoch = timestamp/1000;
		List<AdUserFeaturesExtraction> adUserFeaturesExtractions = adUsersFeaturesExtractionRepository.findByClassifierIdAndTimestamp(classifierId, new Date(timestamp));
//		AdUserFeaturesExtraction ufe = adUsersFeaturesExtractionRepository.getClassifierIdAndByUserIdAndTimestamp(classifierId,uid, new Date(timestamp));
		AdUserFeaturesExtraction ufe = null;
		for(AdUserFeaturesExtraction adUserFeaturesExtraction: adUserFeaturesExtractions){
			if(adUserFeaturesExtraction.getUserId().equals(uid)){
				ufe = adUserFeaturesExtraction;
				break;
			}
		}
		if(ufe == null || ufe.getAttributes() == null){
			return Collections.emptyList();
		}
		
		Collections.sort(ufe.getAttributes(), getUserFeatureComparator(orderBy, direction));
		return ufe.getAttributes();
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
			if(classifierScore.getClassifierId().equals(Classifier.groups.getId())){
				isSaveMaxScore = true;
			}
			updateUserScore(user, classifierScore.getTimestamp(), classifierScore.getClassifierId(), classifierScore.getScore(), classifierScore.getAvgScore(), false,isSaveMaxScore);
			updateUserTotalScore(user, classifierScore.getTimestamp());
		}
	}
	
	private void saveUserTotalScoreToImpala(Date timestamp, ScoreConfiguration scoreConfiguration){
		List<User> users = userRepository.findAll();
		ImpalaTotalScoreWriter writer = impalaWriterFactory.createImpalaTotalScoreWriter();
		for(User user: users){
			if(user.getScore(Classifier.total.getId()) != null){
				writer.writeScores(user, timestamp, scoreConfiguration);
			}
		}
		writer.close();
	}

	@Override
	public void updateUserWithAuthScore() {
		Date lastRun = authDAO.getLastRunDate();
		updateUserWithAuthScore(lastRun);
	}
	
	private void updateUserWithAuthScore(Date lastRun) {
		double avg = authDAO.calculateAvgScoreOfGlobalScore(lastRun);
		List<User> users = new ArrayList<>();
		for(AuthScore authScore: authDAO.findGlobalScoreByTimestamp(lastRun)){
			String username = authScore.getUserName();
			User user = userRepository.findByLogUsername(AuthScore.TABLE_NAME, username);
			if(user == null){
				user = findByAuthUsername(username);
				if(user == null){
					logger.error("no user was found with the username {}", username);
					continue;
				}
				updateLogUsername(user, AuthScore.TABLE_NAME, username, false);
			}
			user = updateUserScore(user, lastRun, Classifier.auth.getId(), authScore.getGlobalScore(), avg, false, false);
			if(user != null){
				users.add(user);
			}
		}
		updateUserTotalScore(users, true, lastRun);		
	}
	
	@Override
	public User findByAuthUsername(String username){
		return findByUsername(generateUsernameRegexesByAuthUsername(username), username);
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
	
	private List<String> generateUsernameRegexesByAuthUsername(String authUsername){
		List<String> regexes = new ArrayList<>();
		for(String regexFormat: authToAdUsernameRegexFormat.split(REGEX_SEPERATOR)){
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
					continue;
				}
				updateApplicationUserDetails(user, new ApplicationUserDetails(UserApplication.vpn.getId(), username), false);
				updateLogUsername(user, VpnScore.TABLE_NAME, username, false);
			}
			user = updateUserScore(user, lastRun, Classifier.vpn.getId(), vpnScore.getGlobalScore(), avg, false, false);
			if(user != null){
				users.add(user);
			}
		}
		updateUserTotalScore(users, true, lastRun);
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
	
	private void updateUserWithGroupMembershipScore(Date lastRun){
		List<AdUserFeaturesExtraction> adUserFeaturesExtractions = adUsersFeaturesExtractionRepository.findByClassifierIdAndTimestamp(Classifier.groups.getId(), lastRun);
//		Pageable pageable = new PageRequest(0, 10000, Direction.ASC, AdUserFeaturesExtraction.timestampField);
//		List<AdUserFeaturesExtraction> adUserFeaturesExtractions = adUsersFeaturesExtractionRepository.findByClassifierId(Classifier.groups.getId(), pageable);
		if(adUserFeaturesExtractions.size() == 0){
			logger.warn("the group membership for timestamp ({}) is empty.", lastRun);
			return;
		}
		
		double avgScore = 0;
		for(AdUserFeaturesExtraction extraction: adUserFeaturesExtractions){
			avgScore += extraction.getScore();
		}
		avgScore = avgScore/adUserFeaturesExtractions.size();
		
		ImpalaGroupsScoreWriter impalaGroupsScoreWriter = impalaWriterFactory.createImpalaGroupsScoreWriter();
		List<User> users = new ArrayList<>();
		for(AdUserFeaturesExtraction extraction: adUserFeaturesExtractions){
			User user = userRepository.findOne(extraction.getUserId().toString());
			if(user == null){
				logger.error("user with id ({}) was not found in user table", extraction.getUserId());
				continue;
			}
			//updating the user with the new score.
			user = updateUserScore(user, new Date(extraction.getTimestamp().getTime()), Classifier.groups.getId(), extraction.getScore(), avgScore, false, true);
			if(user != null){
				users.add(user);
				impalaGroupsScoreWriter.writeScore(user, extraction, avgScore);
			}
			
		}
		impalaGroupsScoreWriter.close();
		
		updateUserTotalScore(users, true, lastRun);
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
		Calendar tmp = Calendar.getInstance();
		tmp.setTime(date1);
		Calendar tmp1 = Calendar.getInstance();
		tmp1.setTime(date2);
		int day1 = tmp.get(Calendar.DAY_OF_YEAR);
		int day2 = tmp1.get(Calendar.DAY_OF_YEAR);
		
		return (Math.abs(day1 - day2) <= dayThreshold);
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
		
		List<Long> distinctRuntimes = authDAO.getDistinctRuntime();
		for(Long runtime: distinctRuntimes){
			if(runtime == null){
				logger.warn("got runtime null in the vpndatares table.");
				continue;
			}
			runtime = runtime*1000;
			if(runtime < oldestTime.getTimeInMillis()){
				continue;
			}
			classifierRuntimes.add(new ClassifierRuntime(Classifier.auth, runtime));
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
					updateUserWithAuthScore(new Date(classifierRuntime.getRuntime()));
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
}
