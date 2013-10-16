package fortscale.services.fe;

import java.util.Date;
import java.util.List;
import java.util.Map;

import fortscale.services.impl.SeverityElement;


public interface ClassifierService {
	public List<IClassifierScoreDistribution> getScoreDistribution(); 
	public Classifier getClassifier(String classifierId);
	public List<IScoreDistribution> getScoreDistribution(String classifierId); 
	public List<ISuspiciousUserInfo> getSuspiciousUsers(String classifierId, String severityId);
	public List<ILoginEventScoreInfo> getUserSuspiciousLoginEvents(String userId, Date timestamp, int offset, int limit);
	public List<ILoginEventScoreInfo> getSuspiciousLoginEvents(Date timestamp, int offset, int limit);
	public List<SeverityElement> getSeverityElements();
	public List<IVpnEventScoreInfo> getUserSuspiciousVpnEvents(String userId, Date timestamp, int offset, int limit);
	public List<IVpnEventScoreInfo> getSuspiciousVpnEvents(Date timestamp, int offset, int limit);
	
	public List<Map<String, String>> getEBSAlgOnQuery(String query);
}
