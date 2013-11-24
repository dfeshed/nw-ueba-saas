package fortscale.domain.fe.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;

import fortscale.domain.fe.AuthScore;

public interface AuthDAO {

	public List<AuthScore> findAll(Pageable pageable);
	
	public AuthScore findCurrentByUsername(String username);
	
	public List<AuthScore> findEventsByUsername(String username, Pageable pageable);
	
	public List<AuthScore> findEventsByUsernameAndTimestamp(String username, Date timestamp, Pageable pageable);
	
	public List<AuthScore> findEventsByUsernameAndTimestampGtEventScore(String username, Date timestamp, int minScore, Pageable pageable);
	
	public List<AuthScore> findEventsByTimestamp(Date timestamp, Pageable pageable);
	
	public List<AuthScore> findEventsByTimestampGtEventScore(Date timestamp, Pageable pageable, int minScore);
	
	public List<AuthScore> findEventsByTimestamp(Date timestamp, Pageable pageable, String additionalWhereQuery);
	
	public List<AuthScore> findGlobalScoreByUsername(String username, int limit);
	
	public List<AuthScore> findGlobalScoreByTimestamp(Date timestamp);
	
	public int countNumOfUsersAboveThreshold(Threshold threshold, Date timestamp);
	
	public int countNumOfUsers(Date timestamp);
	
	public int countNumOfEvents(Date timestamp);
	
	public int countNumOfEventsByUser(Date timestamp, String username);
	
	public Date getLastRunDate();
	
	public Long getLastRuntime();
	
	public double calculateAvgScoreOfGlobalScore(Date timestamp);
	
	public List<AuthScore> getTopUsersAboveThreshold(Threshold threshold, Date timestamp, int limit);
	
	public List<AuthScore> findByTimestampAndGlobalScoreBetweenSortByEventScore(Date timestamp, int lowestVal, int upperVal, int limit);
	
	public List<AuthScore> getTopEventsAboveThreshold(Threshold threshold, Date timestamp, int limit);
	
	public List<Long> getDistinctRuntime();
}
