package fortscale.domain.fe.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;

import fortscale.domain.fe.VpnScore;

public interface VpnDAO {
	public List<VpnScore> findAll(Pageable pageable);
	
	public VpnScore findCurrentByUsername(String username);
	
	public List<VpnScore> findEventsByUsername(String username, Pageable pageable);
	
	public List<VpnScore> findEventsByUsernameAndTimestamp(String username, Date timestamp, Pageable pageable);
	
	public List<VpnScore> findEventsByTimestamp(Date timestamp, Pageable pageable);
	
	public List<VpnScore> findEventsByTimestamp(Date timestamp, Pageable pageable, String additionalWhereQuery);
	
	public List<VpnScore> findGlobalScoreByUsername(String username, int limit);
	
	public List<VpnScore> findGlobalScoreByTimestamp(Date timestamp);
	
	public int countNumOfUsersAboveThreshold(Threshold threshold, Date timestamp);
	
	public Date getLastRunDate();
	
	public Long getLastRuntime();
	
	public double calculateAvgScoreOfGlobalScore(Date timestamp);
	
	public List<VpnScore> getTopUsersAboveThreshold(Threshold threshold, Date timestamp, int limit);
	
	public List<VpnScore> findByTimestampAndGlobalScoreBetweenSortByEventScore(Date timestamp, int lowestVal, int upperVal, int limit);
	
	public List<VpnScore> getTopEventsAboveThreshold(Threshold threshold, Date timestamp, int limit);
	
	public List<Long> getDistinctRuntime();
}
