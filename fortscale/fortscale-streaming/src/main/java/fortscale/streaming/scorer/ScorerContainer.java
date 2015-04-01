package fortscale.streaming.scorer;

import static com.google.common.base.Preconditions.checkNotNull;
import static fortscale.streaming.ConfigUtils.getConfigStringList;

import java.util.ArrayList;
import java.util.List;

import org.apache.samza.config.Config;

public abstract class ScorerContainer extends AbstractScorer{

	protected List<Scorer> scorers = new ArrayList<>();
	
	public ScorerContainer(String scorerName, Config config, ScorerContext context){
		super(scorerName,config, context);
		List<String> scorersStr = getConfigStringList(config, String.format("fortscale.score.%s.scorers",scorerName));
		for(String elem: scorersStr){
			Scorer scorer = (Scorer) context.resolve(Scorer.class, elem);
			checkNotNull(scorer);
			scorers.add(scorer);
		}
	}
}
