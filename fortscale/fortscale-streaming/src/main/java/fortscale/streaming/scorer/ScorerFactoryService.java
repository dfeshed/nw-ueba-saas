package fortscale.streaming.scorer;

import java.util.HashMap;
import java.util.Map;

import org.apache.samza.config.Config;
import org.springframework.stereotype.Service;

@Service
public class ScorerFactoryService {

	private Map<String, ScorerFactory> scorerFactoryMap = new HashMap<>();

	public void register(String key, ScorerFactory scorer){
		scorerFactoryMap.put(key, scorer);
	}

	public ScorerFactory getScorerFactory(String key){
		return scorerFactoryMap.get(key);
	}

	public Scorer getScorer(String scorerType, String scorerName, Config config, ScorerContext context){
		return getScorerFactory(scorerType).getScorer(scorerName, config, context);
	}
}
