package fortscale.streaming.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.samza.config.Config;
import org.springframework.stereotype.Service;

import fortscale.ml.service.ModelService;

@Service
public class ScorerFactoryService {

	private Map<String, ScorerFactory> scorerFactoryMap = new HashMap<>();
	
	public void register(String key, ScorerFactory scorer){
		scorerFactoryMap.put(key, scorer);
	}
	
	public ScorerFactory getScorerFactory(String key){
		return scorerFactoryMap.get(key);
	}
	
	public Scorer getScorer(String key, String scoreName, Config config, ModelService modelService){
		return getScorerFactory(key).getScorer(scoreName, config, modelService);
	}
}
