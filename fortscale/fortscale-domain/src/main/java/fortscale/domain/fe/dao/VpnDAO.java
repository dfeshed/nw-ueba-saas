package fortscale.domain.fe.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;

import fortscale.domain.fe.VpnScore;

public interface VpnDAO {
	
	public String getTableName();
	
	public List<VpnScore> findAll(Pageable pageable);
	
	public int countNumOfRecords();
	
	public int countNumOfEventsByNormalizedUsernameAndStatusRegex(Date timestamp, String username, String statusVal);
	
	public VpnScore findCurrentByNormalizedUsername(String username);
	
	public List<VpnScore> findEventsByNormalizedUsername(String username, Pageable pageable);
	
	public List<VpnScore> findEventsByNormalizedUsernameAndTimestamp(String username, Date timestamp, Pageable pageable);
	
	public List<VpnScore> findEventsByNormalizedUsernameAndTimestampGtEventScore(String username, Date timestamp, int minScore, Pageable pageable);
	
	public List<VpnScore> findEventsByTimestamp(Date timestamp, Pageable pageable);
	
	public List<VpnScore> findEventsByTimestampGtEventScore(Date timestamp, Pageable pageable, int minScore);
	
	public List<VpnScore> findEventsByTimestampGtEventScoreInUsernameList(Date timestamp, Pageable pageable, Integer minScore, Collection<String> usernames);
	
	public List<VpnScore> findEventsByTimestamp(Date timestamp, Pageable pageable, String additionalWhereQuery);
	
	public List<VpnScore> findGlobalScoreByNormalizedUsername(String username, int limit);
	
	public List<VpnScore> findGlobalScoreByTimestamp(Date timestamp);
	
	public List<VpnScore> findUsernamesByTimestamp(Date timestamp);
	
	public int countNumOfUsersAboveThreshold(Threshold threshold, Date timestamp);
	
	public int countNumOfUsers(Date timestamp);
	
	public int countNumOfEvents(Date timestamp);
	
	public int countNumOfEventsByNormalizedUsername(Date timestamp, String username);
	
	public Date getLastRunDate();
	
	public Long getLastRuntime();
	
	public double calculateAvgScoreOfGlobalScore(Date timestamp);
	
	public List<VpnScore> getTopUsersAboveThreshold(Threshold threshold, Date timestamp, int limit);
	
	public List<VpnScore> findByTimestampAndGlobalScoreBetweenSortByEventScore(Date timestamp, int lowestVal, int upperVal, int limit);
	
	public List<VpnScore> getTopEventsAboveThreshold(Threshold threshold, Date timestamp, int limit);
	
	public List<Long> getDistinctRuntime();
	
	
	
	
	public String getTimestampFieldName();
	
	public String getNormalizedUsernameField();

	public String getUsernameFieldName();
	
	public String getStatusFieldName();
	
	public String getCountryFieldName();
	
	public String getRegionFieldName();
	
	public String getCityFieldName();
	
	public String getIspFieldName();
		
	public String getIpusageFieldName();
	
	public String getSourceIpFieldName();
	
	public String getLocalIpFieldName();
	
	public String getEventTimeFieldName();
	
	public String getEventTimeScoreFieldName();

	public String getEventScoreFieldName();

	public String getGlobalScoreFieldName();
}
