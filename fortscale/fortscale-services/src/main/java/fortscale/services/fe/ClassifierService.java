package fortscale.services.fe;

import java.util.Date;
import java.util.List;

import fortscale.services.impl.SeverityElement;


public interface ClassifierService {
	public List<IClassifierScoreDistribution> getScoreDistribution(); 
	public Classifier getClassifier(String classifierId);
	public List<IScoreDistribution> getScoreDistribution(String classifierId); 
	public List<ISuspiciousUserInfo> getSuspiciousUsersByScore(String classifierId, String severityId);
	public List<ISuspiciousUserInfo> getSuspiciousUsersByTrend(String classifierId, String severityId);
	public List<ILoginEventScoreInfo> getUserSuspiciousLoginEvents(String userId, Date timestamp, int offset, int limit);
	public List<ILoginEventScoreInfo> getSuspiciousLoginEvents(Date timestamp, int offset, int limit);
	public List<SeverityElement> getSeverityElements();
	public List<IVpnEventScoreInfo> getUserSuspiciousVpnEvents(String userId, Date timestamp, int offset, int limit);
	public List<IVpnEventScoreInfo> getSuspiciousVpnEvents(Date timestamp, int offset, int limit);
	
	public EBSResult getEBSAlgOnAuthQuery(String query, int offset, int limit);
	public EBSResult getEBSAlgOnQuery(String query, int offset, int limit);
	public Long getLatestRuntime(String tableName);
}
