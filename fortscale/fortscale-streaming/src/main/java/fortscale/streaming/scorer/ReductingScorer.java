package fortscale.streaming.scorer;

import static com.google.common.base.Preconditions.checkNotNull;
import static fortscale.streaming.ConfigUtils.getConfigString;

import org.apache.samza.config.Config;

public class ReductingScorer extends AbstractScorer {
	
	private Scorer mainScorer;
	private Scorer reductingScorer;
	private double reducting;

	public ReductingScorer(String scorerName, Config config, ScorerContext context) {
		super(scorerName, config);
		mainScorer = getScorer(String.format("fortscale.score.%s.main.scorer", scorerName), config, context);
		reductingScorer = getScorer(String.format("fortscale.score.%s.reducting.scorer", scorerName), config, context);
		reducting = config.getDouble(String.format("fortscale.score.%s.reducting", scorerName));
	}
	
	private Scorer getScorer(String scorerNamePath, Config config, ScorerContext context){
		String scorerName = getConfigString(config, scorerNamePath);
		Scorer scorer = (Scorer) context.resolve(Scorer.class, scorerName);
		checkNotNull(scorer);
		return scorer;
	}
	
	@Override
	public Double calculateScore(EventMessage eventMessage) throws Exception {
		Double ret = mainScorer.calculateScore(eventMessage);
		if(ret != null){
			Double reducingScore = reductingScorer.calculateScore(eventMessage);
			if(reducingScore != null && reducingScore < ret){
				ret = reducingScore * reducting + ret * (1-reducting);
			}
		}
		
		eventMessage.setScore(outputFieldName, ret);
		
		return ret;
	}

}
