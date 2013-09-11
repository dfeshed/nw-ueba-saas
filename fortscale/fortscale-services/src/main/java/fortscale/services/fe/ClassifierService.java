package fortscale.services.fe;

import java.util.List;

import fortscale.services.IClassifierScoreDistribution;

public interface ClassifierService {
	public Classifier getClassifier(String classifierId);
	public List<IClassifierScoreDistribution> getScoreDistribution(String classifierId); 
	public List<ISuspiciousUserInfo> getSuspiciousUsers(String classifierId, String severityId);
}
