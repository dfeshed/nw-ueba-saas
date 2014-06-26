package fortscale.domain.fe.dao;

import java.util.Collection;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.data.domain.Pageable;

import fortscale.domain.fe.VpnScore;

public interface VpnDAO extends EventScoreDAO{
	
	public List<VpnScore> findAll(Pageable pageable);
	
	public List<VpnScore> findTopEventsByNormalizedUsername(String username, int limit, DateTime oldestEventTime, String decayScoreFieldName);
	
	public List<VpnScore> findEventsByNormalizedUsername(String username, Pageable pageable);
		
	public List<VpnScore> findEventsByNormalizedUsernameAndGtEventScoreAndBetweenTimes(String username, int minScore, Long latestDate, Long earliestDate, Pageable pageable);
		
	public List<VpnScore> findEventsByGtEventScore(Pageable pageable, int minScore);
	
	public List<VpnScore> findEventsByGtEventScoreBetweenTimeInUsernameList(Pageable pageable, Integer minScore, Long latestDate, Long earliestDate, Collection<String> usernames);
	
	public List<VpnScore> findEvents(Pageable pageable, String additionalWhereQuery);
		
	public List<VpnScore> findUsernames();
		
	public List<VpnScore> getTopEventsAboveThreshold(Threshold threshold, int limit);
	
	
		
	
	
	public String getStatusFieldName();
	
	public String getCountryFieldName();
	
	public String getRegionFieldName();
	
	public String getCityFieldName();
	
	public String getIspFieldName();
		
	public String getIpusageFieldName();
	
	public String getSourceIpFieldName();
	
	public String getLocalIpFieldName();
	
	
}
