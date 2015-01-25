package fortscale.streaming.scorer;

import static com.google.common.base.Preconditions.checkNotNull;
import static fortscale.streaming.ConfigUtils.getConfigStringList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.samza.config.Config;

public abstract class ScorerContainer extends AbstractScorer{

	protected List<String> scorersStr;
	protected List<Scorer> scorers = new ArrayList<>();
	
	public ScorerContainer(String scorerName, Config config){
		super(scorerName,config);
		scorersStr = getConfigStringList(config, String.format("fortscale.score.%s.scorers",scorerName));
	}

	@Override
	public void afterPropertiesSet(Map<String, Scorer> scorerMap) {
		for(String scorerName: scorersStr){
			Scorer scorer = scorerMap.get(scorerName);
			checkNotNull(scorer);
			scorers.add(scorer);
		}

	}
}
