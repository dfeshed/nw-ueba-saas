package fortscale.services.fe;

import java.util.List;


public interface ClassifierService {
	public List<IClassifierScoreDistribution> getScoreDistribution(); 
	public Classifier getClassifier(String classifierId);
	public List<IScoreDistribution> getScoreDistribution(String classifierId); 
	public List<ISuspiciousUserInfo> getSuspiciousUsers(String classifierId, String severityId);
}
