package fortscale.streaming.scorer;

import static com.google.common.base.Preconditions.checkNotNull;
import static fortscale.streaming.ConfigUtils.getConfigStringList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.samza.config.Config;

public abstract class ScorerContainer implements Scorer{

	protected ScorerContainerConfig scorerContainerConfig;
	protected List<Scorer> scorers = new ArrayList<>();
	
	public ScorerContainer(String scoreName, Config config){
		List<String> scorers = getConfigStringList(config, String.format("fortscale.score.%s.scorers",scoreName));
		scorerContainerConfig = new ScorerContainerConfig(scoreName, scorers);
	}

	@Override
	public void afterPropertiesSet(Map<String, Scorer> scorerMap) {
		for(String scorerName: scorerContainerConfig.scorers){
			Scorer scorer = scorerMap.get(scorerName);
			checkNotNull(scorer);
			scorers.add(scorer);
		}

	}
}
