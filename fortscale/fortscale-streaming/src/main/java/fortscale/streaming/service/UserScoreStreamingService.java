package fortscale.streaming.service;

import com.google.common.base.Throwables;
import fortscale.domain.core.ClassifierScore;
import fortscale.domain.core.ScoreInfo;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.streaming.user.UserScoreSnapshot;
import fortscale.domain.streaming.user.dao.UserScoreSnapshotRepository;
import fortscale.streaming.exceptions.LevelDbException;
import fortscale.streaming.model.UserEventTypePair;
import fortscale.streaming.model.UserTopEvents;
import org.apache.samza.storage.kv.Entry;
import org.apache.samza.storage.kv.KeyValueIterator;
import org.apache.samza.storage.kv.KeyValueStore;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
public class UserScoreStreamingService {

	private static final Logger logger = LoggerFactory.getLogger(UserScoreStreamingService.class);

	public static int MAX_NUM_OF_PREV_SCORES = 14;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserScoreSnapshotRepository userScoreSnapshotRepository;
	
	@Autowired
	private MongoOperations mongoTemplate;

	@Value("${user.score.streaming.service.page.size:1000}")
	private int userScoreStreamingServicePageSize;

	private KeyValueStore<UserEventTypePair, UserTopEvents> store;

	private boolean isUseLatestEventTimeAsCurrentTime = false;

	public void setStore(KeyValueStore<UserEventTypePair, UserTopEvents> store) {
		this.store = store;
	}
	
	private long getCurrentEpochTimeInMillis(UserTopEvents userTopEvents){
		if(isUseLatestEventTimeAsCurrentTime){
			return userTopEvents.getLatestRecievedEventEpochTime();
		} else{
			return System.currentTimeMillis();
		}
	}
	
	private void checkIfNeedToUpdatePastScores(String dataSource, long latestEventTimeInMillis, long eventTimeInMillis){
		if(latestEventTimeInMillis < eventTimeInMillis){
			if(isUseLatestEventTimeAsCurrentTime && !isOnSameDay(eventTimeInMillis, latestEventTimeInMillis)){
				if(latestEventTimeInMillis > 0){
					DateTime dateTime = new DateTime(latestEventTimeInMillis).withTimeAtStartOfDay().plusDays(1).minusSeconds(1);
					while(!isOnSameDay(eventTimeInMillis, dateTime.getMillis())){
						updateDb(dataSource, dateTime.getMillis());
						dateTime = dateTime.plusDays(1);
					}
				}
				updateDb(dataSource, eventTimeInMillis);
				exportSnapshotForDataSource(dataSource);
			}
		}
	}
	
	public void updateUserWithEventScore(String username, String dataSource, double score, long eventTimeInMillis) throws LevelDbException{
		// build a store key and get the top scores from the store
		UserEventTypePair key = new UserEventTypePair(username, dataSource);
		UserTopEvents userTopEvents = store.get(key);

		boolean hasToUpdateUserRepository = (userTopEvents == null);
		boolean hasToUpdateStore = (userTopEvents == null);
		if(userTopEvents == null){
			userTopEvents = new UserTopEvents(username);
		}

		// Update mongodb in case we are adding a new event score to the state, or in case the score has changed
		// significantly. The check below ensure that we update mongodb in case of new event score added to the
		// store and the check after the call to userTopEvents.calculateUserScore ensure that we update also
		// in case the score changed significantly.
		// We distinguish between the cases, since we don't want to update mongodb in case we replaced a score of 90
		// with a score of 92 in the state as the impact on the user score is minimal so the trade-off by not updating
		// mongodb is negligible.
		if(!userTopEvents.isFull()) {
			hasToUpdateUserRepository = true;
		}

		checkIfNeedToUpdatePastScores(dataSource, userTopEvents.getLatestRecievedEventEpochTime(), eventTimeInMillis);
		
		if(userTopEvents.updateEventScores(score, eventTimeInMillis)){
			hasToUpdateStore = true;
		}

		long currentEpochTime = getCurrentEpochTimeInMillis(userTopEvents);

		// determine if we need to update user score in mongodb, if the score changed considerably
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
				store.put(key, userTopEvents);
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
			String classifierId = userTopEvents.getEventType();
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
			logger.error("error updating score for user {} and classifier {} in mongodb from user score stream task", username, userTopEvents.getEventType(), e);
			Throwables.propagateIfInstanceOf(e, org.springframework.dao.DataAccessResourceFailureException.class);
		}
		return ret;
	}

	public void cleanupScores(String dataSource) {
		// go over all leveldb data and clean the specific data
		KeyValueIterator<UserEventTypePair, UserTopEvents> iterator = store.all();
		while (iterator.hasNext()) {
			Entry<UserEventTypePair, UserTopEvents> entry = iterator.next();
			UserEventTypePair key = entry.getKey();
			if (key!=null && dataSource.equals(key.getEventType())) {
				store.delete(key);
			}
		}
		iterator.close();

		// delete all relevant snapshots in mongodb
		userScoreSnapshotRepository.clearAllClassifiersScores(dataSource);
	}

	/**
	 * export snapshot for a specific data source or for all data source. null value passed to the data source
	 * parameter signal all data sources.
	 */
	private void exportSnapshotForDataSource(String dataSource) {
		// go over all users top events in the store and persist them to mongodb
		KeyValueIterator<UserEventTypePair, UserTopEvents> iterator = store.all();
		try {
			while (iterator.hasNext()) {
				Entry<UserEventTypePair, UserTopEvents> entry = iterator.next();
				UserEventTypePair key = entry.getKey();
				UserTopEvents userTopEvents = entry.getValue();
				if (key != null && userTopEvents != null) {
					// model might be null in case of a serialization error, in that case
					// we don't want to fail here and the error is logged in the serde implementation
					String username = key.getUsername();
					String classifierId = key.getEventType();
					if (dataSource==null || dataSource.equals(classifierId)) {
						UserScoreSnapshot userScoreSnapshot = userScoreSnapshotRepository.findByUserNameAndClassifierId(username, classifierId);
						if (userScoreSnapshot == null) {
							userScoreSnapshot = new UserScoreSnapshot();
							userScoreSnapshot.setUserName(username);
							userScoreSnapshot.setClassifierId(classifierId);
						}
						userScoreSnapshot.setSnapshot(userTopEvents);
						userScoreSnapshotRepository.save(userScoreSnapshot);
					}
				}
			}
		} catch (Exception e) {
			logger.error("error exporting state snapshot for user scores", e);
		} finally {
			if (iterator!=null)
				iterator.close();
		}
	}

	public void exportSnapshot(){
		exportSnapshotForDataSource(null);
	}
	
	public void updateDb(String dataSource,long lastUpdateEpochTime) {
		// When no events were received yet and the
		// current time is taken out of the latest event
		if (lastUpdateEpochTime == 0)
			return;
		
		Map<String, Double> map = new HashMap<String, Double>();
		double avgScore = updateLevelDb(dataSource, lastUpdateEpochTime, map);
		updateMongoDb(dataSource, lastUpdateEpochTime, avgScore, map);
	}
	
	private double updateLevelDb(String dataSource, long lastUpdateEpochTime, Map<String, Double> userScores) {
		KeyValueIterator<UserEventTypePair, UserTopEvents> iterator = store.all();

		double avgScore = 0;

		// Iterate the users' top events to calculate the average score
		while (iterator.hasNext()) {
			Entry<UserEventTypePair, UserTopEvents> entry = iterator.next();
			UserEventTypePair key = entry.getKey();
			UserTopEvents userTopEvents = entry.getValue();
			
			// Model might be null in case of a serialization error, in that case we
			// don't want to fail here and the error is logged in the SerDe implementation
			if (key != null && dataSource.equals(key.getEventType())) {
				// go over all data sources and calculate user score and update store
				double currentScore = userTopEvents.calculateUserScore(lastUpdateEpochTime);
				userTopEvents.setLastUpdatedScore(currentScore);
				userTopEvents.setLastUpdateScoreEpochTime(lastUpdateEpochTime);

				// Update average and add entry to map
				avgScore += currentScore;
				userScores.put(key.getUsername(), currentScore);

				store.put(key, userTopEvents);
			}
		}
		iterator.close();
		
		if (userScores.size() > 1)
			avgScore /= userScores.size();
		return avgScore;
	}
	
	private void updateMongoDb(String dataSource, long lastUpdateEpochTime, double avgScore, Map<String, Double> map) {
		Iterator<String> iterator = map.keySet().iterator();
		int numOfMissingUsers = 0;
		
		try {
			while (iterator.hasNext()) {
				Set<String> subset = new HashSet<String>();
				// Get next userScoreStreamingServicePageSize users and store in subset
				do {
					subset.add(iterator.next());
				} while (iterator.hasNext() && subset.size() < userScoreStreamingServicePageSize);
				
				// Get these users from mongoDb and iterate them
				List<User> users = userRepository.findByUsernamesExcludeAdInfo(subset);
				for (User user : users) {
					String username = user.getUsername();
					// Update scores of user
					try {
						updateUserScore(user, lastUpdateEpochTime, dataSource, map.get(username));
						updateUserAvgScore(user, dataSource, avgScore);
						Update update = new Update();
						update.set(User.getClassifierScoreField(dataSource), user.getScore(dataSource));
						mongoTemplate.updateFirst(query(where(User.usernameField).is(username)), update, User.class);
					} catch (Exception e) {
						logger.error(String.format("Exception while trying to update the scores of user %s", username), e);
					}
				}
				
				// Update number of users that do not exist in mongoDb
				numOfMissingUsers += subset.size() - users.size();
			}
			
			if (numOfMissingUsers > 0)
				logger.warn("Received events of {} users that do not exist in mongoDb", numOfMissingUsers);
		} catch (Exception e) {
			logger.error("Exception while trying to update the users' scores in mongoDb", e);
			Throwables.propagateIfInstanceOf(e, org.springframework.dao.DataAccessResourceFailureException.class);
		}
	}
	
	private void updateUserAvgScore(User user, String classifierId, double avgScore){
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

	public void setUseLatestEventTimeAsCurrentTime(boolean isUseLatestEventTimeAsCurrentTime) {
		this.isUseLatestEventTimeAsCurrentTime = isUseLatestEventTimeAsCurrentTime;
	}
}
