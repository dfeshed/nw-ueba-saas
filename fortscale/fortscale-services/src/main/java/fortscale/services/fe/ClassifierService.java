package fortscale.services.fe;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;

import fortscale.domain.events.LogEventsEnum;
import fortscale.domain.fe.EventScore;
import fortscale.domain.fe.dao.EventLoginDayCount;


public interface ClassifierService {
	public List<IClassifierScoreDistribution> getScoreDistribution(); 
	public Classifier getClassifier(String classifierId);
	public List<IScoreDistribution> getScoreDistribution(String classifierId); 
	public int countUsers(String classifierId);
	public Page<ISuspiciousUserInfo> getSuspiciousUsersByScore(String classifierId, String severityId, int page, int size, boolean followedOnly);
	public Page<ISuspiciousUserInfo> getSuspiciousUsersByTrend(String classifierId, String severityId, int page, int size, boolean followedOnly);
	public Page<ISuspiciousUserInfo> getSuspiciousUsersByScore(String classifierId, int page, int size, Integer minScore, Integer maxScore, boolean followedOnly);
	public Page<ISuspiciousUserInfo> getSuspiciousUsersByTrend(String classifierId, int page, int size, Integer minScore, Integer maxScore, boolean followedOnly);
	public List<ILoginEventScoreInfo> getUserSuspiciousAuthEvents(LogEventsEnum eventId, String userId, int offset, int limit, String orderBy, Direction direction, int minScore);
	public List<ILoginEventScoreInfo> getSuspiciousAuthEvents(LogEventsEnum eventId, int offset, int limit, String orderBy, Direction direction, Integer minScore, boolean onlyFollowedUsers);
	public List<EventLoginDayCount> getEventLoginDayCount(LogEventsEnum eventId, String username, int numberOfDays);
	public int countAuthEvents(LogEventsEnum eventId, String userId, int minScore);
	public int countAuthEvents(LogEventsEnum eventId, int minScore, boolean onlyFollowedUsers);
	public int countAuthEvents(LogEventsEnum eventId, String userId);
	public int countAuthEvents(LogEventsEnum eventId);
	public List<IVpnEventScoreInfo> getUserSuspiciousVpnEvents(String userId, int offset, int limit, String orderBy, Direction direction, int minScore);
	public List<IVpnEventScoreInfo> getSuspiciousVpnEvents(int offset, int limit, String orderBy, Direction direction, Integer minScore, boolean onlyFollowedUsers);
	
	public void addFilter(String collectionName, String fieldName, String regex);
	public String getFilterRegex(String collectionName, String fieldName);
	
	public List<EventScore> getEventScores(List<LogEventsEnum> classifierId, String username, int daysBack, int limit);
}
