package fortscale.services.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import fortscale.domain.core.ClassifierScore;
import fortscale.domain.core.ScoreInfo;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.fe.IFeature;
import fortscale.services.IUserScore;
import fortscale.services.IUserScoreHistoryElement;
import fortscale.services.UserScoreService;
import fortscale.services.exceptions.UnknownResourceException;
import fortscale.services.fe.Classifier;
import fortscale.services.fe.ClassifierService;

@Service("userScoreService")
public class UserScoreServiceImpl implements UserScoreService{
//	private static Logger logger = Logger.getLogger(UserScoreServiceImpl.class);
	
	private static final int MAX_NUM_OF_HISTORY_DAYS = 21;
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ClassifierService classifierService;

	@Value("${vpn.status.success.value.regex:SUCCESS}")
	private String vpnStatusSuccessValueRegex;
	
	@Value("${ssh.status.success.value.regex:Accepted}")
	private String sshStatusSuccessValueRegex;
	
	

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
			if(isOnSameDay(new Date(), classifierScore.getTimestamp(), 0, MAX_NUM_OF_HISTORY_DAYS)) {
				Classifier classifier = classifierService.getClassifier(classifierScore.getClassifierId());
				if(classifier == null){
					continue;
				}
				UserScore score = new UserScore(user.getId(), classifierScore.getClassifierId(), classifier.getDisplayName(),
						(int)Math.round(classifierScore.getScore()), (int)Math.round(classifierScore.getAvgScore()));
				ret.add(score);
			}
		}
		
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
		
		// since the score for that day takes into account the classifier scores for the previous 24 hours
		// the time range should be for the 24 hours prior to the total score, which may exceed the day limits
		for (ScoreInfo prevTotalScore : user.getScore(Classifier.total.getId()).getPrevScores()) {
			if (dateTimeStart.isBefore(prevTotalScore.getTimestampEpoc()) &&
					dateTimeEnd.isAfter(prevTotalScore.getTimestampEpoc())) {
				// adjust the time range according to the total score in the date range
				dateTimeEnd = new DateTime(prevTotalScore.getTimestampEpoc());
				dateTimeStart = dateTimeEnd.minusHours(24);
				break;
			}
		}

		for(Classifier classifier: Classifier.values()){
			if(!classifier.equals(Classifier.total)) {
				String classifierId = classifier.getId();
				ClassifierScore classifierScore = user.getScore(classifierId);
				if(classifierScore != null){
					ScoreInfo latestScoreInfo = null;
					for(ScoreInfo prevScoreInfo: classifierScore.getPrevScores()) {
						if(dateTimeStart.isAfter(prevScoreInfo.getTimestampEpoc())) {
							// skip classifier score which is before the time range
							continue;
						} else if(dateTimeEnd.isBefore(prevScoreInfo.getTimestampEpoc())) {
							// skip classifier score which is after the time range
							continue;
						}
						// update the latest score info for that classifier
						if ((latestScoreInfo==null) || (latestScoreInfo.getTimestampEpoc() < prevScoreInfo.getTimestampEpoc()))
							latestScoreInfo = prevScoreInfo;
					}
					if (latestScoreInfo!=null) {
						// add the latest score info for the classifier into the results
						UserScore score = new UserScore(user.getId(), classifierId, classifier.getDisplayName(), (int) Math.round(latestScoreInfo.getScore()), (int) Math.round(latestScoreInfo.getAvgScore()));
						ret.put(classifierId, score);
					}
				}
			}
		}
		
		return new ArrayList<IUserScore>(ret.values());
	}

	@Override
	public List<IUserScoreHistoryElement> getUserScoresHistory(String uid, String classifierId, long fromEpochTime, long toEpochTime, int tzShift){
		Classifier.validateClassifierId(classifierId);
		User user = userRepository.findOne(uid);
		if(user == null){
			throw new UnknownResourceException(String.format("user with id [%s] does not exist", uid));
		}
		
		List<IUserScoreHistoryElement> ret = new ArrayList<>();
		ClassifierScore classifierScore = user.getScore(classifierId);
		if(classifierScore != null){
			
			if(!classifierScore.getPrevScores().isEmpty()){
				ScoreInfo scoreInfo = classifierScore.getPrevScores().get(0);
				int i = 0;
				if(classifierScore.getTimestampEpoc() >= fromEpochTime && classifierScore.getTimestampEpoc() <= toEpochTime){
					UserScoreHistoryElement currentScoreHistoryElement = new UserScoreHistoryElement(classifierScore.getTimestamp(), classifierScore.getScore(), classifierScore.getAvgScore());
					//handling the case where the current score and the first prev score are on the same day.
					if(isOnSameDay(classifierScore.getTimestamp(), scoreInfo.getTimestamp(), tzShift)){
						i++;
						//choosing the higher score.
						if(classifierScore.getScore() < scoreInfo.getScore()){
							currentScoreHistoryElement = new UserScoreHistoryElement(scoreInfo.getTimestamp(), scoreInfo.getScore(), scoreInfo.getAvgScore());
						}
					}
					ret.add(currentScoreHistoryElement);
				}
				for(; i < classifierScore.getPrevScores().size(); i++){
					scoreInfo = classifierScore.getPrevScores().get(i);
					if(scoreInfo.getTimestampEpoc() > toEpochTime){
						continue;
					}
					if (scoreInfo.getTimestampEpoc() < fromEpochTime){
						break;
					}

					UserScoreHistoryElement userScoreHistoryElement = new UserScoreHistoryElement(scoreInfo.getTimestamp(), scoreInfo.getScore(), scoreInfo.getAvgScore());
					ret.add(userScoreHistoryElement);
				}
			} else{
				if(classifierScore.getTimestampEpoc() >= fromEpochTime && classifierScore.getTimestampEpoc() <= toEpochTime){
					UserScoreHistoryElement userScoreHistoryElement = new UserScoreHistoryElement(classifierScore.getTimestamp(), classifierScore.getScore(), classifierScore.getAvgScore());
					ret.add(userScoreHistoryElement);
				}
			}
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
	
	@SuppressWarnings("unused")
	private Sort processOrderBy(String orderBy, Direction direction){
		if(direction == null){
			direction = Direction.DESC;
		}
		orderBy = "featureScore";

		Sort sort = new Sort(direction, orderBy);
		return sort;
	}
	
	
	
	@Override
	public boolean isOnSameDay(Date date1, Date date2){
		return isOnSameDay(date1, date2, 0, 0);
	}
	
	public boolean isOnSameDay(Date date1, Date date2, int tzShift){
		return isOnSameDay(date1, date2, tzShift, 0);
	}
	
	private boolean isOnSameDay(Date date1, Date date2, int tzShift, int dayThreshold){
		int millisOffset = tzShift * 60 * 1000;
 		DateTimeZone dateTimeZone = DateTimeZone.forOffsetMillis(millisOffset);
		DateTime dateTime1 = new DateTime(date1.getTime(), dateTimeZone);
		dateTime1 = dateTime1.withTimeAtStartOfDay();
		DateTime dateTime2 = new DateTime(date2.getTime(), dateTimeZone);
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
	
	
	
	
}
