package fortscale.services.analyst;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.Range;

import fortscale.domain.analyst.Analyst;
import fortscale.domain.analyst.ScoreConfiguration;
import fortscale.services.fe.Classifier;
import fortscale.services.impl.SeverityElement;

public interface ConfigurationService {
	public ScoreConfiguration getScoreConfiguration();
	public void setScoreConfiguration(ScoreConfiguration scoreConfiguration, Analyst createdBy);
	public void setScoreConfiguration(ScoreConfiguration scoreConfiguration, String createById, String createdByUsername);
	public List<SeverityElement> getSeverityElements();
	public void setScoreDistribution(String scoreDistribution);
	public void setScoreDistribution(List<SeverityElement> severityList);
	public Map<String, Classifier> getClassifiersMap();
	public Range getRange(String severityId);
	
}
