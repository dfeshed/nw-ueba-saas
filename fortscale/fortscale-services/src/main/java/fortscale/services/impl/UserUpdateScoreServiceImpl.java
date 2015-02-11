package fortscale.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import fortscale.domain.analyst.ScoreWeight;
import fortscale.domain.core.ClassifierScore;
import fortscale.domain.core.ScoreInfo;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.fe.AdUserFeaturesExtraction;
import fortscale.domain.fe.dao.AdUsersFeaturesExtractionRepository;
import fortscale.services.UserScoreService;
import fortscale.services.UserService;
import fortscale.services.UserUpdateScoreService;
import fortscale.services.analyst.ConfigurationService;
import fortscale.services.fe.Classifier;
import fortscale.utils.logging.Logger;

@Service("userUpdateScoreService")
public class UserUpdateScoreServiceImpl implements UserUpdateScoreService {
	private static Logger logger = Logger.getLogger(UserUpdateScoreServiceImpl.class);
	
	public static int MAX_NUM_OF_PREV_SCORES = 14;
	public static String SCORE_DECAY_FIELD_NAME = "scoredecay";

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserService userService;
	
	@Autowired 
	private UserScoreService userScoreService;
	
	@Autowired
	private AdUsersFeaturesExtractionRepository adUsersFeaturesExtractionRepository;
		
	@Autowired
	private ImpalaWriterFactory impalaWriterFactory;
	
	@Autowired
	private ConfigurationService configurationService; 
	
	
	
	@Value("${user.score.oldest.event.diff.in.sec:1209600}")
	private int userScoreOldestEventDiffFromNowInSeconds;
	
	@Value("${user.score.num.of.top.events:5}")
	private int userScoreNumOfTopEvents;
	
	@Value("${group.membership.score.page.size}")
	private int groupMembershipScorePageSize;
	
	@Value("${total.score.page.size}")
	private int totalScorePageSize;
	
	@Override
	public void updateUserTotalScore() {
		long count = userRepository.count();
		if (count == 0)
			return;
		
		ImpalaTotalScoreWriter writer = null;
		Date lastRun = new Date();
		
		try {
			writer = impalaWriterFactory.createImpalaTotalScoreWriter();
			// Calculate number of pages and iterate them
			int numOfPages = (int)(((count - 1) / totalScorePageSize) + 1);
			for (int i = 0; i < numOfPages; i++) {
				Pageable pageable = new PageRequest(i, totalScorePageSize);
				// Get list of users on next page
				List<User> users = userRepository.findAllExcludeAdInfo(pageable);
				// Iterate these users
				for (User user : users) {
					try {
						// Update total score
						updateUserTotalScore(user, lastRun);
						Update update = new Update();
						update.set(User.getClassifierScoreField(Classifier.total.getId()), user.getScore(Classifier.total.getId()));
						userService.updateUser(user, update);
						// Write to Impala
						writer.writeScores(user, lastRun, configurationService.getScoreConfiguration());
					} catch (Exception e) {
						logger.error(String.format("Exception while trying to update and write the total score of user %s", user.getUsername()), e);
					}
				}
			}
		} catch (Exception e) {
			logger.error(String.format("Exception while trying to update the users' total scores"), e);
		} finally {
			if (writer != null)
				writer.close();
		}
	}
	
	private void updateUserTotalScore(User user, Date lastRun){
		ScoreInfo totalScore = calculateTotalScore(configurationService.getScoreConfiguration().getConfMap().values(), user.getScores(), lastRun);
		
		updateUserScore(user, lastRun, Classifier.total.getId(), totalScore.getScore(), totalScore.getAvgScore(), false);
	}
	
	private ScoreInfo calculateTotalScore(Collection<ScoreWeight> scoreWeights, Map<String, ClassifierScore> classifierScoreMap, Date lastRun){
		double score = 0;
		
		DateTime dateTime = new DateTime(lastRun.getTime());
		dateTime = dateTime.minusHours(24);//if for some reason a score was not calculated for more than 24 hours then it will not be accounted.
		for(ClassifierScore classifierScore: classifierScoreMap.values()){
			if(classifierScore == null){
				logger.warn("got a user with null classifier score");
				continue;
			}
			if(classifierScore.getClassifierId().equals(Classifier.total.getId())){
				continue;
			}
			if(dateTime.isAfter(classifierScore.getTimestampEpoc())){
				continue;
			}
			score = Math.max(score, classifierScore.getScore());
		}
		
		ScoreInfo ret = new ScoreInfo();
		ret.setScore(Math.round(score));
		ret.setAvgScore(0);
		return ret;
	}
	
	@Override
	public void updateUserWithGroupMembership(){
		Date lastRun = adUsersFeaturesExtractionRepository.getLatestTimeStamp();
		if(lastRun == null){
			logger.warn("there is no timestamp. probably the table is empty.");
			return;
		}
		updateUserWithGroupMembership(lastRun);
	}

	private void updateUserWithGroupMembership(final Date lastRun){
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
					User user = userService.findByUserId(extraction.getUserId());
					if(user != null){
						//writeScore doesn't write only the score, but the entire row of data.
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


	@Override
	public void recalculateTotalScore(){
		List<User> users = userRepository.findAllExcludeAdInfo();
		for(User user: users){
			try{
				recalculateTotalScore(user);
				Update update = new Update();
				update.set(User.getClassifierScoreField(Classifier.total.getId()), user.getScore(Classifier.total.getId()));
				userService.updateUser(user, update);
			} catch(Exception e){
				logger.error(String.format("got the following exception while trying to recalculate the total score for user %s", user.getUsername()),e);
			}
		}
	}
	
	private void recalculateTotalScore(User user){
		List<ClassifierScore> classifierScores = new ArrayList<>();
		for(ClassifierScore classifierScore: user.getScores().values()){
			if(classifierScore == null){
				logger.warn("user {} has null classifier score", user.getUsername());
				continue;
			}
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
			updateUserScore(user, classifierScore.getTimestamp(), classifierScore.getClassifierId(), classifierScore.getScore(), classifierScore.getAvgScore(),isSaveMaxScore);
			updateUserTotalScore(user, classifierScore.getTimestamp());
		}
	}
	
	@Override
	public User updateUserScore(User user, Date timestamp, String classifierId, double value, double avgScore, boolean isSaveMaxScore){
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
			boolean isOnSameDay = userScoreService.isOnSameDay(timestamp, cScore.getPrevScores().get(0).getTimestamp());
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
		return user;
	}
	
	
		
	public static class OrderByClassifierScoreTimestempAsc implements Comparator<ClassifierScore>{

		@Override
		public int compare(ClassifierScore o1, ClassifierScore o2) {
			return o1.getTimestamp().compareTo(o2.getTimestamp());
		}
		
	}
}
