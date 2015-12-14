package fortscale.services.analyst;

import fortscale.domain.analyst.ScoreConfiguration;
import fortscale.services.impl.SeverityElement;

import java.util.List;

public interface ConfigurationService {
	public ScoreConfiguration getScoreConfiguration();
	public void setScoreConfiguration(ScoreConfiguration scoreConfiguration, String createById, String createdByUsername);
	public List<SeverityElement> getSeverityElements();
	public void setScoreDistribution(String scoreDistribution);
	
}
