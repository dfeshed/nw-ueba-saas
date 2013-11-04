package fortscale.domain.analyst;

import java.util.Map;

public class ScoreConfiguration {
	Map<String, ScoreWeight> confMap;

	public Map<String, ScoreWeight> getConfMap() {
		return confMap;
	}

	public void setConfMap(Map<String, ScoreWeight> confMap) {
		this.confMap = confMap;
	}
}
