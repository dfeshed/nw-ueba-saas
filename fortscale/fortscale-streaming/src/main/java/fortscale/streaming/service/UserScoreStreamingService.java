package fortscale.streaming.service;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fortscale.streaming.exceptions.LevelDbException;
import fortscale.streaming.model.UserTopEvents;

import org.apache.samza.storage.kv.Entry;
import org.apache.samza.storage.kv.KeyValueIterator;
import org.apache.samza.storage.kv.KeyValueStore;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.google.common.base.Throwables;

import fortscale.domain.core.ClassifierScore;
import fortscale.domain.core.ScoreInfo;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.streaming.user.UserScoreSnapshot;
import fortscale.domain.streaming.user.dao.UserScoreSnapshotRepository;

@Service
public class UserScoreStreamingService {
	
	private static final Logger logger = LoggerFactory.getLogger(UserScoreStreamingService.class);
	private static final int UPDATE_MONGO_DB_ITER_MAX_USERS = 1000;
	
	public static int MAX_NUM_OF_PREV_SCORES = 14;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserScoreSnapshotRepository userScoreSnapshotRepository;
	
	@Autowired
	private MongoOperations mongoTemplate;
	
	private KeyValueStore<String, UserTopEvents> store;
	private String classifierId;
	private long latestEventTimeInMillis = 0;
	
	private boolean isUseLatestEventTimeAsCurrentTime = false;

	public void setStore(KeyValueStore<String, UserTopEvents> store) {
		this.store = store;
	}
	
	private long getCurrentEpochTimeInMillis(){
		if(isUseLatestEventTimeAsCurrentTime){
			return latestEventTimeInMillis;
		} else{
			return System.currentTimeMillis();
		}
	}
	
	private void updateLatestEventTime(long eventTimeInMillis){
		if(latestEventTimeInMillis < eventTimeInMillis){
			if(isUseLatestEventTimeAsCurrentTime && !isOnSameDay(eventTimeInMillis, latestEventTimeInMillis)){
				if(latestEventTimeInMillis > 0){
					DateTime dateTime = new DateTime(latestEventTimeInMillis).withTimeAtStartOfDay().plusDays(1).minusSeconds(1);
					while(!isOnSameDay(eventTimeInMillis, dateTime.getMillis())){
						updateDb(dateTime.getMillis());
						dateTime = dateTime.plusDays(1);
					}
				}
				updateDb(eventTimeInMillis);
				exportSnapshot();
			}
			latestEventTimeInMillis = eventTimeInMillis;
		}
	}
	
	public void updateUserWithEventScore(String username, double score, long eventTimeInMillis) throws LevelDbException{
		updateLatestEventTime(eventTimeInMillis);
		
		long currentEpochTime = getCurrentEpochTimeInMillis();
		
		UserTopEvents userTopEvents = store.get(username);
		
		boolean hasToUpdateUserRepository = false;
		boolean hasToUpdateStore = false;
		if(userTopEvents == null){
			userTopEvents = new UserTopEvents(classifierId);
		}
		
		if(!userTopEvents.isFull()){
			hasToUpdateUserRepository = true;
		}
		
		if(userTopEvents.updateEventScores(score, eventTimeInMillis)){
			hasToUpdateStore = true;
		}
		
		double curScore = userTopEvents.calculateUserScore(currentEpochTime);
		
		if(!hasToUpdateUserRepository ){
			double ratio = (1-userTopEvents.getLastUpdatedScore()) / (1-curScore);
			if(ratio > 1.5){
				hasToUpdateUserRepository = true;
			}
		}
		
		if(hasToUpdateUserRepository){
			if(updateDb(username, userTopEvents, currentEpochTime, curScore)){
				hasToUpdateStore = true;
			}
		}
		
		if(hasToUpdateStore){
			userTopEvents.setLastUpdateEpochTime(currentEpochTime);
			try{
				store.put(username, userTopEvents);
			} catch(Exception exception){
            	logger.error("error storing value. username: {} exception: {}", username, exception);
                logger.error("error storing value.", exception);
                throw new LevelDbException(String.format("error while trying to store user %s.", username), exception);
            }
		}
	}
	
	private boolean updateDb(String username, UserTopEvents userTopEvents, long lastUpdateTime, double curScore){
		boolean ret = false;
		try {
			User user = userRepository.findByUsername(username);
			if(user != null && user.getScore(classifierId) != null){
				ClassifierScore cScore = user.getScore(classifierId);
				
				double prevScore = 0;
				if(cScore.getPrevScores().size() > 1){
					prevScore = cScore.getPrevScores().get(1).getScore();
				}
				
				double trendScore = curScore - prevScore;
				userRepository.updateCurrentUserScore(user, classifierId, curScore, trendScore, new DateTime(lastUpdateTime));
				userTopEvents.setLastUpdatedScore(curScore);
				userTopEvents.setLastUpdateScoreEpochTime(lastUpdateTime);
				ret = true;
			}
		} catch (Exception e) {
			logger.error("error updating score for user {} in mongodb from user score stream task", username, e);
			Throwables.propagateIfInstanceOf(e, org.springframework.dao.DataAccessResourceFailureException.class);
		}
		return ret;
	}

	public String getClassifierId() {
		return classifierId;
	}

	public void setClassifierId(String classifierId) {
		this.classifierId = classifierId;
	}
	
	public void exportSnapshot(){
		// go over all users top events in the store and persist them to mongodb
		KeyValueIterator<String, UserTopEvents> iterator = store.all();
		try { 
			while (iterator.hasNext()) {
				Entry<String, UserTopEvents> entry = iterator.next();
				UserTopEvents userTopEvents = entry.getValue();
				if(userTopEvents != null){
					// model might be null in case of a serialization error, in that case
					// we don't want to fail here and the error is logged in the serde implementation
					String username = entry.getKey();
					UserScoreSnapshot userScoreSnapshot = userScoreSnapshotRepository.findByUserNameAndClassifierId(username, classifierId);
					if(userScoreSnapshot == null){
						userScoreSnapshot = new UserScoreSnapshot();
						userScoreSnapshot.setClassifierId(classifierId);
						userScoreSnapshot.setUserName(username);
						userScoreSnapshot.setSnapshot(userTopEvents);
					}
					userScoreSnapshot.setSnapshot(userTopEvents);
					userScoreSnapshotRepository.save(userScoreSnapshot);
				}
			}
		} catch (Exception e) {
			logger.error("error exporting state snapshot for user scores", e);
		} finally {
			if (iterator!=null)
				iterator.close();
		}
	}
	
	public void updateDb(){
		updateDb(getCurrentEpochTimeInMillis());
	}
	
	public void updateDb(long lastUpdateEpochTime) {
		double avgScore = updateLevelDb(lastUpdateEpochTime);
		updateMongoDb(lastUpdateEpochTime, avgScore);
	}
	
	private double updateLevelDb(long lastUpdateEpochTime) {
		// When no events were received yet and the
		// current time is taken out of the latest event
		if (lastUpdateEpochTime == 0)
			return 0;
		
		KeyValueIterator<String, UserTopEvents> iterator = store.all();
		double avgScore = 0;
		int numOfUsers = 0;
		
		// Iterate the users' top events to calculate the average score
		while (iterator.hasNext()) {
			Entry<String, UserTopEvents> entry = iterator.next();
			UserTopEvents userTopEvents = entry.getValue();
			
			// Model might be null in case of a serialization error, in that case we
			// don't want to fail here and the error is logged in the SerDe implementation
			if (userTopEvents != null) {
				double currentScore = userTopEvents.calculateUserScore(lastUpdateEpochTime);
				userTopEvents.setLastUpdatedScore(currentScore);
				userTopEvents.setLastUpdateScoreEpochTime(lastUpdateEpochTime);
				store.put(entry.getKey(), userTopEvents);
				avgScore += currentScore;
				numOfUsers++;
			}
		}
		iterator.close();
		
		if (numOfUsers > 1)
			avgScore /= numOfUsers;
		return avgScore;
	}
	
	private void updateMongoDb(long lastUpdateEpochTime, double avgScore) {
		KeyValueIterator<String, UserTopEvents> iterator = store.all();
		
		try {
			while (iterator.hasNext()) {
				Map<String, UserTopEvents> map = new HashMap<String, UserTopEvents>();
				// Get next UPDATE_MONGO_DB_ITER_MAX_USERS users and store in map
				do {
					Entry<String, UserTopEvents> entry = iterator.next();
					map.put(entry.getKey(), entry.getValue());
				} while (iterator.hasNext() && map.size() < UPDATE_MONGO_DB_ITER_MAX_USERS);
				
				// Get these users from mongoDb and iterate them
				List<User> users = userRepository.findByUsernamesExcludeAdInfo(map.keySet());
				for (User user : users) {
					// Update scores of next user
					try {
						double userScore = map.get(user.getUsername()).calculateUserScore(lastUpdateEpochTime);
						updateUserScore(user, lastUpdateEpochTime, classifierId, userScore);
						updateUserAvgScore(user, avgScore);
						Update update = new Update();
						update.set(User.getClassifierScoreField(classifierId), user.getScore(classifierId));
						mongoTemplate.updateFirst(query(where(User.usernameField).is(user.getUsername())), update, User.class);
					} catch (Exception e) {
						logger.error(String.format("Exception while trying to update the scores of user %s", user.getUsername()), e);
					}
				}
			}
		} catch (Exception e) {
			logger.error("Exception while trying to update the users' scores in mongoDb", e);
			Throwables.propagateIfInstanceOf(e, org.springframework.dao.DataAccessResourceFailureException.class);
		} finally {
			if (iterator != null)
				iterator.close();
		}
	}
	
	private void updateUserAvgScore(User user,double avgScore){
		ClassifierScore cScore = user.getScore(classifierId);
		cScore.setAvgScore(avgScore);
		cScore.getPrevScores().get(0).setAvgScore(avgScore);
	}
	
	private void updateUserScore(User user, long epochtime, String classifierId, double value){
		ClassifierScore cScore = user.getScore(classifierId);
		
		
		boolean isReplaceCurrentScore = true;
		double trend = 0.0; 
		double diffScore = 0.0;
		if(cScore == null){
			cScore = new ClassifierScore();
			cScore.setClassifierId(classifierId);
			ScoreInfo scoreInfo = new ScoreInfo();
			scoreInfo.setScore(value);
			scoreInfo.setTimestamp(new Date(epochtime));
			scoreInfo.setTimestampEpoc(epochtime);
			List<ScoreInfo> prevScores = new ArrayList<ScoreInfo>();
			prevScores.add(scoreInfo);
			cScore.setPrevScores(prevScores);
		}else{
			boolean isOnSameDay = isOnSameDay(epochtime, cScore.getPrevScores().get(0).getTimestamp().getTime());
			if(cScore.getPrevScores().size() > 1){
				double prevScore = cScore.getPrevScores().get(1).getScore() + 0.00001;
				double curScore = value + 0.00001;
				diffScore = curScore - prevScore;
				trend = diffScore / prevScore;
			}
			
			ScoreInfo scoreInfo = new ScoreInfo();
			scoreInfo.setScore(value);
			scoreInfo.setTimestamp(new Date(epochtime));
			scoreInfo.setTimestampEpoc(epochtime);
			scoreInfo.setTrend(trend);
			scoreInfo.setTrendScore(diffScore);
			if (isOnSameDay) {
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
			cScore.setTimestamp(new Date(epochtime));
			cScore.setTimestampEpoc(epochtime);
			cScore.setTrend(trend);
			cScore.setTrendScore(Math.abs(diffScore));
		}
		user.putClassifierScore(cScore);
	}
	
	private boolean isOnSameDay(long epochtime1, long epochtime2){
		DateTime dateTime1 = new DateTime(epochtime1);
		dateTime1 = dateTime1.withTimeAtStartOfDay();
		DateTime dateTime2 = new DateTime(epochtime2);
		dateTime2 = dateTime2.withTimeAtStartOfDay();
		if(dateTime1.equals(dateTime2)){
			return true;
		}
		
		return false;
	}

	public boolean isUseLatestEventTimeAsCurrentTime() {
		return isUseLatestEventTimeAsCurrentTime;
	}

	public void setUseLatestEventTimeAsCurrentTime(
			boolean isUseLatestEventTimeAsCurrentTime) {
		this.isUseLatestEventTimeAsCurrentTime = isUseLatestEventTimeAsCurrentTime;
	}
}
