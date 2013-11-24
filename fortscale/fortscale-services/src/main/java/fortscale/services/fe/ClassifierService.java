package fortscale.services.fe;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Sort.Direction;


public interface ClassifierService {
	public List<IClassifierScoreDistribution> getScoreDistribution(); 
	public Classifier getClassifier(String classifierId);
	public List<IScoreDistribution> getScoreDistribution(String classifierId); 
	public int countUsers(String classifierId);
	public List<ISuspiciousUserInfo> getSuspiciousUsersByScore(String classifierId, String severityId, int page, int size);
	public List<ISuspiciousUserInfo> getSuspiciousUsersByTrend(String classifierId, String severityId, int page, int size);
	public List<ILoginEventScoreInfo> getUserSuspiciousLoginEvents(String userId, Date timestamp, int offset, int limit, String orderBy, Direction direction, int minScore);
	public List<ILoginEventScoreInfo> getSuspiciousLoginEvents(Date timestamp, int offset, int limit, String orderBy, Direction direction, int minScore);
	public int countLoginEvents(Date timestamp);
	public int countLoginEvents(String userId, Date timestamp);
	public List<IVpnEventScoreInfo> getUserSuspiciousVpnEvents(String userId, Date timestamp, int offset, int limit, String orderBy, Direction direction, int minScore);
	public List<IVpnEventScoreInfo> getSuspiciousVpnEvents(Date timestamp, int offset, int limit, String orderBy, Direction direction, int minScore);
	public int countVpnEvents(Date timestampt);
	public int countVpnEvents(String userId, Date timestamp);
	
	public EBSResult getEBSAlgOnQuery(String query, int offset, int limit, String orderBy, String orderByDirection, Integer minScore);
	public Long getLatestRuntime(String tableName);
	public void addFilter(String collectionName, String fieldName, String regex);
	public String getFilterRegex(String collectionName, String fieldName);
}
