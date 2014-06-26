package fortscale.domain.fe.dao;

import java.util.Collection;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.data.domain.Pageable;

import fortscale.domain.fe.AuthScore;

public interface AuthDAO extends EventScoreDAO{
	
	public List<AuthScore> findAll(Pageable pageable);
		
	public List<AuthScore> findEventsByNormalizedUsername(String username, Pageable pageable);
	
	public List<AuthScore> findTopEventsByNormalizedUsername(String username, int limit, DateTime oldestEventTime, String decayScoreFieldName);
		
	public List<AuthScore> findEventsByNormalizedUsernameAndGtEventScoreAndBetweenTimes(String username, int minScore, Long latestDate, Long earliestDate, Pageable pageable);
		
	public List<AuthScore> findEventsByGtEventScore(Pageable pageable, int minScore);
	
	public List<AuthScore> findEventsByGtEventScoreBetweenTimeInUsernameList(Pageable pageable, Integer minScore, Long latestDate, Long earliestDate, Collection<String> usernames);
	
	public List<AuthScore> findEvents(Pageable pageable, String additionalWhereQuery);
		
	public List<AuthScore> findUsernames();
		
	public List<AuthScore> getTopEventsAboveThreshold(Threshold threshold, int limit);		
}
