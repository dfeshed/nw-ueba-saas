package fortscale.services;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort.Direction;

import fortscale.domain.core.User;
import fortscale.domain.fe.IFeature;

public interface UserScoreService {
	
	public List<IUserScore> getUserScores(String uid); 
	
	public List<IUserScore> getUserScoresByDay(String uid, Long dayTimestamp); 

	public List<IUserScoreHistoryElement> getUserScoresHistory(String uid, String classifierId, long fromEpochTime, long toEpochTime, int tzShift);

	public Map<User, List<IUserScore>> getUsersScoresByIds(List<String> uids);
	
	public Map<User, List<IUserScore>> getFollowedUsersScores();

	public boolean isOnSameDay(Date date1, Date date2);
}
