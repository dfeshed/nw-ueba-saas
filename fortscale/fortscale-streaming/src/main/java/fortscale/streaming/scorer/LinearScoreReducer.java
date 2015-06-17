package fortscale.streaming.scorer;

import static com.google.common.base.Preconditions.checkNotNull;
import static fortscale.streaming.ConfigUtils.getConfigString;

import java.util.ArrayList;
import java.util.List;

import org.apache.samza.config.Config;
import org.springframework.util.Assert;

public class LinearScoreReducer extends AbstractScorer{
	
	private Scorer reducedScorer;
	private double reductingWeight;

	public LinearScoreReducer(String scorerName, Config config, ScorerContext context) {
		super(scorerName, config, context);
		reducedScorer = getScorer(String.format("fortscale.score.%s.reduced.scorer", scorerName), config, context);
		reductingWeight = config.getDouble(String.format("fortscale.score.%s.reducting.weight", scorerName));
		Assert.isTrue(reductingWeight>0);
		Assert.isTrue(reductingWeight<1);
	}
	
	private Scorer getScorer(String scorerNamePath, Config config, ScorerContext context){
		String scorerName = getConfigString(config, scorerNamePath);
		Scorer scorer = (Scorer) context.resolve(Scorer.class, scorerName);
		checkNotNull(scorer);
		return scorer;
	}

	@Override
	public FeatureScore calculateScore(EventMessage eventMessage) throws Exception {
		FeatureScore featureScore = null;
		FeatureScore inputScore = reducedScorer.calculateScore(eventMessage);
		if(inputScore != null){
			List<FeatureScore> featureScores = new ArrayList<>();
			featureScores.add(inputScore);
			double score = inputScore.getScore()*reductingWeight;
			featureScore = new FeatureScore(outputFieldName, score, featureScores);
		}
		
		return featureScore;
	}

}
