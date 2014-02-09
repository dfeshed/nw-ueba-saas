package fortscale.services.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import fortscale.domain.analyst.ScoreWeight;
import fortscale.domain.core.ClassifierScore;
import fortscale.domain.core.ScoreInfo;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.fe.AdUserFeaturesExtraction;
import fortscale.domain.fe.IFeature;
import fortscale.domain.fe.dao.AdUsersFeaturesExtractionRepository;
import fortscale.services.IUserScore;
import fortscale.services.IUserScoreHistoryElement;
import fortscale.services.UserScoreService;
import fortscale.services.analyst.ConfigurationService;
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
	private AdUsersFeaturesExtractionRepository adUsersFeaturesExtractionRepository;
	
	@Autowired
	private ClassifierService classifierService;
			
	@Autowired
	private ConfigurationService configurationService; 
	
	
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
	
	@SuppressWarnings("unused")
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
	public boolean isOnSameDay(Date date1, Date date2){
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
	
	
	
	
}
