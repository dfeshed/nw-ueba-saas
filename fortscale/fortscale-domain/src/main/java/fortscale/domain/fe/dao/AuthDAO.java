package fortscale.domain.fe.dao;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Pageable;

import fortscale.domain.fe.AuthScore;

public interface AuthDAO extends EventScoreDAO{
	
	public List<AuthScore> findAll(Pageable pageable);
		
	public List<AuthScore> findEventsByNormalizedUsername(String username, Pageable pageable);
		
	public List<AuthScore> findEventsByNormalizedUsernameAndGtEventScore(String username, int minScore, Pageable pageable);
		
	public List<AuthScore> findEventsByGtEventScore(Pageable pageable, int minScore);
	
	public List<AuthScore> findEventsByGtEventScoreInUsernameList(Pageable pageable, Integer minScore, Collection<String> usernames);
	
	public List<AuthScore> findEvents(Pageable pageable, String additionalWhereQuery);
		
	public List<AuthScore> findUsernames();
		
	public List<AuthScore> getTopEventsAboveThreshold(Threshold threshold, int limit);	
	
	
	
	public String getEventScoreFieldName();
}
