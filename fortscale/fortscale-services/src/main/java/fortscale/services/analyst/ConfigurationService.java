package fortscale.services.analyst;

import fortscale.domain.analyst.Analyst;
import fortscale.domain.analyst.ScoreConfiguration;

public interface ConfigurationService {
	public ScoreConfiguration getScoreConfiguration();
	public void setScoreConfiguration(ScoreConfiguration scoreConfiguration, Analyst createdBy);
	public void setScoreConfiguration(ScoreConfiguration scoreConfiguration, String createById, String createdByUsername);
}
