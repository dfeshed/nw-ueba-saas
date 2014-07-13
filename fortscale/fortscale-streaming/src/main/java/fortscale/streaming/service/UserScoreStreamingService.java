package fortscale.streaming.service;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fortscale.streaming.model.UserTopEvents;
import org.apache.samza.storage.kv.Entry;
import org.apache.samza.storage.kv.KeyValueIterator;
import org.apache.samza.storage.kv.KeyValueStore;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import fortscale.domain.core.ClassifierScore;
import fortscale.domain.core.ScoreInfo;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.streaming.user.UserScoreSnapshot;
import fortscale.domain.streaming.user.dao.UserScoreSnapshotRepository;

@Service
public class UserScoreStreamingService {
	
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
				updateDb();
				exportSnapshot();
			}
			latestEventTimeInMillis = eventTimeInMillis;
		}
	}
	
	public void updateUserWithEventScore(String username, double score, long eventTimeInMillis){
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
		
		double curScore = userTopEvents.calculateUserScore();
		
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
			store.put(username, userTopEvents);
		}
	}
	
	private boolean updateDb(String username, UserTopEvents userTopEvents, long lastUpdateTime, double curScore){
		boolean ret = false;
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
		} finally {
			if (iterator!=null)
				iterator.close();
		}
	}
	
	public void updateDb(){
		
		long lastUpdateEpochTime = getCurrentEpochTimeInMillis();
		if(lastUpdateEpochTime == 0){
			return; //This happens when no event was recieved yet and the current time is taken out of the latest event.
		}
		
		// go over all users top events in the store and persist them to mongodb
		KeyValueIterator<String, UserTopEvents> iterator = store.all();
		List<User> users = new ArrayList<>();
		double avgScore = 0;
		try { 
			while (iterator.hasNext()) {
				Entry<String, UserTopEvents> entry = iterator.next();
				UserTopEvents userTopEvents = entry.getValue();
				if(userTopEvents != null){
					// model might be null in case of a serialization error, in that case
					// we don't want to fail here and the error is logged in the serde implementation
					String username = entry.getKey();
					double curScore = userTopEvents.calculateUserScore();
					
					User user = userRepository.findByUsername(username);
					if(user != null){
						updateUserScore(user, lastUpdateEpochTime, classifierId, curScore);
						avgScore += user.getScore(classifierId).getScore();
						users.add(user);
						
						userTopEvents.setLastUpdatedScore(curScore);
						userTopEvents.setLastUpdateScoreEpochTime(lastUpdateEpochTime);
						store.put(username, userTopEvents);
					}
				}
			}
			if(users.size() > 1){
				avgScore = avgScore / users.size();
			}
			
			
			for(User user: users){
				Update update = new Update();
				updateUserAvgScore(user, avgScore);
				update.set(User.getClassifierScoreField(classifierId), user.getScore(classifierId));
				mongoTemplate.updateFirst(query(where(User.ID_FIELD).is(user.getId())), update, User.class);
			}
		} finally {
			if (iterator!=null)
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
