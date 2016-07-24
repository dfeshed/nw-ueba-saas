package fortscale.ml.scorer;

import fortscale.common.event.Event;
import fortscale.domain.core.FeatureScore;
import org.springframework.util.Assert;

import java.util.Collections;


public class ScoreMapper extends AbstractScorer {
	private Scorer baseScorer;
	private ScoreMapping.ScoreMappingConf scoreMappingConf;

	public ScoreMapper(String name, Scorer baseScorer, ScoreMapping.ScoreMappingConf scoreMappingConf) {
		super(name);
		Assert.notNull(baseScorer);
		Assert.notNull(scoreMappingConf);
		this.baseScorer = baseScorer;
		this.scoreMappingConf = scoreMappingConf;
	}

	@Override
	public FeatureScore calculateScore(Event eventMessage, long eventEpochTimeInSec) throws Exception {
		FeatureScore baseScore = baseScorer.calculateScore(eventMessage, eventEpochTimeInSec);
		double mappedScore = ScoreMapping.mapScore(baseScore.getScore(), scoreMappingConf);
		return new FeatureScore(getName(), mappedScore, Collections.singletonList(baseScore));
	}
}
