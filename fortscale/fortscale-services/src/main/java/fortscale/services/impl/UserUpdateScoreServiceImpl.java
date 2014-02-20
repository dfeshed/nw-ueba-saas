package fortscale.services.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import fortscale.domain.analyst.ScoreConfiguration;
import fortscale.domain.analyst.ScoreWeight;
import fortscale.domain.core.ClassifierScore;
import fortscale.domain.core.ScoreInfo;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.fe.AdUserFeaturesExtraction;
import fortscale.domain.fe.AuthScore;
import fortscale.domain.fe.VpnScore;
import fortscale.domain.fe.dao.AdUsersFeaturesExtractionRepository;
import fortscale.domain.fe.dao.AuthDAO;
import fortscale.domain.fe.dao.VpnDAO;
import fortscale.services.LogEventsEnum;
import fortscale.services.UserApplication;
import fortscale.services.UserScoreService;
import fortscale.services.UserService;
import fortscale.services.UserUpdateScoreService;
import fortscale.services.analyst.ConfigurationService;
import fortscale.services.fe.Classifier;
import fortscale.services.impl.UserServiceImpl.OrderByClassifierScoreTimestempAsc;
import fortscale.utils.logging.Logger;

@Service("userUpdateScoreService")
public class UserUpdateScoreServiceImpl implements UserUpdateScoreService {
	private static Logger logger = Logger.getLogger(UserUpdateScoreServiceImpl.class);
	
	public static int MAX_NUM_OF_PREV_SCORES = 14;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserService userService;
	
	@Autowired 
	private UserScoreService userScoreService;
	
	@Autowired
	private AdUsersFeaturesExtractionRepository adUsersFeaturesExtractionRepository;
	
	@Autowired
	private AuthDAO loginDAO;
	
	@Autowired
	private AuthDAO sshDAO;
	
	@Autowired
	private VpnDAO vpnDAO;
	
	@Autowired
	private ImpalaWriterFactory impalaWriterFactory;
	
	@Autowired
	private ConfigurationService configurationService; 
	
	
	@Value("${vpn.status.success.value.regex:SUCCESS}")
	private String vpnStatusSuccessValueRegex;
	
	@Value("${ssh.status.success.value.regex:Accepted}")
	private String sshStatusSuccessValueRegex;
	
	@Value("${group.membership.score.page.size}")
	private int groupMembershipScorePageSize;
	
	@Value("${total.score.page.size}")
	private int totalScorePageSize;
	

	@Override
	public void updateUserTotalScore(){
		logger.info("getting all users");
		List<User> users = userRepository.findAllExcludeAdInfo();
		Date lastRun = new Date();
		
		logger.info("update all user total score.");
		for(User user: users){
			updateUserTotalScore(user, lastRun);
		}
		
		for(User user: users){
			Update update = new Update();
			update.set(User.getClassifierScoreField(Classifier.total.getId()), user.getScore(Classifier.total.getId()));
			userService.updateUser(user, update);
		}
		
		saveUserTotalScoreToImpala(users, lastRun, configurationService.getScoreConfiguration());
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
	
	
		
	private void saveUserTotalScoreToImpala(List<User> users, Date timestamp, ScoreConfiguration scoreConfiguration){
		ImpalaTotalScoreWriter writer = null;
		try{
			writer = impalaWriterFactory.createImpalaTotalScoreWriter();
	
			saveUserTotalScoreToImpala(writer, users, timestamp, scoreConfiguration);			
		} finally{
			if(writer != null){
				writer.close();
			}
		}
	}
	
	private void saveUserTotalScoreToImpala(ImpalaTotalScoreWriter writer, List<User> users, Date timestamp, ScoreConfiguration scoreConfiguration){
		logger.info("writing {} users total score to the file system.", users.size());
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
	
	@Override
	public void updateUserWithAuthScore(Classifier classifier, Date lastRun) {
		AuthDAO authDAO = getAuthDAO(classifier.getLogEventsEnum());
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
	
	//This code is left for possible use in the near future.
	@SuppressWarnings("unused")
	private Map<String, List<User>> getUsernameToUsersMap(String tablename){
		logger.info("getting all users");
		Map<String, List<User>> usersMap = new HashMap<>();
		for(User user: userRepository.findAllExcludeAdInfo()){
			String logUsername = user.getLogUserName(tablename);
			if(logUsername != null){
				List<User> tmp = usersMap.get(logUsername.toLowerCase());
				if(tmp == null){
					tmp = new ArrayList<>();
					usersMap.put(logUsername.toLowerCase(), tmp);
				}
				tmp.add(user);
			} else{
				List<User> tmp = usersMap.get(user.getUsername().toLowerCase());
				if(tmp == null){
					tmp = new ArrayList<>();
					usersMap.put(user.getUsername().toLowerCase(), tmp);
				}
				tmp.add(user);
				String usernameSplit[] = StringUtils.split(user.getUsername(), "@");
				if(usernameSplit.length > 1){
					tmp = usersMap.get(usernameSplit[0].toLowerCase());
					if(tmp == null){
						tmp = new ArrayList<>();
						usersMap.put(usernameSplit[0].toLowerCase(), tmp);
					}
					tmp.add(user);
				}
			}
		}
		return usersMap;
	}
	
	private User findByAuthUsername(LogEventsEnum eventId, String username){
		return userService.findByAuthUsername(eventId, username);
	}
	
	private void updateUserWithAuthScore(AuthDAO authDAO, final Classifier classifier, Date lastRun) {
		logger.info("calculating avg score for {} events.", classifier);
		double avg = 0;
		try{
			avg = authDAO.calculateAvgScoreOfGlobalScore(lastRun);
		} catch(Exception e){
			int count = authDAO.countNumOfEvents(lastRun);
			String message;
			if(count > 0){
				message = String.format("while running calculateAvgScoreOfGlobalScore on the table (%s) with runtime (%s) got an exception.", authDAO.getTableName(), lastRun);
				throw new RuntimeException(message,e);
			} else{
				logger.info("no events found on runtime: {}", lastRun);
				return;
			}
		}
		logger.info("getting all {} scores", classifier);
		List<AuthScore> authScores = authDAO.findGlobalScoreByTimestamp(lastRun);
		
				
		logger.info("going over all {} {} scores", authScores.size(), classifier);
		final String tablename = authDAO.getTableName();
		List<Update> updates = new ArrayList<>();
		List<User> users = new ArrayList<>();
		List<User> newUsers = new ArrayList<>();
		for(AuthScore authScore: authScores){
			final String username = authScore.getUserName();
			if(StringUtils.isEmpty(username)){
				logger.warn("got a empty string {} username", classifier);
				continue;
			}

			User user = findByAuthUsername(classifier.getLogEventsEnum(), username);
			if(user == null){
				if(classifier.getLogEventsEnum().equals(LogEventsEnum.ssh)){
					logger.info("no user was found with SSH username ({})", username);
					if(authDAO.countNumOfEventsByUserAndStatusRegex(lastRun, username, sshStatusSuccessValueRegex) > 0){
						logger.info("creating a new user from a successed ssh event. ssh username ({})", username);
						user = userService.createUser(UserApplication.ssh, authScore.getUserName());
					} else{
						continue;
					}
				} else{
					logger.warn("no user was found with the username {}", username);
					continue;
				}
			}

			final boolean isNewLogUsername = (user.getLogUserName(authDAO.getTableName()) == null) ? true : false;
			boolean isNewAppUsername = false;
			if(isNewLogUsername){
				isNewAppUsername = userService.createNewApplicationUserDetails(user, classifier.getUserApplication(), username, false);
				userService.updateLogUsername(user, authDAO.getTableName(), username, false);
			}
			
			user = updateUserScore(user, lastRun, classifier.getId(), authScore.getGlobalScore(), avg, false, false);
			if(user != null){
				if(user.getId() != null){
					Update update = new Update();
					userService.fillUpdateUserScore(update, user, classifier);
					if(isNewLogUsername){
						userService.fillUpdateLogUsername(update, username, tablename);
					}
					if(isNewAppUsername){
						userService.fillUpdateAppUsername(update, user, classifier);
					}
					updates.add(update);
					users.add(user);
				} else{
					newUsers.add(user);
				}
			}
		}
		
		logger.info("finished processing {} {} scores.",	authScores.size(), classifier);
		
		logger.info("updating all the {} users.", users.size());
		for(int i = 0; i < users.size(); i++){
			Update update = updates.get(i);
			User user = users.get(i);
			userService.updateUser(user, update);
		}
		
		logger.info("adding {} new users.", newUsers.size());
		if(newUsers.size() > 0){
			userRepository.save(newUsers);
		}
		
		logger.info("finished updating the user collection. with {} score and total score.", classifier);
	}	
	
	
	@Override
	public void updateUserWithVpnScore() {
		Date lastRun = vpnDAO.getLastRunDate();
		updateUserWithVpnScore(lastRun);
	}
	
	@Override
	public void updateUserWithVpnScore(Date lastRun) {
		logger.info("calculating avg score for vpn events.");
		double avg = 0;
		try{
			avg = vpnDAO.calculateAvgScoreOfGlobalScore(lastRun);
		} catch(Exception e){
			int count = vpnDAO.countNumOfRecords();
			String message;
			if(count > 0){
				message = String.format("while running calculateAvgScoreOfGlobalScore on the table (%s) with runtime (%s) got an exception.", vpnDAO.getTableName(), lastRun);
			} else{
				message = String.format("the table (%s) is empty", vpnDAO.getTableName());
			}
			throw new RuntimeException(message,e);
		}
		
		logger.info("getting all vpn scores");
		List<VpnScore> vpnScores = vpnDAO.findGlobalScoreByTimestamp(lastRun);
		
		final String tablename = VpnScore.TABLE_NAME;
				
		logger.info("going over all {} vpn scores", vpnScores.size());
		List<User> newUsers = new ArrayList<>();
		List<Update> updates = new ArrayList<>();
		List<User> users = new ArrayList<>();
		for(VpnScore vpnScore: vpnScores){
			String username = vpnScore.getUserName();
			if(StringUtils.isEmpty(username)){
				logger.warn("got a empty string vpn username");
				continue;
			}
			
			User user = findByAuthUsername(LogEventsEnum.vpn, username);
			if(user == null){
				logger.info("no user was found with vpn username ({})", username);
				if(vpnDAO.countNumOfEventsByUserAndStatusRegex(lastRun, username, vpnStatusSuccessValueRegex) > 0){
					logger.info("creating a new user from a successed vpn event. vpn username ({})", username);
					user = createUser(vpnScore);
				} else{
					continue;
				}
			}
			
			final boolean isNewLogUsername = (user.getLogUserName(tablename) == null) ? true : false;
			if(isNewLogUsername){
				userService.createNewApplicationUserDetails(user, UserApplication.vpn, username, false);
				updateVpnLogUsername(user, username, false);
			}
			
			user = updateUserScore(user, lastRun, Classifier.vpn.getId(), vpnScore.getGlobalScore(), avg, false, false);
			if(user != null){
				if(user.getId() != null){
					Update update = new Update();
					update.set(User.getClassifierScoreField(Classifier.vpn.getId()), user.getScore(Classifier.vpn.getId()));
					if(isNewLogUsername){
						update.set(User.getLogUserNameField(tablename), user.getLogUserName(tablename));
						update.set(User.getAppField(UserApplication.vpn.getId()), user.getApplicationUserDetails().get(UserApplication.vpn.getId()));
					}
					updates.add(update);
					users.add(user);
				} else{
					newUsers.add(user);
				}
			}
		}
		
		logger.info("finished processing {} vpn scores.",	vpnScores.size());
		
		logger.info("updating all the {} users.", users.size());
		for(int i = 0; i < users.size(); i++){
			Update update = updates.get(i);
			User user = users.get(i);
			userService.updateUser(user, update);
		}
		
		logger.info("inserting {} new users.", newUsers.size());
		if(newUsers.size() > 0){
			userRepository.save(newUsers);
		}
				
		logger.info("finished updating the user collection. with vpn score and total score.");		
	}
	
	
	
	private void updateVpnLogUsername(User user, String username, boolean isSave){
		userService.updateLogUsername(user, VpnScore.TABLE_NAME, username, isSave);
	}
	
	private User createUser(VpnScore vpnScore){
		return userService.createUser(UserApplication.vpn, vpnScore.getUserName());
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
		logger.info("start updating the user collection with group membership score");
		long count = adUsersFeaturesExtractionRepository.countByClassifierIdAndTimestamp(Classifier.groups.getId(), lastRun); 
		if(count == 0){
			logger.warn("the group membership for timestamp ({}) is empty.", lastRun);
			return;
		}
		
		logger.info("total number of group membership score is {}", count);
		
		logger.info("calculating average score...");
		final double avgScore = adUsersFeaturesExtractionRepository.calculateAvgScore(Classifier.groups.getId(), lastRun);
		logger.info("average score is {}", avgScore);
				
		int numOfPages = (int) (((count -1) / groupMembershipScorePageSize) + 1); 
		
		ImpalaGroupsScoreWriter impalaGroupsScoreWriter = null;
		try{
			impalaGroupsScoreWriter = impalaWriterFactory.createImpalaGroupsScoreWriter();
			for(int i = 0; i < numOfPages; i++){
				logger.info("retrieving page #{} of group membership score documents. page size is {}.", i, groupMembershipScorePageSize);
				PageRequest pageRequest = new PageRequest(i, groupMembershipScorePageSize);
				List<AdUserFeaturesExtraction> adUserFeaturesExtractions = adUsersFeaturesExtractionRepository.findByClassifierIdAndTimestamp(Classifier.groups.getId(), lastRun, pageRequest);
				
				logger.info("updating the user collection and the hdfs with group membership score");
				for(AdUserFeaturesExtraction extraction: adUserFeaturesExtractions){
					User user = updateUserWithGroupMembershipScore(lastRun, avgScore, extraction);
					if(user != null){
						impalaGroupsScoreWriter.writeScore(user, extraction, avgScore);
					}
				}
				
				logger.info("finished updating the user collection and the hdfs with group membership score");
				
			}
		} finally{
			if(impalaGroupsScoreWriter != null){
				impalaGroupsScoreWriter.close();
			}
		}
		
		logger.info("finished group membership score update.");
	}
		
	public void saveUserGroupMembershipScoreToImpala(ImpalaGroupsScoreWriter impalaGroupsScoreWriter, List<User> users,  UpdateUserGroupMembershipScoreContext updateUserGroupMembershipScoreContext, double avgScore){
		logger.info("writing group score to file");
		for(User user: users){
			AdUserFeaturesExtraction adUserFeaturesExtraction = updateUserGroupMembershipScoreContext.findFeautreByUserId(user.getId());
			if(adUserFeaturesExtraction != null){
				impalaGroupsScoreWriter.writeScore(user, adUserFeaturesExtraction, avgScore);
			}
		}
		logger.info("finished writing group score to file");
	}
	
	private User updateUserWithGroupMembershipScore(final Date lastRun, double avgScore, AdUserFeaturesExtraction extraction){		
		User user = userService.findByUserId(extraction.getUserId());
		if(user == null){
			logger.warn("user with id ({}) was not found in user table", extraction.getUserId());
			return null;
		}
		//updating the user with the new score.
		user = updateUserScore(user, new Date(extraction.getTimestamp().getTime()), Classifier.groups.getId(), extraction.getScore(), avgScore, false, false);
	
		Update update = new Update();
		update.set(User.getClassifierScoreField(Classifier.groups.getId()), user.getScore(Classifier.groups.getId()));
		userService.updateUser(user, update);
		
		return user;
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
			boolean isOnSameDay = userScoreService.isOnSameDay(timestamp, cScore.getTimestamp());
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
}
