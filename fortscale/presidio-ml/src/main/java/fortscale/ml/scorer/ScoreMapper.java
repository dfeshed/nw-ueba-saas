package fortscale.ml.scorer;

import fortscale.domain.feature.score.FeatureScore;
import org.springframework.util.Assert;
import presidio.ade.domain.record.AdeRecordReader;

import java.util.Collections;

public class ScoreMapper extends AbstractScorer {
	private Scorer baseScorer;
	private ScoreMapping.ScoreMappingConf scoreMappingConf;

	public ScoreMapper(String name, Scorer baseScorer, ScoreMapping.ScoreMappingConf scoreMappingConf) {
		super(name);
		Assert.notNull(baseScorer, "Base scorer cannot be null.");
		Assert.notNull(scoreMappingConf, "Score mapping conf cannot be null.");
		this.baseScorer = baseScorer;
		this.scoreMappingConf = scoreMappingConf;
	}

	@Override
	public FeatureScore calculateScore(AdeRecordReader adeRecordReader) {
		FeatureScore baseScore = baseScorer.calculateScore(adeRecordReader);
		double mappedScore = ScoreMapping.mapScore(baseScore.getScore(), scoreMappingConf);
		return new FeatureScore(getName(), mappedScore, Collections.singletonList(baseScore));
	}
}
