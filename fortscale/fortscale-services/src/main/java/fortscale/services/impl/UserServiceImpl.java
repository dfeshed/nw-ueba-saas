package fortscale.services.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
	private ImpalaScoreWriterFactory impalaGroupsScoreWriterFactory;
	
	@Autowired 
	private ADUserParser adUserParser; 
	
	
	

	@Override
	public User getUserById(String uid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateUserWithCurrentADInfo() {
		String timestamp = adUserRepository.getLatestTimeStamp();
		if(timestamp != null) {
			updateUserWithADInfo(timestamp);
		} else {
			//TODO: log
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
			user.addApplicationUserDetails(createApplicationUserDetails(UserApplication.ad, user.getUsername()));
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			user.setAdWhenCreated(adUserParser.parseDate(adUser.getWhenCreated()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		user.setAdUserAccountControl(adUser.getUserAccountControl());
		
		ADUserParser adUserParser = new ADUserParser();
		String[] groups = adUserParser.getUserGroups(adUser.getMemberOf());
		if(groups != null){
			for(String groupDN: groups){
				AdGroup adGroup = adGroupRepository.findByDistinguishedName(groupDN);
				if(adGroup != null){
					user.addGroup(new AdUserGroup(groupDN, adGroup.getName()));
				}else{
					//TODO: LOG WARNING.
				}
			}
		}
		
		String[] directReports = adUserParser.getDirectReports(adUser.getDirectReports());
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
					//TODO: LOG WARNING.
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
	public List<IFeature> getUserAttributesScores(String uid, String classifierId, Long timestamp) {
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
		Collections.sort(ufe.getAttributes(), new IFeature.OrderByScoreDesc());
		return ufe.getAttributes();
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
		ScoreConfiguration scoreConfiguration = configurationService.getScoreConfiguration();
		for(User user: users){
			double totalWeights = 0.00001;
			double score = 0;
			double avgScore = 0;
			
			for(ScoreWeight scoreWeight: scoreConfiguration.getConfMap().values()){
				ClassifierScore classifierScore = user.getScore(scoreWeight.getId());
				if(classifierScore != null){
					totalWeights += scoreWeight.getWeight();
					
					score += classifierScore.getScore() * scoreWeight.getWeight();
					avgScore += classifierScore.getAvgScore() * scoreWeight.getWeight();					
				}
			}
			
			updateUserScore(user, lastRun, Classifier.total.getId(), score/totalWeights, avgScore/totalWeights, false);
		}
		
		if(isToSave){
			userRepository.save(users);
			saveUserTotalScoreToImpala(lastRun);
		}
	}
	
	private void saveUserTotalScoreToImpala(Date timestamp){
		List<User> users = userRepository.findAll();
		ImpalaTotalScoreWriter writer = impalaGroupsScoreWriterFactory.createImpalaTotalScoreWriter();
		for(User user: users){
			if(user.getScore(Classifier.total.getId()) != null){
				writer.writeScore(user, timestamp);
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
			User user = userRepository.findByUsername(authScore.getUserName().toLowerCase());
			if(user == null){
				//TODO:	error log message
				continue;
			}
			user = updateUserScore(user, lastRun, Classifier.auth.getId(), authScore.getGlobalScore(), avg, false);
			if(user != null){
				users.add(user);
			}
		}
		updateUserTotalScore(users, true, lastRun);		
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
			String userName = vpnScore.getUserName();
			List<User> tmpUsers = userRepository.findByAdUserPrincipalNameContaining(userName.toLowerCase());
			if(tmpUsers == null | tmpUsers.size() == 0 | tmpUsers.size() > 1){
				//TODO:	error log message
				continue;
			}
			User user = tmpUsers.get(0);
			createApplicationUserDetailsIfNotExist(user, new ApplicationUserDetails(UserApplication.vpn.getId(), userName));
			user = updateUserScore(user, lastRun, Classifier.vpn.getId(), vpnScore.getGlobalScore(), avg, false);
			if(user != null){
				users.add(user);
			}
		}
		updateUserTotalScore(users, true, lastRun);
	}
	
	@Override
	public void updateUserWithGroupMembershipScore(){
		Date lastRun = adUsersFeaturesExtractionRepository.getLatestTimeStamp();
		if(lastRun == null){
			//TODO: WARN LOG
			return;
		}
		updateUserWithGroupMembershipScore(lastRun);
	}
	
	private void updateUserWithGroupMembershipScore(Date lastRun){
		List<AdUserFeaturesExtraction> adUserFeaturesExtractions = adUsersFeaturesExtractionRepository.findByClassifierIdAndTimestamp(Classifier.groups.getId(), lastRun);
//		Pageable pageable = new PageRequest(0, 10000, Direction.ASC, AdUserFeaturesExtraction.timestampField);
//		List<AdUserFeaturesExtraction> adUserFeaturesExtractions = adUsersFeaturesExtractionRepository.findByClassifierId(Classifier.groups.getId(), pageable);
		if(adUserFeaturesExtractions.size() == 0){
			//TODO: WARN LOG
			return;
		}
		
		double avgScore = 0;
		for(AdUserFeaturesExtraction extraction: adUserFeaturesExtractions){
			avgScore += extraction.getScore();
		}
		avgScore = avgScore/adUserFeaturesExtractions.size();
		
		ImpalaGroupsScoreWriter impalaGroupsScoreWriter = impalaGroupsScoreWriterFactory.createImpalaGroupsScoreWriter();
		List<User> users = new ArrayList<>();
		for(AdUserFeaturesExtraction extraction: adUserFeaturesExtractions){
			User user = userRepository.findOne(extraction.getUserId().toString());
			if(user == null){
				//TODO: error log.
				continue;
			}
			//updating the user with the new score.
			user = updateUserScore(user, new Date(extraction.getTimestamp().getTime()), Classifier.groups.getId(), extraction.getScore(), avgScore, false);
			if(user != null){
				users.add(user);
			}
			impalaGroupsScoreWriter.writeScore(user, extraction, avgScore);
		}
		impalaGroupsScoreWriter.close();
		
		updateUserTotalScore(users, true, lastRun);
	}
	
	@Override
	public User updateUserScore(User user, Date timestamp, String classifierId, double value, double avgScore, boolean isToSave){
		ClassifierScore cScore = user.getScore(classifierId);
		boolean isReplaceCurrentScore = true;
		double trend = 0.0;
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
			if(cScore.getPrevScores().size() > 1){
				double prevScore = cScore.getPrevScores().get(1).getScore() + 0.00001;
				double curScore = value + 0.00001;
				trend = (curScore - prevScore) / prevScore;
			}
			
			ScoreInfo scoreInfo = new ScoreInfo();
			scoreInfo.setScore(value);
			scoreInfo.setAvgScore(avgScore);
			scoreInfo.setTimestamp(timestamp);
			scoreInfo.setTimestampEpoc(timestamp.getTime());
			scoreInfo.setTrend(trend);
			scoreInfo.setTrendScore(Math.abs(trend));
			if (isOnSameDay(timestamp, cScore.getTimestamp())) {
				if(value < cScore.getScore()){
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
			cScore.setTrendScore(Math.abs(trend));
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

	@Override
	public void createApplicationUserDetailsIfNotExist(User user, ApplicationUserDetails applicationUserDetails) {
		if(!user.containsApplicationUserDetails(applicationUserDetails)) {
			user.addApplicationUserDetails(applicationUserDetails);
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
		List<ClassifierRuntime> classifierRuntimes = new ArrayList<>();
		for(User user: userRepository.findAll()){
			user.removeAllScores();
		}
		
		List<Date> distinctDates = adUsersFeaturesExtractionRepository.getDistinctRuntime(Classifier.groups.getId());
		for(Date date: distinctDates){
			classifierRuntimes.add(new ClassifierRuntime(Classifier.groups, date.getTime()));
		}
		
		List<Long> distinctRuntimes = authDAO.getDistinctRuntime();
		for(Long runtime: distinctRuntimes){
			classifierRuntimes.add(new ClassifierRuntime(Classifier.auth, runtime));
		}
		
		distinctRuntimes = vpnDAO.getDistinctRuntime();
		for(Long runtime: distinctRuntimes){
			classifierRuntimes.add(new ClassifierRuntime(Classifier.vpn, runtime));
		}
		
		Collections.sort(classifierRuntimes);
		
		for(ClassifierRuntime classifierRuntime: classifierRuntimes){
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
			int ret = (int)(this.runtime - o.runtime);
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
	
	
}
