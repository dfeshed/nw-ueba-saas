package fortscale.domain.fe.dao;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Pageable;

import fortscale.domain.fe.VpnScore;

public interface VpnDAO extends EventScoreDAO{
	
	public List<VpnScore> findAll(Pageable pageable);
	
	public List<VpnScore> findEventsByNormalizedUsername(String username, Pageable pageable);
		
	public List<VpnScore> findEventsByNormalizedUsernameAndGtEventScore(String username, int minScore, Pageable pageable);
		
	public List<VpnScore> findEventsByGtEventScore(Pageable pageable, int minScore);
	
	public List<VpnScore> findEventsByGtEventScoreInUsernameList(Pageable pageable, Integer minScore, Collection<String> usernames);
	
	public List<VpnScore> findEvents(Pageable pageable, String additionalWhereQuery);
		
	public List<VpnScore> findUsernames();
		
	public List<VpnScore> getTopEventsAboveThreshold(Threshold threshold, int limit);
	
	
		
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
}
