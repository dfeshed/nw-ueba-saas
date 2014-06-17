package fortscale.streaming.service;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

	public void setStore(KeyValueStore<String, UserTopEvents> store) {
		this.store = store;
	}
	
	public void updateUserWithEventScore(String username, double score, long eventTimeInMillis){
		DateTime lastUpdateTime = new DateTime();
		
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
			if(updateDb(username, userTopEvents, lastUpdateTime, curScore)){
				hasToUpdateStore = true;
			}
		}
		
		if(hasToUpdateStore){
			userTopEvents.setLastUpdateEpochTime(lastUpdateTime.getMillis());
			store.put(username, userTopEvents);
		}
	}
	
	private boolean updateDb(String username, UserTopEvents userTopEvents, DateTime lastUpdateTime, double curScore){
		boolean ret = false;
		User user = userRepository.findByUsername(username);
		if(user != null){
			ClassifierScore cScore = user.getScore(classifierId);
			double prevScore = 0;
			if(cScore != null && cScore.getPrevScores().size() >=2){
				prevScore = cScore.getPrevScores().get(1).getScore();
			}
			
			double trendScore = curScore - prevScore;
			userRepository.updateCurrentUserScore(user, classifierId, curScore, trendScore, lastUpdateTime);
			userTopEvents.setLastUpdatedScore(curScore);
			userTopEvents.setLastUpdateScoreEpochTime(lastUpdateTime.getMillis());
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
		// go over all users top events in the store and persist them to mongodb
		DateTime lastUpdateTime = new DateTime();
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
						updateUserScore(user, lastUpdateTime.toDate(), classifierId, curScore);
						avgScore += user.getScore(classifierId).getScore();
						users.add(user);
						
						userTopEvents.setLastUpdatedScore(curScore);
						userTopEvents.setLastUpdateScoreEpochTime(lastUpdateTime.getMillis());
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
	
	private void updateUserScore(User user, Date timestamp, String classifierId, double value){
		ClassifierScore cScore = user.getScore(classifierId);
		
		
		boolean isReplaceCurrentScore = true;
		double trend = 0.0; 
		double diffScore = 0.0;
		if(cScore == null){
			cScore = new ClassifierScore();
			cScore.setClassifierId(classifierId);
			ScoreInfo scoreInfo = new ScoreInfo();
			scoreInfo.setScore(value);
			scoreInfo.setTimestamp(timestamp);
			scoreInfo.setTimestampEpoc(timestamp.getTime());
			List<ScoreInfo> prevScores = new ArrayList<ScoreInfo>();
			prevScores.add(scoreInfo);
			cScore.setPrevScores(prevScores);
		}else{
			boolean isOnSameDay = isOnSameDay(timestamp, cScore.getPrevScores().get(0).getTimestamp());
			if(cScore.getPrevScores().size() > 1){
				double prevScore = cScore.getPrevScores().get(1).getScore() + 0.00001;
				double curScore = value + 0.00001;
				diffScore = curScore - prevScore;
				trend = diffScore / prevScore;
			}
			
			ScoreInfo scoreInfo = new ScoreInfo();
			scoreInfo.setScore(value);
			scoreInfo.setTimestamp(timestamp);
			scoreInfo.setTimestampEpoc(timestamp.getTime());
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
			cScore.setTimestamp(timestamp);
			cScore.setTimestampEpoc(timestamp.getTime());
			cScore.setTrend(trend);
			cScore.setTrendScore(Math.abs(diffScore));
		}
		user.putClassifierScore(cScore);
	}
	
	private boolean isOnSameDay(Date date1, Date date2){
		DateTime dateTime1 = new DateTime(date1.getTime());
		dateTime1 = dateTime1.withTimeAtStartOfDay();
		DateTime dateTime2 = new DateTime(date2.getTime());
		dateTime2 = dateTime2.withTimeAtStartOfDay();
		if(dateTime1.equals(dateTime2)){
			return true;
		}
		
		return false;
	}
}
