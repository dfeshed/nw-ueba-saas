package fortscale.domain.fe.dao;

import fortscale.domain.fe.EventScore;
import fortscale.utils.hdfs.partition.PartitionStrategy;

import java.util.Collection;
import java.util.List;

public interface EventScoreDAO {
	public String getTableName();
	
	PartitionStrategy getPartitionStrategy();
	
	public int countNumOfRecords();
	
	public int countNumOfEventsByNormalizedUsernameAndStatusRegex(String username, String statusVal);
	
	public int countNumOfEventsByNormalizedUsernameAndGtEScoreAndBetweenTimes(String username, int minScore, Long latestDate, Long earliestDate);
	
	public int countNumOfEventsByGTEScoreAndBetweenTimesAndNormalizedUsernameList(int minScore, Long latestDate, Long earliestDate, Collection<String> usernames);
		
	public int countNumOfUsers();
	
	public int countNumOfEvents();
	
	public int countNumOfEventsByNormalizedUsername(String username);
		
	public List<EventLoginDayCount> getEventLoginDayCount(String username, int numberOfDays);
	
	public List<EventScore> getEventScores(String username, int daysBack, int limit);
	
	public List<EventsToMachineCount> getEventsToTargetMachineCount(String username, Long latestDate, Long earliestDate, int minScore);
	
	public String getNormalizedUsernameField();

	public String getUsernameFieldName();
	
	public String getEventTimeFieldName();
	
	public String getEventEpochTimeFieldName();
	
	public String getEventTimeScoreFieldName();

	public String getEventScoreFieldName();
	
	public String getSourceFieldName();
	
	public String getSourceIpFieldName();
}
