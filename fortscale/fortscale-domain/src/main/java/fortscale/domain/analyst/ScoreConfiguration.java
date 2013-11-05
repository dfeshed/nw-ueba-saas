package fortscale.domain.analyst;

import java.util.HashMap;
import java.util.Map;

public class ScoreConfiguration {
	Map<String, ScoreWeight> confMap;

	public Map<String, ScoreWeight> getConfMap() {
		return confMap;
	}

	public void setConfMap(Map<String, ScoreWeight> confMap) {
		this.confMap = confMap;
	}
	
	public void addScoreWeight(ScoreWeight scoreWeight){
		if(confMap == null){
			confMap = new HashMap<>();
		}
		this.confMap.put(scoreWeight.getId(), scoreWeight);
	}
}
