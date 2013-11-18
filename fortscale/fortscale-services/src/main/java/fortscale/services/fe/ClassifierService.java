package fortscale.services.fe;

import java.util.Date;
import java.util.List;
import java.util.Map;


public interface ClassifierService {
	public List<IClassifierScoreDistribution> getScoreDistribution(); 
	public Classifier getClassifier(String classifierId);
	public List<IScoreDistribution> getScoreDistribution(String classifierId); 
	public int countUsers(String classifierId);
	public List<ISuspiciousUserInfo> getSuspiciousUsersByScore(String classifierId, String severityId, int page, int size);
	public List<ISuspiciousUserInfo> getSuspiciousUsersByTrend(String classifierId, String severityId, int page, int size);
	public List<ILoginEventScoreInfo> getUserSuspiciousLoginEvents(String userId, Date timestamp, int offset, int limit);
	public List<ILoginEventScoreInfo> getSuspiciousLoginEvents(Date timestamp, int offset, int limit);
	public int countLoginEvents(Date timestamp);
	public int countLoginEvents(String userId, Date timestamp);
	public List<IVpnEventScoreInfo> getUserSuspiciousVpnEvents(String userId, Date timestamp, int offset, int limit);
	public List<IVpnEventScoreInfo> getSuspiciousVpnEvents(Date timestamp, int offset, int limit);
	public int countVpnEvents(Date timestampt);
	public int countVpnEvents(String userId, Date timestamp);
	
	public EBSResult getEBSAlgOnAuthQuery(List<Map<String, Object>> resultsMap, int offset, int limit);
	public EBSResult getEBSAlgOnQuery(String query, int offset, int limit);
	public Long getLatestRuntime(String tableName);
	public void addFilter(String collectionName, String fieldName, String regex);
	public String getFilterRegex(String collectionName, String fieldName);
	public EBSResult getSimpleEBSAlgOnVpnDataQuery(List<Map<String, Object>> resultsMap, String timeFieldName, int offset, int limit);
}
