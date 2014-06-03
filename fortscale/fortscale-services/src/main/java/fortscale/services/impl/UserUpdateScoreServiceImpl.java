package fortscale.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import fortscale.domain.analyst.ScoreConfiguration;
import fortscale.domain.analyst.ScoreWeight;
import fortscale.domain.core.ClassifierScore;
import fortscale.domain.core.ScoreInfo;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.events.LogEventsEnum;
import fortscale.domain.fe.AdUserFeaturesExtraction;
import fortscale.domain.fe.AuthScore;
import fortscale.domain.fe.VpnScore;
import fortscale.domain.fe.dao.AdUsersFeaturesExtractionRepository;
import fortscale.domain.fe.dao.AuthDAO;
import fortscale.domain.fe.dao.VpnDAO;
import fortscale.services.UserScoreService;
import fortscale.services.UserService;
import fortscale.services.UserUpdateScoreService;
import fortscale.services.analyst.ConfigurationService;
import fortscale.services.fe.Classifier;
import fortscale.utils.impala.ImpalaPageRequest;
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
		double score = 0;
		
		DateTime dateTime = new DateTime(lastRun.getTime());
		dateTime = dateTime.minusHours(24);//if for some reason a score was not calculated for more than 24 hours then it will not be accounted.
		for(ClassifierScore classifierScore: classifierScoreMap.values()){
			if(dateTime.isAfter(classifierScore.getTimestampEpoc())){
				continue;
			}
			score = Math.max(score, classifierScore.getScore());
		}
		
		ScoreInfo ret = new ScoreInfo();
		ret.setScore(score);
		ret.setAvgScore(0);
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

	public void updateUserWithAuthScore(Classifier classifier){
		Date runtime = new Date();
		updateUserWithAuthScore(classifier, runtime);
	}
	
	@Override
	public void updateUserWithAuthScore(Classifier classifier, Date runtime) {
		AuthDAO authDAO = getAuthDAO(classifier.getLogEventsEnum());
		
		double sum = 0;
		
		logger.info("getting all users");
		List<User> users = userRepository.findAllExcludeAdInfo();
		
		logger.info("calculating {} scores for all users", classifier);
		PageRequest pageable = new ImpalaPageRequest(5, new Sort(Direction.DESC, authDAO.getEventScoreFieldName()));
		
		Map<String, Double> userIdToScoreMap = new HashMap<>();
		for(User user: users){
			List<AuthScore> authScores = authDAO.findEventsByNormalizedUsername(user.getUsername(), pageable);
			double userSum = 0;
			for(AuthScore authScore: authScores){
				userSum += authScore.getEventScore();
			}
			double userScore = userSum/5;
			userIdToScoreMap.put(user.getId(), userScore);
			sum += userScore;
		}
		
		logger.info("updating all the {} users with new scores.", users.size());
		double avg = sum / users.size();
		for(User user: users){
			double userScore = userIdToScoreMap.get(user.getId());
			User updatedUser = updateUserScore(user, runtime, classifier.getId(), userScore, avg, false, false);
			if(updatedUser != null){
				Update update = new Update();
				userService.fillUpdateUserScore(update, user, classifier);
				userService.updateUser(user, update);
			}
		}
		
		logger.info("finished updating the user collection with {} score.", classifier);
	}
		
	
	@Override
	public void updateUserWithVpnScore() {
		Date runtime = new Date();
		updateUserWithVpnScore(runtime);
	}
	
	@Override
	public void updateUserWithVpnScore(Date runtime) {
		double sum = 0;
		
		logger.info("getting all users");
		List<User> users = userRepository.findAllExcludeAdInfo();
		
		logger.info("calculating scores for all users");
		PageRequest pageable = new ImpalaPageRequest(5, new Sort(Direction.DESC, vpnDAO.getEventScoreFieldName()));
		
		Map<String, Double> userIdToScoreMap = new HashMap<>();
		for(User user: users){
			List<VpnScore> vpnScores = vpnDAO.findEventsByNormalizedUsername(user.getUsername(), pageable);
			double userSum = 0;
			for(VpnScore vpnScore: vpnScores){
				userSum += vpnScore.getEventScore();
			}
			double userScore = userSum/5;
			userIdToScoreMap.put(user.getId(), userScore);
			sum += userScore;
		}
		
		logger.info("updating all the {} users with new scores.", users.size());
		double avg = sum / users.size();
		for(User user: users){
			double userScore = userIdToScoreMap.get(user.getId());
			User updatedUser = updateUserScore(user, runtime, Classifier.vpn.getId(), userScore, avg, false, false);
			if(updatedUser != null){
				Update update = new Update();
				userService.fillUpdateUserScore(update, updatedUser, Classifier.vpn);
				userService.updateUser(user, update);
			}
		}
		
		logger.info("finished updating the user collection with vpn score.");
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
	
	
		
	public static class OrderByClassifierScoreTimestempAsc implements Comparator<ClassifierScore>{

		@Override
		public int compare(ClassifierScore o1, ClassifierScore o2) {
			return o1.getTimestamp().compareTo(o2.getTimestamp());
		}
		
	}
}
