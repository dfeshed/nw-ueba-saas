package fortscale.domain.fe.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;

import fortscale.domain.fe.AuthScore;

public interface AuthDAO extends EventScoreDAO{
	
	public List<AuthScore> findAll(Pageable pageable);
	
	public AuthScore findCurrentByNormalizedUsername(String username);
	
	public List<AuthScore> findEventsByNormalizedUsername(String username, Pageable pageable);
	
	public List<AuthScore> findEventsByNormalizedUsernameAndTimestamp(String username, Date timestamp, Pageable pageable);
	
	public List<AuthScore> findEventsByNormalizedUsernameAndTimestampGtEventScore(String username, Date timestamp, int minScore, Pageable pageable);
	
	public List<AuthScore> findEventsByTimestamp(Date timestamp, Pageable pageable);
	
	public List<AuthScore> findEventsByTimestampGtEventScore(Date timestamp, Pageable pageable, int minScore);
	
	public List<AuthScore> findEventsByTimestampGtEventScoreInUsernameList(Date timestamp, Pageable pageable, Integer minScore, Collection<String> usernames);
	
	public List<AuthScore> findEventsByTimestamp(Date timestamp, Pageable pageable, String additionalWhereQuery);
	
	public List<AuthScore> findGlobalScoreByNormalizedUsername(String username, int limit);
	
	public List<AuthScore> findGlobalScoreByTimestamp(Date timestamp);
	
	public List<AuthScore> findUsernamesByTimestamp(Date timestamp);
		
	public List<AuthScore> getTopUsersAboveThreshold(Threshold threshold, Date timestamp, int limit);
	
	public List<AuthScore> findByTimestampAndGlobalScoreBetweenSortByEventScore(Date timestamp, int lowestVal, int upperVal, int limit);
	
	public List<AuthScore> getTopEventsAboveThreshold(Threshold threshold, Date timestamp, int limit);	
}
