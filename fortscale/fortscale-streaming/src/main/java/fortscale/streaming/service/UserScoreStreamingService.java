package fortscale.streaming.service;

import org.apache.samza.storage.kv.Entry;
import org.apache.samza.storage.kv.KeyValueIterator;
import org.apache.samza.storage.kv.KeyValueStore;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fortscale.domain.core.ClassifierScore;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.streaming.user.UserScoreSnapshot;
import fortscale.domain.streaming.user.dao.UserScoreSnapshotRepository;

@Service
public class UserScoreStreamingService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserScoreSnapshotRepository userScoreSnapshotRepository;
	
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
			User user = userRepository.findByUsername(username);
			if(user != null){
				ClassifierScore cScore = user.getScore(classifierId);
				double prevScore = 0;
				if(cScore.getPrevScores().size() >=2){
					prevScore = cScore.getPrevScores().get(1).getScore();
				}
				
				double trendScore = curScore - prevScore;
				userRepository.updateCurrentUserScore(user, classifierId, curScore, trendScore, lastUpdateTime);
				userTopEvents.setLastUpdatedScore(curScore);
				userTopEvents.setLastUpdateScoreEpochTime(lastUpdateTime.getMillis());
				hasToUpdateStore = true;
			}
		}
		
		if(hasToUpdateStore){
			userTopEvents.setLastUpdateEpochTime(lastUpdateTime.getMillis());
			store.put(username, userTopEvents);
		}
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
				String username = entry.getKey();
				UserTopEvents userTopEvents = entry.getValue();
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
		} finally {
			if (iterator!=null)
				iterator.close();
		}
	}
}
