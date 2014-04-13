package fortscale.services.fe;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Sort.Direction;

import fortscale.domain.core.Data;
import fortscale.services.LogEventsEnum;


public interface ClassifierService {
	public List<IClassifierScoreDistribution> getScoreDistribution(); 
	public Classifier getClassifier(String classifierId);
	public List<IScoreDistribution> getScoreDistribution(String classifierId); 
	public int countUsers(String classifierId);
	public Data<List<ISuspiciousUserInfo>> getSuspiciousUsersByScore(String classifierId, String severityId, int page, int size, boolean followedOnly);
	public Data<List<ISuspiciousUserInfo>> getSuspiciousUsersByTrend(String classifierId, String severityId, int page, int size, boolean followedOnly);
	public List<ILoginEventScoreInfo> getUserSuspiciousAuthEvents(LogEventsEnum eventId, String userId, Date timestamp, int offset, int limit, String orderBy, Direction direction, int minScore);
	public List<ILoginEventScoreInfo> getSuspiciousAuthEvents(LogEventsEnum eventId, Date timestamp, int offset, int limit, String orderBy, Direction direction, Integer minScore, boolean onlyFollowedUsers);
	public int countAuthEvents(LogEventsEnum eventId, Date timestamp, String userId, int minScore);
	public int countAuthEvents(LogEventsEnum eventId, Date timestamp, int minScore, boolean onlyFollowedUsers);
	public int countAuthEvents(LogEventsEnum eventId, String userId, Date timestamp);
	public int countAuthEvents(LogEventsEnum eventId, Date timestamp);
	public List<IVpnEventScoreInfo> getUserSuspiciousVpnEvents(String userId, Date timestamp, int offset, int limit, String orderBy, Direction direction, int minScore);
	public List<IVpnEventScoreInfo> getSuspiciousVpnEvents(Date timestamp, int offset, int limit, String orderBy, Direction direction, Integer minScore, boolean onlyFollowedUsers);
	
	public EBSResult getEBSAlgOnQuery(String query, int offset, int limit, String orderBy, String orderByDirection, Integer minScore);
	public Long getLatestRuntime(String tableName);
	public void addFilter(String collectionName, String fieldName, String regex);
	public String getFilterRegex(String collectionName, String fieldName);
}
