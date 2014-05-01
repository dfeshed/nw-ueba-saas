package fortscale.domain.fe.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import fortscale.domain.fe.EventScore;

public interface EventScoreDAO {
	public String getTableName();
	
	public int countNumOfRecords();
	
	public int countNumOfEventsByNormalizedUsernameAndStatusRegex(Date timestamp, String username, String statusVal);
	
	public int countNumOfEventsByNormalizedUsernameAndGtEScore(Date timestamp, String username, int minScore);
	
	public int countNumOfEventsByGTEScoreAndNormalizedUsernameList(Date timestamp, int minScore, Collection<String> usernames);
	
	public int countNumOfUsersAboveThreshold(Threshold threshold, Date timestamp);
	
	public int countNumOfUsers(Date timestamp);
	
	public int countNumOfEvents(Date timestamp);
	
	public int countNumOfEventsByNormalizedUsername(Date timestamp, String username);
	
	public Date getLastRunDate();
	
	public Long getLastRuntime();
	
	public double calculateAvgScoreOfGlobalScore(Date timestamp);
	
	public List<Long> getDistinctRuntime();
	
	public List<EventLoginDayCount> getEventLoginDayCount(String username, int numberOfDays);
	
	public List<EventScore> getEventScores(String username, int daysBack, int limit);
}
